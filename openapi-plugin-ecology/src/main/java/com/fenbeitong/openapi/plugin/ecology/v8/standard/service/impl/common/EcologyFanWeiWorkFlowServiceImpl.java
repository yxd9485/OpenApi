package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

import com.alibaba.fastjson.JSON;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiCreateWorkflowReqDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.EcologyFanWeiWorkFlowService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.fanwei.ClientUtil;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.weaver.v8.hrm.UserBean;
import com.weaver.v8.workflow.WorkflowBaseInfo;
import com.weaver.v8.workflow.WorkflowRequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther zhang.peng
 * @Date 2021/5/28
 */
@ServiceAspect
@Service
@Slf4j
public class EcologyFanWeiWorkFlowServiceImpl implements EcologyFanWeiWorkFlowService {


    @Override
    public List<FanWeiFormData> getFormContent(OpenEcologyWorkflowConfig workflowConfig , FanWeiCreateWorkflowReqDTO reqDTO) {
        String[] conditions = new String[2];
        conditions[0] = "";
        conditions[1] = "";
        WorkflowBaseInfo[] response = null;
        try {
            log.info("获取泛微表单开始 , companyId : {}",workflowConfig.getCompanyId());
            response = ClientUtil.getWorkFlowClient(workflowConfig).getCreateWorkflowList(reqDTO.getPageNo(), reqDTO.getPageSize(), reqDTO.getRecordCount(), reqDTO.getUserId(), reqDTO.getWorkflowType(), conditions);
            log.info("获取泛微表单结束 , companyId : {} , response : {} ",workflowConfig.getCompanyId(),response);
        } catch (Exception e){
            log.warn("获取泛微表单失败 , 返回结果 {} ,error : {}",e.getMessage(),response);
        }
        List<FanWeiFormData> fanWeiFormDataList = new ArrayList<>();
        if ( null == response || response.length == 0 ){
            return fanWeiFormDataList;
        }
        for (WorkflowBaseInfo workflowBaseInfo : response) {
            FanWeiFormData fanWeiFormData = new FanWeiFormData();
            BeanUtils.copyProperties(workflowBaseInfo,fanWeiFormData);
            fanWeiFormDataList.add(fanWeiFormData);
        }
        return fanWeiFormDataList;
    }

    @Override
    public String doCreateWorkflow(WorkflowRequestInfo workflowRequestInfo , String userId , OpenEcologyWorkflowConfig workflowConfig) {
        String requestId = "";
        try {
            log.info("创建表单请求开始 , userId {} , companyId {}",userId,workflowConfig.getCompanyId());
            String json = JSON.toJSONString(workflowRequestInfo);
            log.info("创建表单请求参数 json : {}",json);
            requestId = ClientUtil.getWorkFlowClient(workflowConfig).doCreateWorkflowRequest(workflowRequestInfo, StringUtils.isBlank(userId) ? 0 : Integer.parseInt(userId));
            log.info("创建表单请求结束 , requestId {} ",requestId);
        } catch (Exception e){
            log.info("创建表单失败 , companyId {} , requestId {} ",workflowConfig.getCompanyId(),requestId);
        }
        return requestId;
    }

    @Override
    public String submitCreateWorkflow(WorkflowRequestInfo workflowRequestInfo, int userId, String requestId, OpenEcologyWorkflowConfig workflowConfig , EcologySubmitTypeEnum submitTypeEnum , String remark) {
        String responseId = "";
        try {
            String type = submitTypeEnum.getKey();
            int requestIdValue = StringUtils.isBlank(requestId) ? 0 : Integer.parseInt(requestId);
            workflowRequestInfo.setRequestId(requestId);
            workflowRequestInfo.setRemark(remark);
            workflowRequestInfo.setWorkflowDetailTableInfos(null);
            workflowRequestInfo.setWorkflowMainTableInfo(null);
            log.info("提交表单开始 , companyId {} , requestId {} ",workflowConfig.getCompanyId(),requestId);
            responseId = ClientUtil.getWorkFlowClient(workflowConfig).submitWorkflowRequest(workflowRequestInfo, requestIdValue, userId, type, remark);
            log.info("提交表单结束 , companyId {} , requestId {} ",workflowConfig.getCompanyId(),requestId);
        } catch (Exception e){
            log.info("提交表单失败 , companyId {} , requestId {} ",workflowConfig.getCompanyId(),requestId);
        }
        return responseId;
    }

    @Override
    public UserBean getHrmUserInfo(String url, String userDepartmentId, String thirdEmployId , OpenEcologyWorkflowConfig workflowConfig) {
        UserBean[] hrmUserInfo = null;
        try {
            log.info("获取用户信息开始 , companyId {} ",workflowConfig.getCompanyId());
            hrmUserInfo = ClientUtil.getHrmClient(workflowConfig).getHrmUserInfo(url, null, null, userDepartmentId, null, null);
            log.info("获取用户信息结束 , companyId {} , userInfo {} ",workflowConfig.getCompanyId(),hrmUserInfo);
        } catch (Exception e){
            log.info("获取用户信息失败 , companyId {} , error {} ",workflowConfig.getCompanyId(),e.getMessage());
        }
        if ( null == hrmUserInfo || hrmUserInfo.length == 0 ){
            return new UserBean();
        }
        int thirdEmployIdInt = Integer.parseInt(thirdEmployId);
        for (UserBean userBean : hrmUserInfo) {
            if ( userBean.getUserid() == thirdEmployIdInt ){
                return userBean;
            }
        }
        return new UserBean();
    }

    @Override
    public boolean deleteWorkflow(int requestId, int userId, OpenEcologyWorkflowConfig workflowConfig) {
        boolean result = false;
        try {
            log.info("删除表单请求开始 , userId {} , companyId {} , requestId {}",userId,workflowConfig.getCompanyId(),requestId);
            // 两个参数分别是 : 请求id , userId
            result = ClientUtil.getWorkFlowClient(workflowConfig).deleteRequest(requestId,userId);
            log.info("删除表单请求结束 , 返回结果 {} ",result);
        } catch (Exception e){
            log.info("删除表单失败 , userId {} , companyId {} , requestId {} ",userId,workflowConfig.getCompanyId(),requestId);
        }
        return result;
    }
}
