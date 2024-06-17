package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.request.OapiSmartworkHrmEmployeeFieldGrouplistRequest;
import com.dingtalk.api.request.OapiSmartworkHrmEmployeeV2ListRequest;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeFieldGrouplistResponse;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeV2ListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRosterInfoService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.utils.DingtalkEiaClientUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 钉钉花名册信息实现
 * @author zhangpeng
 * @date 2022/3/23 11:42 上午
 */
@Service
@ServiceAspect
@Slf4j
public class IDingtalkRosterInfoServiceImpl implements IDingtalkRosterInfoService {

    @Autowired
    private DingtalkEiaClientUtils dingtalkEiaClientUtils;

    @Override
    public OapiSmartworkHrmEmployeeFieldGrouplistResponse getFieldsGroupInfo(String corpId){
        String url = dingtalkEiaClientUtils.getProxyUrlByCorpId(corpId) + "/topapi/smartwork/hrm/roster/meta/get";
        OapiSmartworkHrmEmployeeFieldGrouplistRequest request = new OapiSmartworkHrmEmployeeFieldGrouplistRequest();
        OapiSmartworkHrmEmployeeFieldGrouplistResponse req = dingtalkEiaClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return req;
    }

    @Override
    public OapiSmartworkHrmEmployeeV2ListResponse getHrmEmployeeList(String corpId , String userIdsList , String fieldFilterList , long agentId){
        String url = dingtalkEiaClientUtils.getProxyUrlByCorpId(corpId) + "/topapi/smartwork/hrm/employee/v2/list";
        OapiSmartworkHrmEmployeeV2ListRequest request = new OapiSmartworkHrmEmployeeV2ListRequest();
        request.setFieldFilterList(fieldFilterList);
        request.setUseridList(userIdsList);
        request.setAgentid(agentId);
        OapiSmartworkHrmEmployeeV2ListResponse rsp = dingtalkEiaClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return rsp;
    }
}
