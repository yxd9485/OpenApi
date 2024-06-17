package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import org.springframework.validation.BindException;

/**
 * <p>Title: IDingtalkProcessApplyService</p>
 * <p>Description: 钉钉审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/21 3:44 PM
 */
public interface IDingtalkProcessApplyService {

    /**
     * 处理钉钉任务
     *
     * @param task                 钉钉任务
     * @param dingtalkCorp         企业信息表
     * @param apply                审批单注册表
     * @param processInstanceTopVo 钉钉审批单详情
     * @return 处理结果
     */
    TaskResult processApply(Task task, PluginCorpDefinition dingtalkCorp, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) throws BindException;



//    /**
//     * 处理钉钉任务
//     *
//     * @param task                 钉钉任务
//     * @param apply                审批单注册表
//     * @param processInstanceTopVo 钉钉审批单详情
//     * @return 处理结果
//     */
//    TaskResult processApply(Task task, DingtalkIsvCompany dingtalkIsvCompany,DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo);
}
