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
 * 用车信息表单组装
 * @Auther zhang.peng
 * @Date 2021/5/21
 */
@ServiceAspect
@Service
public class CarFormDataServiceImpl implements FanWeiFormDataBuildService {

    @Override
    public WorkflowRequestInfo buildFormDataInfo(FanWeiFormData fanWeiFormData, FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList ) {

        WorkflowRequestInfo  workflowRequestInfo = new WorkflowRequestInfo();//工作流程请求信息

        String userId = fenbeitongApproveDto.getThirdEmployeeId();//
        workflowRequestInfo.setCanView(true);//显示
        workflowRequestInfo.setCanEdit(true);//可编辑
        workflowRequestInfo.setRequestName( fenbeitongApproveDto.getEmployeeName() + "的分贝通用车审批申请单");//请求标题
        workflowRequestInfo.setRequestLevel("1");//请求重要级别
        workflowRequestInfo.setCreatorId( userId );//创建者id
        WorkflowBaseInfo workflowBaseInfo = new WorkflowBaseInfo();//工作流信息
        BeanUtils.copyProperties(fanWeiFormData,workflowBaseInfo);
        workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo);//工作流信息

        /****************main table start*************/
        if (CollectionUtils.isNotBlank(mappingConfigList)){
            // 7 是用车审批
            mappingConfigList = mappingConfigList.stream().filter(etlMappingConfig -> 7 == etlMappingConfig.getType()).collect(Collectors.toList());
        }
        WorkflowMainTableInfo workflowMainTableInfo = WorkFlowRequestTableFieldBuilder.buildCarMainTableInfo(fenbeitongApproveDto,fbtApproveId,mappingConfigList);
        workflowRequestInfo.setWorkflowMainTableInfo(workflowMainTableInfo);

        List<FenbeitongApproveDto.Trip> tripList = fenbeitongApproveDto.getTripList();
        if (CollectionUtils.isNotBlank(tripList)){
            // 获取城市
            FenbeitongApproveDto.Trip trip = tripList.get(0);
            List<String> startCityNameList = trip.getStartCityNameList();
            int detailCount = startCityNameList.size();
            // 这个是子表信息 , 只有一张子表
            WorkflowDetailTableInfo[] workflowDetailTables = new WorkflowDetailTableInfo[1];
            workflowDetailTables[0] = new WorkflowDetailTableInfo();
            // 将字段信息设置到行中,几个城市就有有一行
            WorkflowRequestTableRecord[] workflowDetailRequestTableRecord = new WorkflowRequestTableRecord[detailCount];
            if (CollectionUtils.isNotBlank(startCityNameList)) {
                for (int i = 0; i < startCityNameList.size(); i++) {
                    // 每一个行程代表一张明细表,trip里面的字段代表每一行的数据
                    // 获取每一行的字段列表
                    WorkflowRequestTableField[] workflowRequestTableFields = WorkFlowRequestTableFieldBuilder.buildCarDetailInfo(fenbeitongApproveDto, trip , startCityNameList.get(i),mappingConfigList);
                    workflowDetailRequestTableRecord[i] = new WorkflowRequestTableRecord();
                    workflowDetailRequestTableRecord[i].setWorkflowRequestTableFields(workflowRequestTableFields);
                }
                workflowDetailTables[0].setWorkflowRequestTableRecords(workflowDetailRequestTableRecord);
            }
            workflowRequestInfo.setWorkflowDetailTableInfos(workflowDetailTables);
        }
        return workflowRequestInfo;
    }

    @Override
    public String getType(){
        return CommonServiceTypeConstant.CAR;
    }

}
