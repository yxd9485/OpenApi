package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.dingtalk.api.response.OapiSmartworkHrmEmployeeFieldGrouplistResponse;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeV2ListResponse;

/**
 * 花名册信息接口
 * @author zhangpeng
 * @date 2022/3/23 11:03 上午
 */
public interface IDingtalkRosterInfoService {

    /**
     * 获取花名册字段组信息
     * @param corpId 钉钉corpId
     * @return 花名册字段组信息
     */
    OapiSmartworkHrmEmployeeFieldGrouplistResponse getFieldsGroupInfo(String corpId);

    /**
     * 获取花名册人员字段信息
     * @param corpId 钉钉corpId
     * @param fieldFilterList 查询字段信息
     * @param agentId 微应用在企业的agentId
     * @return 员工花名册字段详情
     */
    OapiSmartworkHrmEmployeeV2ListResponse getHrmEmployeeList(String corpId , String userIdsList , String fieldFilterList, long agentId);
}
