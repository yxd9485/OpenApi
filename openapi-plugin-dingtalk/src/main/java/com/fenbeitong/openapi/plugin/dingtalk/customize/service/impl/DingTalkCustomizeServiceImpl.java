package com.fenbeitong.openapi.plugin.dingtalk.customize.service.impl;

import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.customize.service.DingTalkCustomizeService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiDepartmentService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <p>Title: DingTalkCustomizeServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/1/7 1:41 下午
 */
@ServiceAspect
@Service
@Slf4j
public class DingTalkCustomizeServiceImpl implements DingTalkCustomizeService {
    @Autowired
    private IDingtalkCorpService dingtalkCorpService;
    @Autowired
    private IApiDepartmentService apiDepartmentService;
    @Autowired
    private IApiTokenService dingtalkTokenService;
    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Override
    public Map<String, String> getDepDetail(String companyId, String deptId) {
        PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
        String thirdCorpId = corpDefinition.getThirdCorpId();
        String accessToken = dingtalkTokenService.getAccessToken(thirdCorpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(thirdCorpId).getProxyUrl();
        OapiDepartmentGetResponse oapiDepartmentGetResponse = apiDepartmentService.getDepartmentInfo(accessToken, proxyUrl, deptId);
        if (!ObjectUtils.isEmpty(oapiDepartmentGetResponse)) {
            return JsonUtils.toObj(oapiDepartmentGetResponse.getBody(), Map.class);
        }
        return null;
    }
}
