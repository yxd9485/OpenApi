package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.WorkflowDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.weaver.v8.workflow.WorkflowRequestInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: EcologyWorkFlowListener</p>
 * <p>Description: 泛微审批流监听</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/1 1:01 PM
 */
public interface EcologyWorkFlowListener {

    /**
     * 获取第三方用户信息
     *
     * @param employeeList 员工列表
     * @return
     */
    Map<String, EmployeeBaseInfo> getThirdUserMap(List<EmployeeBaseInfo> employeeList);

    /**
     * 获取用户审批流列表
     *
     * @param workflowConfig 泛微工作流配置
     * @param userId         用户id
     * @param createDate     创建时间
     * @return
     */
    List<WorkflowRequestInfo> getUserAllWorkflowRequestList(OpenEcologyWorkflowConfig workflowConfig, Integer userId, Date createDate, Map<String, Integer> applyNameMapping);

    /**
     * 加载工作流详情
     *
     * @param userId         用户id
     * @param requestIdList  工作流程id列表
     * @param workflowConfig 工作流配置
     * @return
     */
    List<WorkflowRequestInfo> loadDetailWorkFlowList(int userId, List<String> requestIdList, OpenEcologyWorkflowConfig workflowConfig);

    /**
     * 设置表单开始及结束时间
     *
     * @param workflow       工作流对象
     * @param workflowDto    工作流转换dto
     * @param workflowConfig 工作流配置选项
     */
    void setStartEndDateTime(OpenEcologyWorkflow workflow, WorkflowDTO workflowDto, OpenEcologyWorkflowConfig workflowConfig);

    /**
     * 是否审批通过
     *
     * @param workflowDto 工作流对象
     * @return
     */
    Integer agreed(WorkflowDTO workflowDto);

    /**
     * 创建审批
     *
     * @param companyId
     */
    void createApply(String companyId);

}