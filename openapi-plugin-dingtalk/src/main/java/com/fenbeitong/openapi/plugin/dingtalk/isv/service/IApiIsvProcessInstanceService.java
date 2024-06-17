package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;

/** 
 * @Description  
 * @Author duhui
 * @Date  2021-04-07
**/
public interface IApiIsvProcessInstanceService {

    /**
     * 获取钉钉审批实例
     * @param instanceId 钉钉审批实例id
     * @param corpId 钉钉corpId
     * @return 审批信息
     */
    OapiProcessinstanceGetResponse.ProcessInstanceTopVo getProcessInstance(String instanceId, String corpId);
}
