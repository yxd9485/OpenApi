package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiCreateWorkflowReqDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common.EcologySubmitTypeEnum;
import com.weaver.v8.hrm.UserBean;
import com.weaver.v8.workflow.WorkflowRequestInfo;

import java.util.List;

/**
 * @Auther zhang.peng
 * @Date 2021/5/28
 */
public interface EcologyFanWeiWorkFlowService {

    List<FanWeiFormData> getFormContent(OpenEcologyWorkflowConfig workflowConfig , FanWeiCreateWorkflowReqDTO reqDTO);

    String doCreateWorkflow(WorkflowRequestInfo workflowRequestInfo, String userId , OpenEcologyWorkflowConfig workflowConfig);

    String submitCreateWorkflow(WorkflowRequestInfo workflowRequestInfo , int userId , String requestId , OpenEcologyWorkflowConfig workflowConfig , EcologySubmitTypeEnum submitTypeEnum , String remark);

    UserBean getHrmUserInfo(String url , String userDepartmentId , String thirdEmployId ,  OpenEcologyWorkflowConfig workflowConfig );

    boolean deleteWorkflow(int requestId, int userId, OpenEcologyWorkflowConfig workflowConfig);
}
