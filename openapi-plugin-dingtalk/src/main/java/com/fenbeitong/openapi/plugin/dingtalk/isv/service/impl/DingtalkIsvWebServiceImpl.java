package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.common.util.DdConfigSign;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkJsapiSignRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvWebService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.support.common.dto.UserCenterResponse;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcEmployeeSelfInfoResponse;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Map;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkContactClientUtils;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkOauthClientUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResultEntity;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * 钉钉H5或WEB后台的一些服务
 *
 * @author lizhen
 * @date 2021/1/12
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkIsvWebServiceImpl implements IDingtalkIsvWebService {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private DingtalkOauthClientUtils dingtalkOauthClientUtils;

    @Autowired
    private DingtalkContactClientUtils dingtalkContactClientUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${host.usercenter}")
    private String ucHost;

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Override
    public DingtalkJsapiSignRespDTO getJsapiSign(UserComInfoVO user, String data) {
        Map<String, Object> map = JsonUtils.toObj(data, Map.class);
        String url = StringUtils.obj2str(map.get("url"));
        if (user == null) {
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED));
        }
        String companyId = user.getCompany_id();
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCompanyId(companyId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED));
        }
        String corpId = dingtalkIsvCompany.getCorpId();
        String jsapiTicket = getJsapiTicket(corpId);
        String noncestr = DdConfigSign.getRandomStr(32);
        Long timeStamp = System.currentTimeMillis();
        Long agentid = dingtalkIsvCompany.getAgentid();
        String sign = null;
        try {
            sign = DdConfigSign.sign(jsapiTicket, noncestr, timeStamp, url);
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR));
        }
        return DingtalkJsapiSignRespDTO.builder().agentId(agentid).corpId(corpId).nonceStr(noncestr).timeStamp(timeStamp).signature(sign).url(url).build();
    }


    public String getJsapiTicket(String corpId) {
        String url = dingtalkHost + "get_jsapi_ticket";
        OapiGetJsapiTicketRequest request = new OapiGetJsapiTicketRequest();
        request.setHttpMethod("GET");
        OapiGetJsapiTicketResponse response = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return response.getTicket();
    }

    /**
     *
     * @param code 临时授权码
     * @return
     */
    @Override
    public DingtalkResultEntity getUserInfo(String code) {
        try {
            String userAccessToken = dingtalkOauthClientUtils.getUserAccessToken(code);
            GetUserResponseBody userInfo = dingtalkContactClientUtils.getUserInfo(userAccessToken);
            String dingtalkContactUserKey = MessageFormat.format(RedisKeyConstant.DINGTALK_CONTACT_USRT_KEY, userInfo.getUnionId() );
            redisTemplate.opsForValue().set(dingtalkContactUserKey, userInfo.getMobile());
            redisTemplate.expire(dingtalkContactUserKey, 1, TimeUnit.DAYS);
            return DingtalkResponseUtils.success( userInfo );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DingtalkResponseUtils.error( -9999  , "查询用户信息失败");
    }

    @Override
    public DingtalkResultEntity updateMobile(String token , String unionId) {
        String dingtalkContactUserKey = MessageFormat.format(RedisKeyConstant.DINGTALK_CONTACT_USRT_KEY, unionId );
        String userMobile = (String) redisTemplate.opsForValue().get( dingtalkContactUserKey );
        if(!StringUtils.isBlank( userMobile )){
            //调用更新手机号接口，更新手机号
            return syncPhone(token, userMobile);
        }
        return DingtalkResponseUtils.error( 500  , "手机号码为空，更新手机号码失败，请重新授权！");
    }

    /**
     *
     * @param token
     * @param phone
     * @return
     */
    public DingtalkResultEntity syncPhone (String token , String phone ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        Map<String,String> map = new HashMap<>();
        map.put("phoneNum" , phone);
        String result = RestHttpUtils.postJson(ucHost + "/uc/employee/third/sync_phone", httpHeaders, JsonUtils.toJson( map ));
        BaseDTO baseResult = JsonUtils.toObj(result, BaseDTO.class);
        if (baseResult == null || !baseResult.success()) {
            String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
           return DingtalkResponseUtils.error( baseResult.getCode()  , msg );
        }
        return DingtalkResponseUtils.success( baseResult.getData() );
    }
}
