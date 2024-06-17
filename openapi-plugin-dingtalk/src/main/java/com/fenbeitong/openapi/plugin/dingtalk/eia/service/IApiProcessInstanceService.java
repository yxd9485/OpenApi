package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;

/**
 * <p>Title: IApiProcessInstanceService</p>
 * <p>Description: 钉钉审批实例接口</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/20 7:41 PM
 */
public interface IApiProcessInstanceService {

    /**
     * 获取钉钉审批实例
     * @param instanceId 钉钉审批实例id
     * @param corpId 钉钉corpId
     * @return 审批信息
     */
    OapiProcessinstanceGetResponse.ProcessInstanceTopVo getProcessInstance(String instanceId, String corpId);
}
