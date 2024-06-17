package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.request.OapiRoleListRequest;
import com.dingtalk.api.response.OapiRoleListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvRoleService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 角色
 *
 * @author lizhen
 * @date 2020/10/23
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvRoleServiceImpl implements IDingtalkIsvRoleService {

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Override
    public OapiRoleListResponse listRole(String corpId) {
        try {
            String url = dingtalkHost + "topapi/role/list";
            OapiRoleListRequest request = new OapiRoleListRequest();
            request.setOffset(0L);
            request.setSize(10L);
            OapiRoleListResponse response = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
            return response;
        } catch (Exception e) {
            log.error("获取钉钉角色失败：", e);
        }
        return null;
    }
}
