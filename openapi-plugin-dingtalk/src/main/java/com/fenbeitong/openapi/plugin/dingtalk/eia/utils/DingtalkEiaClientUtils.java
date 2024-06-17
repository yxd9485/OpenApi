package com.fenbeitong.openapi.plugin.dingtalk.eia.utils;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoRequest;
import com.taobao.api.TaobaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @author ctl
 * @date 2021/4/27
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkEiaClientUtils {

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    /**
     * 根据corpId获取proxyUrl
     *
     * @param corpId
     * @return
     */
    public String getProxyUrlByCorpId(String corpId) {
        return dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
    }

    /**
     * 根据corpId获取accessToken
     *
     * @param corpId
     * @return
     */
    public String getAccessTokenByCorpId(String corpId) {
        return dingtalkTokenService.getAccessToken(corpId);
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
        String accessToken = getAccessTokenByCorpId(corpId);
        DingTalkClient client = new DefaultDingTalkClient(url);
        T response = null;
        try {
            log.info("调用钉钉url：{},参数：{}", url, JsonUtils.toJson(req));
            response = client.execute(req, accessToken);
            log.info("调用钉钉接口完成，url:{}, 返回结果：{}", url, response.getBody());
            if ("33001".equals(response.getErrorCode()) || "40014".equals(response.getErrorCode()) || "41001".equals(response.getErrorCode()) || "42001".equals(response.getErrorCode())) {
                dingtalkTokenService.clearCorpAccessToken(corpId);
                response = executeWithCorpAccesstoken(url, req, corpId);
            }
            // 50002请求的员工userid不在授权范围内
            // 50004请求的部门id不在授权范围内
            // 60121人员不存在fd
            // 40009不合法的部门id
            if (response.isSuccess() || response.getErrorCode().equals("50002") || response.getErrorCode().equals("50004") || response.getErrorCode().equals("60121") || response.getErrorCode().equals("40009")) {
                return response;
            } else {
                throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR), response.getMessage());
            }
        } catch (ApiException e) {
            log.error("调用钉钉获取部门列表接口异常：", e);
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR), e.getErrMsg());
        }
    }
}
