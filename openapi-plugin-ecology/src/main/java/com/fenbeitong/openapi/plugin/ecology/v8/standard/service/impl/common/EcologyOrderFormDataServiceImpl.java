package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

import com.fenbeitong.openapi.plugin.ecology.v8.constant.CommonServiceTypeConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.FanWeiFormDataBuildService;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonOrderInfo;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.apply.service.IOrderCovertService;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.EmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OrderCovertServiceImpl;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.weaver.v8.workflow.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 超规订单信息表单组装
 * @Auther zhang.peng
 * @Date 2021/11/08
 */
@Service
public class EcologyOrderFormDataServiceImpl implements FanWeiFormDataBuildService {

    private static final Integer ORDER_TYPE = 9;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public WorkflowRequestInfo buildFormDataInfo(FanWeiFormData fanWeiFormData, FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList ) {

        WorkflowRequestInfo workflowRequestInfo = new WorkflowRequestInfo();//工作流程请求信息

        String userId = fenbeitongApproveDto.getThirdEmployeeId();//
        workflowRequestInfo.setCanView(true);//显示
        workflowRequestInfo.setCanEdit(true);//可编辑
        workflowRequestInfo.setRequestName( fenbeitongApproveDto.getOrderPerson() + "的分贝通超规审批申请单");//请求标题
        workflowRequestInfo.setRequestLevel("1");//请求重要级别
        workflowRequestInfo.setCreatorId( userId );//创建者id
        WorkflowBaseInfo workflowBaseInfo = new WorkflowBaseInfo();//工作流信息
        BeanUtils.copyProperties(fanWeiFormData,workflowBaseInfo);
        workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo);//工作流信息

        /****************main table start*************/
        if (CollectionUtils.isNotBlank(mappingConfigList)){
            // 9 是超规审批
            mappingConfigList = mappingConfigList.stream().filter(etlMappingConfig -> ORDER_TYPE.equals(etlMappingConfig.getType())).collect(Collectors.toList());
        }
        IOrderCovertService covertService = new OrderCovertServiceImpl();
        Map<String,String> employId2ThirdEmployeeIdMap = new HashMap<>();
        if (CollectionUtils.isNotBlank(fenbeitongApproveDto.getGuestList())){
            for (FenbeitongApproveDto.Guest guest : fenbeitongApproveDto.getGuestList()) {
                // 0 是三方员工
                String thirdEmployeeId = employeeService.getEmployeeByEmployeeId(fenbeitongApproveDto.getCompanyId(),guest.getEmployeeId(),"0");
                employId2ThirdEmployeeIdMap.put(guest.getEmployeeId(),thirdEmployeeId);
            }
        }
        CommonOrderInfo commonOrderInfo = covertService.convertOrder(fenbeitongApproveDto,employId2ThirdEmployeeIdMap);
        WorkflowMainTableInfo workflowMainTableInfo = WorkFlowRequestTableFieldBuilder.buildOrderMainTableInfo(fenbeitongApproveDto,fbtApproveId,mappingConfigList);
        workflowRequestInfo.setWorkflowMainTableInfo(workflowMainTableInfo);
        /****************main table end*************/

        /**************** 明细表开始 *************/
        if ( null != commonOrderInfo ){
            int detailCount = ORDER_SUB_FORM_SIZE;
            // 这个是子表信息 , 只有一张子表
            WorkflowDetailTableInfo[] workflowDetailTables = new WorkflowDetailTableInfo[1];
            workflowDetailTables[0] = new WorkflowDetailTableInfo();
            // 多个行程多行记录
            WorkflowRequestTableRecord[] workflowDetailRequestTableRecord = new WorkflowRequestTableRecord[detailCount];

            /**************** start *************/
            // 每一个行程代表一张明细表,trip里面的字段代表每一行的数据
            // 获取每一行的字段列表
            WorkflowRequestTableField[] workflowRequestTableFields = WorkFlowRequestTableFieldBuilder.buildOrderDetailInfo(fenbeitongApproveDto,commonOrderInfo,mappingConfigList);
            // 将字段信息设置到行中,每个表只有一行
            workflowDetailRequestTableRecord[0] = new WorkflowRequestTableRecord();
            workflowDetailRequestTableRecord[0].setWorkflowRequestTableFields(workflowRequestTableFields);

            workflowDetailTables[0].setWorkflowRequestTableRecords(workflowDetailRequestTableRecord);
            workflowRequestInfo.setWorkflowDetailTableInfos(workflowDetailTables);
            /**************** end *************/
        }
        /**************** 明细表结束  *************/


        return workflowRequestInfo;
    }

    private static final int ORDER_SUB_FORM_SIZE = 1;

    @Override
    public String getType(){
        return CommonServiceTypeConstant.ORDER;
    }

}
