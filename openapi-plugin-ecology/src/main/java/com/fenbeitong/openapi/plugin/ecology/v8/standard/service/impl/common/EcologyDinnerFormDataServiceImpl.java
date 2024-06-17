package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

import com.fenbeitong.openapi.plugin.ecology.v8.constant.CommonServiceTypeConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.FanWeiFormDataBuildService;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.weaver.v8.workflow.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用餐信息表单组装
 * @Auther zhang.peng
 * @Date 2021/10/26
 */
@Service
public class EcologyDinnerFormDataServiceImpl implements FanWeiFormDataBuildService {

    private static final Integer DINNER_TYPE = 8;

    @Override
    public WorkflowRequestInfo buildFormDataInfo(FanWeiFormData fanWeiFormData, FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList ) {

        WorkflowRequestInfo workflowRequestInfo = new WorkflowRequestInfo();//工作流程请求信息

        String userId = fenbeitongApproveDto.getThirdEmployeeId();//
        workflowRequestInfo.setCanView(true);//显示
        workflowRequestInfo.setCanEdit(true);//可编辑
        workflowRequestInfo.setRequestName( fenbeitongApproveDto.getEmployeeName() + "的分贝通用餐审批申请单");//请求标题
        workflowRequestInfo.setRequestLevel("1");//请求重要级别
        workflowRequestInfo.setCreatorId( userId );//创建者id
        WorkflowBaseInfo workflowBaseInfo = new WorkflowBaseInfo();//工作流信息
        BeanUtils.copyProperties(fanWeiFormData,workflowBaseInfo);
        workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo);//工作流信息

        /****************main table start*************/
        if (CollectionUtils.isNotBlank(mappingConfigList)){
            // 8 是用餐审批
            mappingConfigList = mappingConfigList.stream().filter(etlMappingConfig -> DINNER_TYPE.equals(etlMappingConfig.getType())).collect(Collectors.toList());
        }
        WorkflowMainTableInfo workflowMainTableInfo = WorkFlowRequestTableFieldBuilder.buildDinnerMainTableInfo(fenbeitongApproveDto,fbtApproveId,mappingConfigList);
        workflowRequestInfo.setWorkflowMainTableInfo(workflowMainTableInfo);

        return workflowRequestInfo;
    }

    @Override
    public String getType(){
        return CommonServiceTypeConstant.DINNER;
    }

}
