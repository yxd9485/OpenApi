package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.util.ParseUcTokenUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.common.util.DdConfigSign;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkJsapiSignRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpAppService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkEiaWebService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvWebService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.support.common.dto.UserCenterResponse;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpAppDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcEmployeeSelfInfoResponse;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 钉钉内嵌版获取鉴权签名
 *
 * @author xiaohai
 * @date 2021/10/28
 */
@Slf4j
@Service
public class DingtalkEiaWebServiceImpl implements IDingtalkEiaWebService {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private IDingtalkCorpAppService dingtalkCorpAppService;

    @Autowired
    private DingtalkCorpServiceImpl dingtalkCorpService;

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Override
    public DingtalkJsapiSignRespDTO getJsapiSign(UserComInfoVO user, String data) {
        Map<String, Object> map = JsonUtils.toObj(data, Map.class);
        String url = StringUtils.obj2str(map.get("url"));
        if (user == null) {
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_USER_UN_SYNC));
        }
        String companyId = user.getCompany_id();
        PluginCorpDefinition dingtalkCorp = dingtalkCorpService.getByCompanyId(companyId);
        if (dingtalkCorp == null) {
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.CORP_INALID));
        }
        String corpId = dingtalkCorp.getThirdCorpId();
        PluginCorpAppDefinition dingtalkCorpApp = dingtalkCorpAppService.getByCorpId(corpId);
        String jsapiTicket = getJsapiTicket(corpId);
        String noncestr = DdConfigSign.getRandomStr(32);
        Long timeStamp = System.currentTimeMillis();
        Long thirdAgentId = dingtalkCorpApp.getThirdAgentId();
        String sign = null;
        try {
            sign = DdConfigSign.sign(jsapiTicket, noncestr, timeStamp, url);
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR));
        }
        return DingtalkJsapiSignRespDTO.builder().corpId(corpId).agentId(thirdAgentId).nonceStr(noncestr).timeStamp(timeStamp).signature(sign).url(url).build();
    }


    public String getJsapiTicket(String corpId) {
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/get_jsapi_ticket");
        OapiGetJsapiTicketRequest request = new OapiGetJsapiTicketRequest();
        request.setHttpMethod("GET");
        try {
            OapiGetJsapiTicketResponse response = client.execute(request, accessToken);
            if (response.isSuccess()) {
                return response.getTicket();
            }
            throw new OpenApiArgumentException(response.getErrmsg());
        } catch (ApiException e) {
            log.info("获取鉴权签名失败", e);
            throw new OpenApiArgumentException(e.getErrMsg());
        }
    }
}
