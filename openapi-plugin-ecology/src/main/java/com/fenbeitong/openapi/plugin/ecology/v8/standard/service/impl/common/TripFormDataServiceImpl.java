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
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 差旅信息表单组装
 * @Auther zhang.peng
 * @Date 2021/5/21
 */
@ServiceAspect
@Service
public class TripFormDataServiceImpl implements FanWeiFormDataBuildService {

    @Override
    public WorkflowRequestInfo buildFormDataInfo(FanWeiFormData fanWeiFormData, FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList ) {

        WorkflowRequestInfo  workflowRequestInfo = new WorkflowRequestInfo();//工作流程请求信息

        String userId = fenbeitongApproveDto.getThirdEmployeeId();//
        workflowRequestInfo.setCanView(true);//显示
        workflowRequestInfo.setCanEdit(true);//可编辑
        workflowRequestInfo.setRequestName( fenbeitongApproveDto.getApplyName() + "的分贝通差旅审批申请单");//请求标题
        workflowRequestInfo.setRequestLevel("1");//请求重要级别
        workflowRequestInfo.setCreatorId( userId );//创建者id

        WorkflowBaseInfo workflowBaseInfo = new WorkflowBaseInfo();//工作流信息
        BeanUtils.copyProperties(fanWeiFormData,workflowBaseInfo);
        workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo);//工作流信息

        /****************main table start*************/
        if (CollectionUtils.isNotBlank(mappingConfigList)){
            // 6 是差旅审批
            mappingConfigList = mappingConfigList.stream().filter(etlMappingConfig -> 6 == etlMappingConfig.getType()).collect(Collectors.toList());
        }
        WorkflowMainTableInfo workflowMainTableInfo = WorkFlowRequestTableFieldBuilder.buildTripMainTableInfo(fenbeitongApproveDto,fbtApproveId,mappingConfigList);

        workflowRequestInfo.setWorkflowMainTableInfo(workflowMainTableInfo);
        /****************main table end*************/

        List<FenbeitongApproveDto.Trip> tripList = fenbeitongApproveDto.getTripList();
        if (CollectionUtils.isNotBlank(tripList)){
            int detailCount = tripList.size();
            // 这个是子表信息 , 只有一张子表
            WorkflowDetailTableInfo[] workflowDetailTables = new WorkflowDetailTableInfo[1];
            workflowDetailTables[0] = new WorkflowDetailTableInfo();
            // 多个行程多行记录
            WorkflowRequestTableRecord[] workflowDetailRequestTableRecord = new WorkflowRequestTableRecord[detailCount];
            for (int i = 0; i < tripList.size(); i++) {
                // 每一个行程代表一张明细表,trip里面的字段代表每一行的数据
                // 获取每一行的字段列表
                WorkflowRequestTableField[] workflowRequestTableFields = WorkFlowRequestTableFieldBuilder.buildTripDetailInfo(fenbeitongApproveDto,tripList.get(i),mappingConfigList);
                // 将字段信息设置到行中,每个表只有一行
                workflowDetailRequestTableRecord[i] = new WorkflowRequestTableRecord();
                workflowDetailRequestTableRecord[i].setWorkflowRequestTableFields(workflowRequestTableFields);
            }
            workflowDetailTables[0].setWorkflowRequestTableRecords(workflowDetailRequestTableRecord);
            workflowRequestInfo.setWorkflowDetailTableInfos(workflowDetailTables);
        }
        /**********第一张明细表结束**********/

        return workflowRequestInfo;
    }

    @Override
    public String getType(){
        return CommonServiceTypeConstant.TRIP;
    }

}
