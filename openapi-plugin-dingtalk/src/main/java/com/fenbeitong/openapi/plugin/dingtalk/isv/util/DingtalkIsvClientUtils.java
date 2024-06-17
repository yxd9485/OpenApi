package com.fenbeitong.openapi.plugin.dingtalk.isv.util;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiServiceGetCorpTokenRequest;
import com.dingtalk.api.response.OapiServiceGetCorpTokenResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoRequest;
import com.taobao.api.TaobaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.Serializable;

/**
 * @author lizhen
 * @date 2020/7/13
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvClientUtils {

    @Value("${dingtalk.isv.suitekey}")
    private String suiteKey;

    @Value("${dingtalk.isv.suiteSecret}")
    private String suiteSecret;

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public <T extends TaobaoResponse> T execute(String url, TaobaoRequest<T> req) {
        DingTalkClient client = new DefaultDingTalkClient(url);
        T response = null;
        try {
            log.info("调用钉钉url：{},参数：{}", url, JsonUtils.toJson(req));
            response = client.execute(req);
            log.info("调用钉钉接口完成，url:{}, 返回结果：{}", url, JsonUtils.toJson(response));
            if (response.isSuccess()) {
                return response;
            } else {
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, response.getMessage());
            }
        } catch (ApiException e) {
            log.error("调用钉钉获取部门列表接口异常：", e);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, e.getErrMsg());
        }
    }

    /**
     * 为Url补充suiteAccessToken
     *
     * @param url
     * @param req
     * @param <T>
     * @return
     */
    public <T extends TaobaoResponse> T executeWithSuiteAccessToken(String url, TaobaoRequest<T> req) {
        String suiteAccessToken = dingtalkIsvCompanyAuthService.getSuiteAccessToken();
        String newUrl = url + suiteAccessToken;
        DingTalkClient client = new DefaultDingTalkClient(newUrl);
        T response = null;
        try {
            log.info("调用钉钉url：{},参数：{}", newUrl, JsonUtils.toJson(req));
            response = client.execute(req);
            log.info("调用钉钉接口完成，url:{}, 返回结果：{}", newUrl, JsonUtils.toJson(response));
            if ("40082".equals(response.getErrorCode()) || "41022".equals(response.getErrorCode()) || "42009".equals(response.getErrorCode()) || "48003".equals(response.getErrorCode())) {
                dingtalkIsvCompanyAuthService.clearSuiteAccessToken();
                response = executeWithSuiteAccessToken(url, req);
            }
            if (response.isSuccess()) {
                return response;
            } else {
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, response.getMessage());
            }
        } catch (ApiException e) {
            log.error("调用钉钉获取部门列表接口异常：", e);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, e.getErrMsg());
        }
    }

    public <T extends TaobaoResponse> T executeWithSuiteInfo(String url, TaobaoRequest<T> request) {
        // redis未命中， 重新获取
//        String suiteTicketKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, CacheConstant.DINGTALK_ISV_SUITE_TICKET);
//        String suiteTicket = (String) redisTemplate.opsForValue().get(suiteTicketKey);
//        if (StringUtils.isBlank(suiteTicket)) {
//            log.error("【dingtalk isv】 , suiteTicket获取失败");
//            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_SUITE_TICKET_IS_NULL);
//        }
        String suiteTicket = dingtalkIsvCompanyAuthService.getSuiteTicket();
        DefaultDingTalkClient client = new DefaultDingTalkClient(url);
        T response = null;
        try {
            log.info("调用钉钉url：{},参数：{}", url, JsonUtils.toJson(request));
            response = client.execute(request, suiteKey, suiteSecret, suiteTicket);
            log.info("调用钉钉接口完成，url:{}, 返回结果：{}", url, JsonUtils.toJson(response));
            if (response.isSuccess()) {
                return response;
            } else {
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, response.getMessage());
            }
        } catch (ApiException e) {
            log.error("调用钉钉获取部门列表接口异常：", e);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, e.getErrMsg());
        }
    }

    /**
     * 传corpAccessToken的请求
     *
     * @param url
     * @param req
     * @param corpId
     * @param <T>
     * @return
     */
    public <T extends TaobaoResponse> T executeWithCorpAccesstoken(String url, TaobaoRequest<T> req, String corpId) {
        String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId(corpId);
        DingTalkClient client = new DefaultDingTalkClient(url);
        T response = null;
        try {
            log.info("调用钉钉url：{},参数：{}", url, JsonUtils.toJson(req));
            response = client.execute(req, accessToken);
            log.info("调用钉钉接口完成，url:{}, 返回结果：{}", url, JsonUtils.toJson(response));
            if ("33001".equals(response.getErrorCode()) || "40014".equals(response.getErrorCode()) || "41001".equals(response.getErrorCode()) || "42001".equals(response.getErrorCode())) {
                dingtalkIsvCompanyAuthService.clearCorpAccessToken(corpId);
                response = executeWithCorpAccesstoken(url, req, corpId);
            }
            // 50002请求的员工userid不在授权范围内
            // 50004请求的部门id不在授权范围内
            // 60121人员不存在fd
            // 40009不合法的部门id
            if (response.isSuccess() || response.getErrorCode().equals("50002") || response.getErrorCode().equals("50004") || response.getErrorCode().equals("60121") || response.getErrorCode().equals("40009")) {
                return response;
            } else {
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, response.getMessage());
            }
        } catch (ApiException e) {
            log.error("调用钉钉获取部门列表接口异常：", e);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, e.getErrMsg());
        }
    }

    /**
     * 为Url补充suiteAccessToken
     *
     * @param url
     * @param req
     * @param <T>
     * @return
     */
    public <T extends TaobaoResponse> T executeWithSSOToken(String url, TaobaoRequest<T> req) {
        String ssoToken = dingtalkIsvCompanyAuthService.getSSOToken();
        DingTalkClient client = new DefaultDingTalkClient(url);
        T response = null;
        try {
            log.info("调用钉钉url：{},参数：{}", url, JsonUtils.toJson(req));
            response = client.execute(req, ssoToken);
            log.info("调用钉钉接口完成，url:{}, 返回结果：{}", url, JsonUtils.toJson(response));
            //40014 不合法的access_token
            //41007 无效的ssocode
            if ("40014".equals(response.getErrorCode())) {
                dingtalkIsvCompanyAuthService.clearSSOToken();
                response = executeWithSSOToken(url, req);
            }
            if (response.isSuccess()) {
                return response;
            } else {
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, response.getMessage());
            }
        } catch (ApiException e) {
            log.error("调用钉钉获取部门列表接口异常：", e);
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, e.getErrMsg());
        }
    }

}
