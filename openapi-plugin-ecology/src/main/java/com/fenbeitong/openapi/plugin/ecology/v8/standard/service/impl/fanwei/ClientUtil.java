package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.fanwei;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.weaver.v8.hrm.HrmServiceLocator;
import com.weaver.v8.hrm.HrmServicePortType;
import com.weaver.v8.workflow.WorkflowServiceLocator;
import com.weaver.v8.workflow.WorkflowServicePortType;

/**
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
public class ClientUtil {

    public static WorkflowServicePortType getWorkFlowClient(OpenEcologyWorkflowConfig workflowConfig){
        WorkflowServiceLocator locator = new WorkflowServiceLocator(workflowConfig.getWsUrl());
        WorkflowServicePortType httpPort = null;
        try {
            httpPort = locator.getWorkflowServiceHttpPort();
        } catch (Exception e){
            e.printStackTrace();
        }
        return httpPort;
    }

    public static HrmServicePortType getHrmClient(OpenEcologyWorkflowConfig workflowConfig){
        HrmServiceLocator locator = new HrmServiceLocator(workflowConfig.getWsUrl());
        HrmServicePortType httpPort = null;
        int count;
        try {
            httpPort = locator.getHrmServiceHttpPort();
        } catch (Exception e){
            e.printStackTrace();
        }
        return httpPort;
    }
}
