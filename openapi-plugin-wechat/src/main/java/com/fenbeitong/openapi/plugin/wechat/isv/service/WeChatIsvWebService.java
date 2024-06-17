package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.support.common.dto.UserCenterResponse;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcEmployeeSelfInfoResponse;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.AesException;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.common.util.SHA1;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WeChatRedisKeyConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.*;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * web前端业务
 * Created by lizhen on 2020/3/27.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvWebService {

    private static final String GET_ENTERPRISE_JSAPI_TICKET_URL = "/cgi-bin/get_jsapi_ticket?access_token={access_token}";

    private static final String GET_AGENT_JSAPI_TICKET_URL = "/cgi-bin/ticket/get?access_token={access_token}&type={type}";

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Value("${wechat.isv.suite-id}")
    private String suiteId;

    @Value("${host.openplus}")
    private String openPlusHost;

    @Value("${host.fbtweb}")
    private String fbtWebHost;

    @Autowired
    private WeChatIsvCompanyAuthService weChatIsvCompanyAuthService;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private WeChatIsvHttpUtils wechatIsvHttpUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @DubboReference(check = false)
    private ICommonService iCommonService;

    @Autowired
    private UserCenterService userCenterService;

    /**
     * 获取token
     *
     * @param companyId
     * @return
     * @throws Exception
     */
    public Map<String, Object> getToken(String companyId) throws Exception {
        log.info("web获取token,companyId={}", companyId);
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        Map<String, Object> result = new HashMap<>();
        Map<String, String> data = new HashMap<>();
        result.put("data", data);
        String corpId = weChatIsvCompany.getCorpId();
        String accessToken = weChatIsvCompanyAuthService.getAccessTokenByCorpId(corpId);
        String suiteAccessToken = weChatIsvCompanyAuthService.getSuiteAccessToken();
        data.put("corpId", corpId);
        data.put("accessToken", accessToken);
        data.put("suiteAccessToken", suiteAccessToken);
        result.put("code", 0);
        return result;
    }

    /**
     * 获取企业jsapi签名
     *
     * @param companyId
     * @param data
     * @return
     * @throws AesException
     */
    public WeChatIsvJsapiSignResponse getEnterpriseJsapiSign(String companyId, String data) throws AesException {
        log.info("web获取jsapiEnterpriseSign,companyId={},data={}", companyId, data);
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        String corpId = weChatIsvCompany.getCorpId();
        Integer agentId = weChatIsvCompany.getAgentid();
        String noncestr = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String enterpriseJsapiTicket = getEnterpriseJsapiTicket(corpId);
        String timestampSign = "timestamp=" + timestamp + "&";
        String enterpriseJsapiTicketSign = "jsapi_ticket=" + enterpriseJsapiTicket + "&";
        String noncestrSign = "";
        if (!StringUtils.isBlank(data)) {
            //如果data不为空，data前面的数据末尾拼上"&"。不在data前面拼因为后面要排序。
            noncestrSign = "noncestr=" + noncestr + "&";
        } else {
            //防止拼进去null
            data = "";
            noncestrSign = "noncestr=" + noncestr;
        }
        String sign = SHA1.getSHA1(enterpriseJsapiTicketSign, timestampSign, noncestrSign, data);
        WeChatIsvJsapiSignResponse weChatIsvJsapiSignResponse = new WeChatIsvJsapiSignResponse();
        weChatIsvJsapiSignResponse.setCorpId(corpId);
        weChatIsvJsapiSignResponse.setNonceStr(noncestr);
        weChatIsvJsapiSignResponse.setTimestamp(timestamp);
        weChatIsvJsapiSignResponse.setSignature(sign);
        weChatIsvJsapiSignResponse.setAgentId(agentId);
        return weChatIsvJsapiSignResponse;
    }


    /**
     * 获取应用jsapi签名
     *
     * @param companyId
     * @param data
     * @return
     * @throws AesException
     */
    public WeChatIsvJsapiSignResponse getAgentJsapiSign(String companyId, String data) throws AesException {
        log.info("getAgentJsapiSign,companyId={},data={}", companyId, data);
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        String corpId = weChatIsvCompany.getCorpId();
        String noncestr = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String agentJsapiTicket = getAgentJsapiTicket(corpId);
        String timestampSign = "timestamp=" + timestamp + "&";
        String agentJsapiTicketSign = "jsapi_ticket=" + agentJsapiTicket + "&";
        String noncestrSign = "";
        if (!StringUtils.isBlank(data)) {
            //如果data不为空，data前面的数据末尾拼上"&"。不在data前面拼因为后面要排序。
            noncestrSign = "noncestr=" + noncestr + "&";
        } else {
            //防止拼进去null
            data = "";
            noncestrSign = "noncestr=" + noncestr;
        }
        String sign = SHA1.getSHA1(agentJsapiTicketSign, timestampSign, noncestrSign, data);
        Integer agentId = weChatIsvCompany.getAgentid();
        WeChatIsvJsapiSignResponse weChatIsvJsapiSignResponse = new WeChatIsvJsapiSignResponse();
        weChatIsvJsapiSignResponse.setCorpId(corpId);
        weChatIsvJsapiSignResponse.setNonceStr(noncestr);
        weChatIsvJsapiSignResponse.setTimestamp(timestamp);
        weChatIsvJsapiSignResponse.setSignature(sign);
        weChatIsvJsapiSignResponse.setAgentId(agentId);
        return weChatIsvJsapiSignResponse;
    }

    /**
     * 获取企业jsapi_ticket
     *
     * @param corpId
     * @return
     */
    public String getEnterpriseJsapiTicket(String corpId) {
        // 先尝试从redis查询
        String enterpriseJsapiTicketKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_ISV_ENTERPRISE_JSAPI_TICKET, corpId));
        String enterpriseJsapiTicket = (String) redisTemplate.opsForValue().get(enterpriseJsapiTicketKey);
        if (!StringUtils.isBlank(enterpriseJsapiTicket)) {
            return enterpriseJsapiTicket;
        }
        // 未命中缓存， 重新请求
        Map<String, String> param = new HashMap<>();
        String res = wechatIsvHttpUtil.getJsonWithAccessToken(wechatHost + GET_ENTERPRISE_JSAPI_TICKET_URL, param, corpId);
        WeChatIsvJsapiTicketResponse weChatIsvJsapiTicketResponse = (WeChatIsvJsapiTicketResponse) JsonUtils.toObj(res, WeChatIsvJsapiTicketResponse.class);
        if (weChatIsvJsapiTicketResponse == null || StringUtils.isBlank(weChatIsvJsapiTicketResponse.getTicket())) {
            log.error("wechat isv getEnterpriseJsapiTicket error:{}", res);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_ENTERPRISE_JSAPI_TICKET_IS_NULL));
        }
        enterpriseJsapiTicket = weChatIsvJsapiTicketResponse.getTicket();
        // 缓存redis
        log.info("wechat isv save enterpriseJsapiTicket,key={},value={}", enterpriseJsapiTicket, enterpriseJsapiTicket);
        redisTemplate.opsForValue().set(enterpriseJsapiTicketKey, enterpriseJsapiTicket);
        // 有效期7200秒，设置7000秒过期，防止不可用
        redisTemplate.expire(enterpriseJsapiTicketKey, 7000, TimeUnit.SECONDS);
        return enterpriseJsapiTicket;
    }

    /**
     * 获取应用jsapi_ticket
     *
     * @param corpId
     * @return
     */
    public String getAgentJsapiTicket(String corpId) {
        // 先尝试从redis查询
        String agentJsapiTicketKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_ISV_AGENT_JSAPI_TICKET, corpId));
        String agentJsapiTicket = (String) redisTemplate.opsForValue().get(agentJsapiTicketKey);
        if (!StringUtils.isBlank(agentJsapiTicket)) {
            return agentJsapiTicket;
        }
        // 未命中缓存， 重新请求
        Map<String, String> param = new HashMap<>();
        param.put("type", "agent_config");
        String res = wechatIsvHttpUtil.getJsonWithAccessToken(wechatHost + GET_AGENT_JSAPI_TICKET_URL, param, corpId);
        WeChatIsvJsapiTicketResponse weChatIsvJsapiTicketResponse = (WeChatIsvJsapiTicketResponse) JsonUtils.toObj(res, WeChatIsvJsapiTicketResponse.class);
        if (weChatIsvJsapiTicketResponse == null || StringUtils.isBlank(weChatIsvJsapiTicketResponse.getTicket())) {
            log.error("wechat isv getAgentJsapiTicket error:{}", res);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_AGENT_JSAPI_TICKET_IS_NULL));
        }
        agentJsapiTicket = weChatIsvJsapiTicketResponse.getTicket();
        // 缓存redis
        log.info("wechat isv save getAgentJsapiTicket,key={},value={}", agentJsapiTicket, agentJsapiTicket);
        redisTemplate.opsForValue().set(agentJsapiTicketKey, agentJsapiTicket);
        // 有效期7200秒，设置7000秒过期，防止不可用
        redisTemplate.expire(agentJsapiTicketKey, 7000, TimeUnit.SECONDS);
        return agentJsapiTicket;
    }

    /**
     * 获取isv试用天数
     *
     * @return
     */
    public WeChatIsvTrialDayResponse getTrialDay() {
        String trialDay = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WECHAT_ISV_TRIAL_DAY.getCode());
        WeChatIsvTrialDayResponse weChatIsvTrialDayResponse = new WeChatIsvTrialDayResponse();
        weChatIsvTrialDayResponse.setCode(0);
        WeChatIsvTrialDayResponse.WeChatIsvTrialDay weChatIsvTrialDay = new WeChatIsvTrialDayResponse.WeChatIsvTrialDay();
        weChatIsvTrialDay.setWeChatIsvTrialDay(trialDay);
        weChatIsvTrialDayResponse.setData(weChatIsvTrialDay);
        return weChatIsvTrialDayResponse;
    }

    /**
     * 查询人员部门三方id
     *
     * @param employeeAndOrgUnitThirdIdsRequest
     */
    public EmployeeAndOrgUnitThirdIdsResponse getEmployeeAndOrgUnitThirdIds(UserComInfoVO user, EmployeeAndOrgUnitThirdIdsRequest employeeAndOrgUnitThirdIdsRequest) {
        if (user == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.TOKEN_INFO_IS_ERROR));
        }
        String companyId = employeeAndOrgUnitThirdIdsRequest.getCompanyId();
        List<String> employeeList = employeeAndOrgUnitThirdIdsRequest.getEmployeeList();
        List<String> orgUnitList = employeeAndOrgUnitThirdIdsRequest.getOrgUnitList();
        EmployeeAndOrgUnitThirdIdsResponse employeeAndOrgUnitThirdIdsResponse = new EmployeeAndOrgUnitThirdIdsResponse();
        if (employeeList != null && employeeList.size() > 0) {
            //type 1：分贝id 2：第三方id, businessType 业务类型：1：部门 2：项目 3：员工
            List<CommonIdDTO> commonIdDTOS = iCommonService.queryIdDTO(companyId, employeeList, 1, 3);
            employeeAndOrgUnitThirdIdsResponse.setEmployeeList(commonIdDTOS);
        }
        if (orgUnitList != null && orgUnitList.size() > 0) {
            //type 1：分贝id 2：第三方id, businessType 业务类型：1：部门 2：项目 3：员工
            List<CommonIdDTO> commonIdDTOS = iCommonService.queryIdDTO(companyId, orgUnitList, 1, 1);
            employeeAndOrgUnitThirdIdsResponse.setOrgUnitList(commonIdDTOS);
        }
        return employeeAndOrgUnitThirdIdsResponse;
    }

    /**
     * 获取应用invoce签名
     *
     * @param companyId
     * @return
     * @throws AesException
     */
    public WeChatIsvJsapiSignResponse getInvoiceSign(String companyId, String data) throws AesException {
        log.info("getInvoiceSign,companyId={}, data={}", companyId, data);
        WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
        if (weChatIsvCompany == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
        }
        String corpId = weChatIsvCompany.getCorpId();
        String noncestr = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String ticket = getCardTicket(corpId);
        String[] array = new String[]{ticket, timestamp, noncestr, data, corpId};
        String sign = SHA1.getSHA1(array);
        Integer agentId = weChatIsvCompany.getAgentid();
        WeChatIsvJsapiSignResponse weChatIsvJsapiSignResponse = new WeChatIsvJsapiSignResponse();
        weChatIsvJsapiSignResponse.setCorpId(corpId);
        weChatIsvJsapiSignResponse.setNonceStr(noncestr);
        weChatIsvJsapiSignResponse.setTimestamp(timestamp);
        weChatIsvJsapiSignResponse.setSignature(sign);
        weChatIsvJsapiSignResponse.setAgentId(agentId);
        return weChatIsvJsapiSignResponse;
    }

    /**
     * 获取应用invoce_ticket
     *
     * @param corpId
     * @return
     */
    public String getCardTicket(String corpId) {
        // 先尝试从redis查询
        String cardTicketKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format(WeChatRedisKeyConstant.WECHAT_ISV_WX_CARD_TICKET, corpId));
        String cardTicket = (String) redisTemplate.opsForValue().get(cardTicketKey);
        if (!StringUtils.isBlank(cardTicket)) {
            return cardTicket;
        }
        // 未命中缓存， 重新请求
        Map<String, String> param = new HashMap<>();
        param.put("type", "wx_card");
        String res = wechatIsvHttpUtil.getJsonWithAccessToken(wechatHost + GET_AGENT_JSAPI_TICKET_URL, param, corpId);
        WeChatIsvJsapiTicketResponse weChatIsvJsapiTicketResponse = (WeChatIsvJsapiTicketResponse) JsonUtils.toObj(res, WeChatIsvJsapiTicketResponse.class);
        if (weChatIsvJsapiTicketResponse == null || StringUtils.isBlank(weChatIsvJsapiTicketResponse.getTicket())) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_AGENT_JSAPI_TICKET_IS_NULL));
        }
        cardTicket = weChatIsvJsapiTicketResponse.getTicket();
        // 缓存redis
        redisTemplate.opsForValue().set(cardTicketKey, cardTicket);
        redisTemplate.expire(cardTicketKey, weChatIsvJsapiTicketResponse.getExpiresIn(), TimeUnit.SECONDS);
        return cardTicket;
    }


    /**
     * @return
     */
    public String getPreAuthUrl(String redirectUri) {
        String preAuthCode = weChatIsvCompanyAuthService.getPreAuthCode();
        weChatIsvCompanyAuthService.setSessionInfo(preAuthCode);
        String url = "https://open.work.weixin.qq.com/3rdapp/install?suite_id={0}&pre_auth_code={1}&redirect_uri={2}&state=STATE";
        if (StringUtils.isBlank(redirectUri)) {
            redirectUri = fbtWebHost + WeChatIsvConstant.WECHAT_ISV_INSTALL_REDIRECT_UL;
        }
        String redirectUrl = MessageFormat.format(url, suiteId, preAuthCode, redirectUri);
        return redirectUrl;
    }

    /**
     * @return
     */
    public String getRegisterUrl() {
        String registerCode = weChatIsvCompanyAuthService.getRegisterCode();
        String redirectUrl = "https://open.work.weixin.qq.com/3rdservice/wework/register?register_code=" + registerCode;
        return redirectUrl;
    }
}
