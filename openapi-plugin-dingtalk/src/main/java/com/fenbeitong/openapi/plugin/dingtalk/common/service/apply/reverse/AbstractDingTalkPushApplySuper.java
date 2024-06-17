package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;

/**
 * <p>Title: AbstractDingTalkPushApplySuper</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021-04-01 11:44
 */
@Slf4j
public abstract class AbstractDingTalkPushApplySuper {
    @Autowired
    DingtalkIsvClientUtils dingtalkIsvClientUtils;
    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    public <T extends TaobaoResponse> T execute(Integer openType, OapiProcessinstanceCreateRequest req, String corpId) {
        log.info("corpId:{},钉钉反向审批模板入参{},openType:{}", corpId, JsonUtils.toJson(req), openType);
        T response = null;
        String url;
        if (OpenType.DINGTALK_EIA.getType() == openType) {
            url = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl() + "/topapi/processinstance/create";
            // 获取钉钉token
            String accessToken = dingtalkTokenService.getAccessToken(corpId);
            DingTalkClient client = new DefaultDingTalkClient(url);
            try {
                response = (T) client.execute(req, accessToken);
                log.info("eia钉钉发送工作通知完成，参数: corpId: {}，result: {}", corpId, JsonUtils.toJson(response));
            } catch (ApiException e) {
                log.error("eia钉钉发送工作通知接口异常：{}", e);
            }

        } else if (OpenType.DINGTALK_ISV.getType() == openType) {
            url = dingtalkHost + "topapi/processinstance/create";
            DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
            if (ObjectUtils.isEmpty(dingtalkIsvCompany) && dingtalkIsvCompany.getAgentid() != null) {
                throw new RuntimeException("在dingtalk_isv_company表中没有查到corpId：" + corpId + "的信息");
            }
            req.setAgentId(dingtalkIsvCompany.getAgentid());
            response = (T) dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, req, corpId);
        }
        log.info("钉钉反向订单同步结果:{}", response);
        return response;
    }


}
