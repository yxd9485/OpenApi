package com.fenbeitong.openapi.plugin.wechat.eia.service.wechat;

import com.fenbeitong.finhub.common.utils.FinhubLogger;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpAppDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.UserInfoResponse;
import com.fenbeitong.openapi.plugin.wechat.common.exception.AesException;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.common.util.SHA1;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WeChatRedisKeyConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.WeChatEiaGetUserDetailResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.*;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.luastar.swift.base.net.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * web前端业务
 * Created by xiaohai on 2021/11/30.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatWebService {

    /**
     * 获取企业jsapi
     */
    private static final String GET_ENTERPRISE_JSAPI_TICKET_URL = "/cgi-bin/get_jsapi_ticket?access_token={0}";

    /**
     * 获取企业jsapi
     */
    private static final String GET_AGENT_JSAPI_TICKET_URL = "/cgi-bin/ticket/get?access_token={0}&type=agent_config";

    /**
     * 获取企业jsapi_invoice
     */
    private static final String GET_AGENT_JSAPI_TICKET_INVOICE_URL = "/cgi-bin/ticket/get?access_token={0}&type=wx_card";

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private WechatTokenService wechatTokenService;

    @Autowired
    protected PluginCorpAppDefinitionDao pluginCorpAppDefinitionDao;

    @Autowired
    private WeChatUserAuthService weChatUserAuthService;



    /**
     * 获取企业jsapi签名
     * @param companyId
     * @param data
     * @return
     * @throws AesException
     */
    public WeChatIsvJsapiSignResponse getEnterpriseJsapiSign(String companyId, String data) throws AesException {
        log.info("web获取jsapiEnterpriseSign,companyId={},data={}", companyId, data);
        if(StringUtils.isBlank( data )){
            throw new OpenApiArgumentException("url不能为空！");
        }
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        if (corpDefinition == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ID_NOT_EXIST));
        }
        String corpId = corpDefinition.getThirdCorpId();
        String ticketKey = WeChatRedisKeyConstant.WECHAT_EIA_ENTERPRISE_JSAPI_TICKET;
        String ticket = getJsapiTicket(  ticketKey ,  GET_ENTERPRISE_JSAPI_TICKET_URL ,   corpId);
        return setWeChatSignResp(ticket, data, corpId);
    }

    /**
     * 获取应用的jsapi签名
     * @param companyId
     * @param data
     * @return
     * @throws AesException
     */
    public WeChatIsvJsapiSignResponse getAgentJsapiSign(String companyId, String data) throws AesException {
        log.info("web获取jsapiAgent Sign,companyId={},data={}", companyId, data);
        if(StringUtils.isBlank( data )){
            throw new OpenApiArgumentException("url不能为空！");
        }
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        if (corpDefinition == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ID_NOT_EXIST));
        }
        String corpId = corpDefinition.getThirdCorpId();
        String ticketKey = WeChatRedisKeyConstant.WECHAT_EIA_AGENT_JSAPI_TICKET;
        String ticket = getJsapiTicket(  ticketKey ,  GET_AGENT_JSAPI_TICKET_URL ,   corpId);
        return setWeChatSignResp(ticket, data, corpId);
    }

    /**
     * 获取应用的jsapi签名
     * @param companyId
     * @param data
     * @return
     * @throws AesException
     */
    public WeChatIsvJsapiSignResponse getAgentJsapiSignInvoice(String companyId, String data) throws AesException {
        log.info("web获取jsapiAgent invoice Sign,companyId={},data={}", companyId, data);
        if(StringUtils.isBlank( data )){
            throw new OpenApiArgumentException("url不能为空！");
        }
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        if (corpDefinition == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ID_NOT_EXIST));
        }
        String corpId = corpDefinition.getThirdCorpId();
        String ticketKey = WeChatRedisKeyConstant.WECHAT_EIA_AGENT_JSAPI_TICKET_INVOICE;
        String ticket = getJsapiTicket(  ticketKey ,  GET_AGENT_JSAPI_TICKET_INVOICE_URL ,   corpId);
        return getInvoiceSign(ticket, data, corpId);
    }

    /**
     * 获取应用invoce签名
     *
     * @param
     * @return
     * @throws AesException
     */
    public WeChatIsvJsapiSignResponse getInvoiceSign(String ticket , String data ,String corpId) throws AesException {
        PluginCorpAppDefinition appPluginCorpApp = pluginCorpAppDefinitionDao.getByCorpId(corpId);
        String noncestr = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String[] array = new String[]{ticket, timestamp, noncestr, data, corpId};
        String sign = SHA1.getSHA1(array);
        WeChatIsvJsapiSignResponse weChatIsvJsapiSignResponse = new WeChatIsvJsapiSignResponse();
        weChatIsvJsapiSignResponse.setCorpId(corpId);
        weChatIsvJsapiSignResponse.setNonceStr(noncestr);
        weChatIsvJsapiSignResponse.setTimestamp(timestamp);
        weChatIsvJsapiSignResponse.setSignature(sign);
        weChatIsvJsapiSignResponse.setAgentId(NumericUtils.obj2int(appPluginCorpApp.getThirdAgentId()));
        return weChatIsvJsapiSignResponse;
    }



    /**
     * 获取jsapiticket(内嵌版)
     * @param jsapiTicketkey
     * @param jsapiUrl
     * @param corpId
     * @return
     */
    public String getJsapiTicket( String jsapiTicketkey , String jsapiUrl ,  String corpId) {
        // 先尝试从redis查询
        String ticketKey = MessageFormat.format(WeChatRedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, MessageFormat.format( jsapiTicketkey , corpId));
        String ticketValue = (String) redisTemplate.opsForValue().get( ticketKey );
        if (!StringUtils.isBlank( ticketValue )) {
            return ticketValue;
        }
        // 未命中缓存， 重新请求
        String accessToken = wechatTokenService.getWeChatContactTokenByCorpId(corpId);
        String ticketUrl = wechatHost + MessageFormat.format( jsapiUrl , accessToken);
        String jsapiTicketInfo = HttpClientUtils.get( ticketUrl );
        FinhubLogger.info("根据企业ID查询jsapi_ticket返回结果 {}", jsapiTicketInfo);
        WeChatIsvJsapiTicketResponse weChatJsapiTicketResponse = JsonUtils.toObj(jsapiTicketInfo, WeChatIsvJsapiTicketResponse.class);
        if (weChatJsapiTicketResponse == null || StringUtils.isBlank( weChatJsapiTicketResponse.getTicket() )) {
            log.info("wechat eia getJsapiTicket error:{}", jsapiTicketInfo);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_ENTERPRISE_JSAPI_TICKET_IS_NULL));
        }
        ticketValue = weChatJsapiTicketResponse.getTicket();
        // 缓存redis
        log.info("wechat eaia save enterpriseJsapiTicket,key={},value={}", ticketKey , ticketValue);
        redisTemplate.opsForValue().set(ticketKey, ticketValue);
        // 有效期7200秒，设置7000秒过期，防止不可用
        int expiresIn =  weChatJsapiTicketResponse.getExpiresIn() - 200;
        int expiresInTime =  expiresIn <= 0 ? weChatJsapiTicketResponse.getExpiresIn() : expiresIn;
        redisTemplate.expire( ticketKey, expiresInTime, TimeUnit.SECONDS);
        return ticketValue;
    }

    private WeChatIsvJsapiSignResponse setWeChatSignResp( String ticket , String url ,String corpId) throws AesException{
        String noncestr = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String timestampSign = "timestamp=" + timestamp + "&";
        String ticketSign = "jsapi_ticket=" + ticket + "&";
        String noncestrSign = "noncestr="+ noncestr + "&";
        String urlSign = "url=" + url;
        String sign = SHA1.getSHA1( ticketSign , timestampSign, noncestrSign, urlSign);
        PluginCorpAppDefinition appPluginCorpApp = pluginCorpAppDefinitionDao.getByCorpId(corpId);
        WeChatIsvJsapiSignResponse weChatIsvJsapiSignResponse = new WeChatIsvJsapiSignResponse();
        weChatIsvJsapiSignResponse.setCorpId(corpId);
        weChatIsvJsapiSignResponse.setNonceStr(noncestr);
        weChatIsvJsapiSignResponse.setTimestamp(timestamp);
        weChatIsvJsapiSignResponse.setSignature(sign);
        weChatIsvJsapiSignResponse.setAgentId(NumericUtils.obj2int(appPluginCorpApp.getThirdAgentId()));
        return weChatIsvJsapiSignResponse;
    }

    /**
     * 查询微信发票信息
     *
     * @param cardId
     * @param encryptCode
     * @param corpId
     */
    public String getInvoiceInfo(String cardId, String encryptCode, String corpId) {
        String url = wechatHost + WeChatIsvConstant.GET_INVOICE_INFO_URL;
        WeChatIsvInvoiceRequest weChatIsvInvoiceRequest = new WeChatIsvInvoiceRequest();
        weChatIsvInvoiceRequest.setCardId(cardId);
        weChatIsvInvoiceRequest.setEncryptCode(encryptCode);
        String accessToken = wechatTokenService.getWeChatContactTokenByCorpId(corpId);
        String invoiceUrl =  url + accessToken;
        String weChatInvoiceInfo = RestHttpUtils.postJson( invoiceUrl , JsonUtils.toJson(weChatIsvInvoiceRequest) );
        return weChatInvoiceInfo;
    }

    /**
     *
     * 1 根据临时授权码获取用户敏感信息
     * 2 更新员工手机号
     *
     * @param corpId 三方企业id（服务商加密后）
     * @param tempCode 临时授权码
     */
    public void updateUserPhone(String corpId, String tempCode) {
        //根据临时授权码获取成员票据（userTicket）
        UserInfoResponse thirdUserInfo = weChatUserAuthService.getUserInfoByCode(tempCode, corpId);
        // 如果用户拒绝授权，user_ticket为空，直接抛异常。避免user_ticket为空时，直接调用企业微信接口
        if (StringUtils.isBlank(thirdUserInfo.getUserTicket())){
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_WECHAT_USER_MOBILE_FAIL));
        }
        //根据成员票据获取用户敏感信息
        WeChatEiaGetUserDetailResponse userDetailResponse = weChatUserAuthService.getUserDetailByTicket(thirdUserInfo.getUserTicket(), corpId);
        if (StringUtils.isBlank(userDetailResponse.getMobile())){
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_EIA_GET_WECHAT_USER_MOBILE_FAIL));
        }
        // 查询dingtalk_corp中企业配置信息
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.warn("公司信息未配置 , 请检查 dingtalk_corp，corpId:{}",corpId);
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_INFO_NOT_EXIST));
        }
        // 更新手机号
        weChatUserAuthService.syncEmployeePhone(pluginCorpDefinition.getAppId(),userDetailResponse);

    }
}
