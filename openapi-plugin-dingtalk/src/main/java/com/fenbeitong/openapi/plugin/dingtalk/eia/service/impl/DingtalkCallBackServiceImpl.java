package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiCallBackDeleteCallBackRequest;
import com.dingtalk.api.request.OapiCallBackGetCallBackRequest;
import com.dingtalk.api.request.OapiCallBackRegisterCallBackRequest;
import com.dingtalk.api.request.OapiCallBackUpdateCallBackRequest;
import com.dingtalk.api.response.OapiCallBackDeleteCallBackResponse;
import com.dingtalk.api.response.OapiCallBackGetCallBackResponse;
import com.dingtalk.api.response.OapiCallBackRegisterCallBackResponse;
import com.dingtalk.api.response.OapiCallBackUpdateCallBackResponse;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCallBackService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ServiceAspect
@Service
@Slf4j
public class DingtalkCallBackServiceImpl implements IDingtalkCallBackService {

    @Value("${dingtalk.callback.token}")
    private String dingtalkCallbackToken;
    @Value("${dingtalk.callback.aeskey}")
    private String dingtalkCallbackAeskey;

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Autowired
    IDingtalkCorpService dingtalkCorpService;
    @Autowired
    IApiTokenService dingtalkTokenService;
    @Autowired
    private IDingtalkRouteService dingtalkRouteService;
    /**
     * 钉钉回调事件注册
     *
     * @param corpId       钉钉corpId
     * @param callbackTags 回调事件列表 {@link }
     */
    public void register(String corpId,  String[] callbackTags, String callbackDomain) {

//        checkParams(corpId, callbackTags);

        log.info("调用钉钉注册回调事件接口，参数: corpId: {}, , callbackTags: {}, callbackDomain: {}", corpId, callbackTags, callbackDomain);

        String accessToken = dingtalkTokenService.getAccessToken(corpId);
//        String proxyUrl = getProxyUrl(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/call_back/register_call_back");
        OapiCallBackRegisterCallBackRequest request = new OapiCallBackRegisterCallBackRequest();
        request.setUrl(getCallbackUrl(callbackDomain, corpId));
        request.setAesKey(dingtalkCallbackAeskey);
        request.setToken(dingtalkCallbackToken);
        request.setCallBackTag(Arrays.asList(callbackTags));
        try {
            OapiCallBackRegisterCallBackResponse response = client.execute(request, accessToken);
            log.info("调用钉钉注册回调事件接口完成，返回结果: {}", response.getBody());
            if (!response.isSuccess()) {
                throw new FinhubException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR), 0,response.getErrmsg());
            }
        } catch (ApiException e) {
            log.error("调用钉钉注册回调事件接口异常", e);
            throw new FinhubException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR), 0,"调用钉钉接口异常");
        }
    }

    /**
     * 钉钉回调事件更新
     *
     * @param corpId  corpId
     * @param callbackTags 回调事件列表 {@link }
     */
    public void update(String corpId, String[] callbackTags, String callbackDomain) {

//        checkParams(corpId, callbackTags);

        log.info("调用钉钉更新回调事件接口，参数: corpId: {}, callbackTags: {}, callbackDomain: {}", corpId, callbackTags, callbackDomain);

        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/call_back/update_call_back");
        OapiCallBackUpdateCallBackRequest request = new OapiCallBackUpdateCallBackRequest();
        request.setUrl(getCallbackUrl(callbackDomain, corpId));
        request.setAesKey(dingtalkCallbackAeskey);
        request.setToken(dingtalkCallbackToken);
        request.setCallBackTag(Arrays.asList(callbackTags));
        try {
            OapiCallBackUpdateCallBackResponse response = client.execute(request, accessToken);
            log.info("调用钉钉更新回调事件接口完成，返回结果: {}", response.getBody());
            if (!response.isSuccess()) {
                throw new FinhubException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR),0,response.getErrmsg());
            }
        } catch (ApiException e) {
            log.error("调用钉钉更新回调事件接口异常", e);
            throw new FinhubException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR), 0,"调用钉钉接口异常");

        }
    }

    /**
     *  删除回调
     * @param corpId 钉钉企业corpId
     */
    public void delete(String corpId) {

//        CheckUtils.checkEmpty(corpId, "corpId 不能为空");

        log.info("调用钉钉删除回调事件接口，参数: corpId: {}", corpId);

        String accessToken = dingtalkTokenService.getAccessToken(corpId);
//        String proxyUrl = getProxyUrl(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient  client = new DefaultDingTalkClient(proxyUrl + "/call_back/delete_call_back");
        OapiCallBackDeleteCallBackRequest request = new OapiCallBackDeleteCallBackRequest();
        request.setHttpMethod("GET");
        try {
            OapiCallBackDeleteCallBackResponse response = client.execute(request, accessToken);
            log.info("调用钉钉删除回调事件接口完成，返回结果: {}", response.getBody());
            if (!response.isSuccess()) {
                throw new FinhubException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR),0,response.getErrmsg());
            }
        } catch (ApiException e) {
            log.error("调用钉钉删除回调事件接口异常", e);
            throw new FinhubException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR),0,"调用钉钉接口异常");
        }
    }

    /**
     * 查询已注册的回调列表
     * @param corpId 钉钉企业corpId
     */
    public List<String> list(String corpId) {
//        CheckUtils.checkEmpty(corpId, "corpId 不能为空");
        log.info("调用钉钉查询回调事件列表接口，参数: corpId: {}", corpId);

        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient  client = new DefaultDingTalkClient(proxyUrl + "/call_back/get_call_back");
        OapiCallBackGetCallBackRequest request = new OapiCallBackGetCallBackRequest();
        request.setHttpMethod("GET");
        try {
            OapiCallBackGetCallBackResponse response = client.execute(request,accessToken);
            log.info("调用钉钉查询回调列表接口完成，返回结果: {}", response.getBody());
            if (!response.isSuccess() && response.getErrcode().equals(DingtalkResponseCode.CALLBACK_NOT_EXISTS)) {
                return new ArrayList<>();
            }
            if (!response.isSuccess()) {
                throw new FinhubException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR),0,"调用钉钉接口异常");

            }
            return response.getCallBackTag();
        } catch (ApiException e) {
            log.error("调用钉钉查询回调列表接口异常", e);
            throw new FinhubException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR),0,"调用钉钉接口异常");
        }
    }

//    private void checkParams(String corpId,  String[] callbackTags) {
//        CheckUtils.create()
//                .addCheckEmpty(corpId, "corpId 不能为空")
//                .addCheckNull(callbackTags, "callbackTags 不能为空")
//                .check();
//    }

    /**
     * 获取回调地址
     * 回调域名优先级  参数传入 > 数据库dingtalk_corp表配置 > 系统默认配置域名
     * @param callbackDomain callbackDomain
     * @param corpId corpId
     * @return
     */
    private String getCallbackUrl(String callbackDomain, String corpId) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(callbackDomain)) {

            PluginCorpDefinition byCorpId = dingtalkCorpService.getByCorpId(corpId);
            String customCallback = byCorpId.getCallbackHost();
            if (StringUtils.isNotEmpty(customCallback)) {
                callbackDomain = customCallback;
            } else {
                callbackDomain = dingtalkHost;
            }
        }
        String callbackUrl =  callbackDomain + "/openapi/dingtalk/callback/receive?corpId=" + corpId;
        return callbackUrl;
    }

}
