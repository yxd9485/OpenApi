package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.ecology.v8.constant.CommonServiceTypeConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse.ExpenseLargeTypeEnum;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse.ExpenseSmallTypeEnum;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse.VehicleEnum;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.FanWeiFormDataBuildService;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustomReimbursementDto;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustomReimbursementNoticeDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.weaver.v8.workflow.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自定义报销单审批
 * @Auther zhang.peng
 * @Date 2022/1/21
 */
@ServiceAspect
@Service
@Slf4j
public class ReimburseServiceImpl implements FanWeiFormDataBuildService {

    private static final Integer REIMBURSE_TYPE = 10;

    private static final String OTHER = "其他";

    @Autowired
    private WorkFlowReimbursementTableFieldBuilder workFlowReimbursementTableFieldBuilder;

    @Override
    public WorkflowRequestInfo buildFormDataInfo(FanWeiFormData fanWeiFormData, FenbeitongApproveDto fenbeitongApproveDto , String fbtApproveId, List<OpenEtlMappingConfig> mappingConfigList ) {

        WorkflowRequestInfo  workflowRequestInfo = new WorkflowRequestInfo();//工作流程请求信息

        String userId = fenbeitongApproveDto.getThirdEmployeeId();
        /**
         * 显示
         */
        workflowRequestInfo.setCanView(true);
        /**
         * 可编辑
         */
        workflowRequestInfo.setCanEdit(true);
        /**
         * 请求标题
         */
        CustomReimbursementDto customReimbursementDto = (CustomReimbursementDto) fenbeitongApproveDto;
        workflowRequestInfo.setRequestName( (null == customReimbursementDto.getEmployee() ? "" : customReimbursementDto.getEmployee().getEmployeeThirdName()) + "的分贝通自定义报销审批申请单");
        /**
         * 请求重要级别
         */
        workflowRequestInfo.setRequestLevel("1");
        /**
         * 创建者id
         */
        workflowRequestInfo.setCreatorId( userId );
        /**
         * 工作流信息
         */
        WorkflowBaseInfo workflowBaseInfo = new WorkflowBaseInfo();
        BeanUtils.copyProperties(fanWeiFormData,workflowBaseInfo);
        workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo);

        /****************main table start*************/
        if (CollectionUtils.isNotBlank(mappingConfigList)){
            // 20 是自定义报销审批
            mappingConfigList = mappingConfigList.stream().filter(etlMappingConfig -> REIMBURSE_TYPE.equals(etlMappingConfig.getType()) ).collect(Collectors.toList());
        }
        WorkflowMainTableInfo workflowMainTableInfo = workFlowReimbursementTableFieldBuilder.buildReimburseMainTableInfo(fenbeitongApproveDto,fbtApproveId,mappingConfigList);
        workflowRequestInfo.setWorkflowMainTableInfo(workflowMainTableInfo);

        List<CustomReimbursementNoticeDTO.CostCategory> costCategoryList = customReimbursementDto.getCostCategoryList();
        if (CollectionUtils.isNotBlank(costCategoryList)){
            // 有几个费用明细就是几个子表
            WorkflowDetailTableInfo[] workflowDetailTables = new WorkflowDetailTableInfo[costCategoryList.size()];

            for (int i = 0; i < costCategoryList.size(); i++) {
                // 这个是子表信息 , 只有一张子表
                CustomReimbursementNoticeDTO.CostCategory costCategory = costCategoryList.get(i);
                List<CustomReimbursementNoticeDTO.CostDetail> detailList = costCategory.getDetailList();
                detailList = detailList.stream().filter(costDetail -> ( null != costDetail.getValue()) ).collect(Collectors.toList());
                Map<String,Object> costDetailMap = detailList.stream().collect(Collectors.toMap(CustomReimbursementNoticeDTO.CostDetail::getName,CustomReimbursementNoticeDTO.CostDetail::getValue));
                WorkflowRequestTableRecord[] workflowDetailRequestTableRecord = new WorkflowRequestTableRecord[1];
                WorkflowRequestTableField[] requestTableFields = null;
                // 住宿费
                if ( costCategory.getName().contains("住宿")){
                    requestTableFields = buildTripInfo(costCategory,mappingConfigList,costDetailMap);
                }
                // 交通费
                if ( costCategory.getName().contains("交通费") ){
                    requestTableFields = buildTrafficInfo(costCategory,mappingConfigList,costDetailMap);
                }
                // 日程费用明细
                if ( costCategory.getName().contains("日常") ){
                    requestTableFields = buildExpenseDetailInfo(costCategory,mappingConfigList,costDetailMap);
                }
                workflowDetailRequestTableRecord[0] = new WorkflowRequestTableRecord();
                workflowDetailRequestTableRecord[0].setWorkflowRequestTableFields(requestTableFields);
                workflowDetailTables[i] = new WorkflowDetailTableInfo();
                workflowDetailTables[i].setWorkflowRequestTableRecords(workflowDetailRequestTableRecord);
            }
            workflowRequestInfo.setWorkflowDetailTableInfos(workflowDetailTables);
        }

        return workflowRequestInfo;
    }

    @Override
    public String getType(){
        return CommonServiceTypeConstant.REIMBURSE;
    }

    public WorkflowRequestTableField[] buildTripInfo(CustomReimbursementNoticeDTO.CostCategory costCategory , List<OpenEtlMappingConfig> mappingConfigList , Map<String,Object> costDetailMap ) {
        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[0];
        try {
            if (null != costCategory && costCategory.getName().contains("住宿")) {
                //开始时间、结束时间、出差城市、城市级别、住宿天数、住宿标准、交易总额、差旅补助标准、差旅补助总额、单据张数、发票
                List<OpenEtlMappingConfig> tripMappingConfigList = mappingConfigList.stream().filter(etlMappingConfig -> "detail".equals(etlMappingConfig.getGroupName())).collect(Collectors.toList());
                WorkflowRequestTableField = new WorkflowRequestTableField[tripMappingConfigList.size()];
                // 城市单独处理
                List<Map> cityList = JsonUtils.toObj(JsonUtils.toJson(costDetailMap.get("出差城市")), new TypeReference<List<Map>>() {
                });
                for (int k = 0; k < tripMappingConfigList.size(); k++) {
                    WorkflowRequestTableField[k] = new WorkflowRequestTableField();
                    OpenEtlMappingConfig config = tripMappingConfigList.get(k);
                    String sourceName = config.getSrcCol();
                    WorkflowRequestTableField[k].setFieldName(config.getTgtCol());
                    WorkflowRequestTableField[k].setView(true);//字段是否可见
                    WorkflowRequestTableField[k].setEdit(true);//字段是否可编辑
                    if ("startTime".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue((String) costDetailMap.get("开始时间"));
                    }
                    if ("endTime".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue((String) costDetailMap.get("结束时间"));
                    }
                    // 省
                    if ("tripProvince".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(cityList.get(0).get("id") + "");
                    }
                    // 市
                    if ("tripCity".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(cityList.get(1).get("id") + "");
                    }
                    // 区
                    if ("tripArea".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(cityList.get(2).get("id") + "");
                    }
                    if ("tripDays".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(string2Integer((String) costDetailMap.get("住宿天数")));
                    }
                    if ("tripTotalAmount".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(string2Integer((String) costDetailMap.get("住宿总金额")));
                    }
                    if ("tripStandard".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(string2Integer((String) costDetailMap.get("住宿标准")));
                    }
                    if ("dealTotal".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(string2Integer((String) costDetailMap.get("交易总额")));
                    }
                    if ("tripAllowanceStandard".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(string2Integer((String) costDetailMap.get("差旅补助标准")));
                    }
                    if ("tripAllowanceTotal".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(string2Integer((String) costDetailMap.get("差旅补助总额")));
                    }
                    if ("invoice".equals(sourceName)) {
                        List<CustomReimbursementNoticeDTO.ReturnInfo> invoiceList = costCategory.getInvoiceList();
                        if (CollectionUtils.isNotBlank(invoiceList)){
                            String invPicUrl = invoiceList.get(0).getInvPicUrl();
                            FileUrlValueBuilder.buildFileUrls(invPicUrl,WorkflowRequestTableField[k]);
                        }
                    }
                    if ("voucherNum".equals(sourceName)) {
                        WorkflowRequestTableField[k].setFieldValue(string2Integer((String) costDetailMap.get("单据张数")));
                    }
                }
            }
        } catch (Exception e){
            log.info("转换差旅费失败 : {}",e.getMessage());
        }
        return WorkflowRequestTableField;
    }

    /**
     * 去掉小数点
     * @return
     */
    public String string2Integer(String value){
        if (StringUtils.isBlank(value)){
            return "";
        }
        if (value.contains(".")){
            return Integer.valueOf(value.substring(0,value.indexOf("."))).toString();
        }
        return Integer.valueOf(value).toString();
    }

    public WorkflowRequestTableField[] buildTrafficInfo(CustomReimbursementNoticeDTO.CostCategory costCategory , List<OpenEtlMappingConfig> mappingConfigList , Map<String,Object> costDetailMap ){
        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[0];
        try {
            List<OpenEtlMappingConfig> trafficMappingConfigList = mappingConfigList.stream().filter(etlMappingConfig -> "detail2".equals(etlMappingConfig.getGroupName())).collect(Collectors.toList());
            if ( null != costCategory && costCategory.getName().contains("交通费") ){
                WorkflowRequestTableField = new WorkflowRequestTableField[trafficMappingConfigList.size()];
                for (int i = 0; i < trafficMappingConfigList.size(); i++) {
                    WorkflowRequestTableField[i] = new WorkflowRequestTableField();
                    OpenEtlMappingConfig config = trafficMappingConfigList.get(i);
                    String sourceName = config.getSrcCol();
                    WorkflowRequestTableField[i].setFieldName(config.getTgtCol());
                    WorkflowRequestTableField[i].setView(true);//字段是否可见
                    WorkflowRequestTableField[i].setEdit(true);//字段是否可编辑
                    // 交通工具、费用发生时间、交易总额、单据张数、发票
                    if ("vehicle".equals(sourceName)){
                        Map result = (Map) costDetailMap.get("交通工具");
                        int value = VehicleEnum.getType((String) result.get("value")).getType();
                        WorkflowRequestTableField[i].setFieldValue( value+"" );
                    }
                    if ("expenseOccurTime".equals(sourceName)){
                        WorkflowRequestTableField[i].setFieldValue((String)costDetailMap.get("费用发生时间"));
                    }
                    if ("dealTotal".equals(sourceName)){
                        WorkflowRequestTableField[i].setFieldValue(string2Integer((String)costDetailMap.get("交易总额")));
                    }
                    if ("voucherNum".equals(sourceName)){
                        WorkflowRequestTableField[i].setFieldValue(string2Integer((String)costDetailMap.get("单据张数")));
                    }
                    if ("invoice".equals(sourceName)){
                        List<CustomReimbursementNoticeDTO.ReturnInfo> invoiceList = costCategory.getInvoiceList();
                        if (CollectionUtils.isNotBlank(invoiceList)){
                            String invPicUrl = invoiceList.get(0).getInvPicUrl();
                            FileUrlValueBuilder.buildFileUrls(invPicUrl,WorkflowRequestTableField[i]);
                        }
                    }
                }
            }
        } catch (Exception e){
            log.info("转换交通费失败 : {}",e.getMessage());
        }
        return WorkflowRequestTableField;
    }

    public WorkflowRequestTableField[] buildExpenseDetailInfo(CustomReimbursementNoticeDTO.CostCategory costCategory , List<OpenEtlMappingConfig> mappingConfigList , Map<String,Object> costDetailMap ){
        WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[0];
        try {
            List<OpenEtlMappingConfig> expenseMappingConfigList = mappingConfigList.stream().filter(etlMappingConfig -> "detail3".equals(etlMappingConfig.getGroupName())).collect(Collectors.toList());
            if ( null != costCategory && costCategory.getName().contains("日常") ){
                WorkflowRequestTableField = new WorkflowRequestTableField[expenseMappingConfigList.size()];
                for (int i = 0; i < expenseMappingConfigList.size(); i++) {
                    WorkflowRequestTableField[i] = new WorkflowRequestTableField();
                    OpenEtlMappingConfig config = expenseMappingConfigList.get(i);
                    String sourceName = config.getSrcCol();
                    WorkflowRequestTableField[i].setFieldName(config.getTgtCol());
                    WorkflowRequestTableField[i].setView(true);//字段是否可见
                    WorkflowRequestTableField[i].setEdit(true);//字段是否可编辑
                    // 费用类别1、费用类别2、费用发生时间、费用金额、单据张数、发票
                    if ("expenseTypeOne".equals(sourceName)){
                        Map result = (Map) costDetailMap.get("费用类别1");
                        int value = ExpenseLargeTypeEnum.getType((String) result.get("value")).getType();
                        WorkflowRequestTableField[i].setFieldValue( value+"" );
                    }
                    if ("expenseTypeTwo".equals(sourceName)){
                        Map result = (Map) costDetailMap.get("费用类别2");
                        String fbtValue = (String) result.get("value");
                        int value = 0;
                        // 费用小类是其他的,单独处理
                        if (OTHER.equals(fbtValue)){
                            Map detailResult = (Map) costDetailMap.get("费用类别1");
                            String typeOne = (String) detailResult.get("value");
                            if (ExpenseLargeTypeEnum.OFFLINE.getDesc().equals(typeOne)){
                                value = ExpenseSmallTypeEnum.TYPE_14.getType();
                            }
                            if (ExpenseLargeTypeEnum.APP.getDesc().equals(typeOne)){
                                value = ExpenseSmallTypeEnum.TYPE_19.getType();
                            }
                            if (ExpenseLargeTypeEnum.TYPE_11.getDesc().equals(typeOne)){
                                value = ExpenseSmallTypeEnum.TYPE_80.getType();
                            }
                            if (ExpenseLargeTypeEnum.TYPE_12.getDesc().equals(typeOne)){
                                value = ExpenseSmallTypeEnum.TYPE_88.getType();
                            }
                            if (ExpenseLargeTypeEnum.TYPE_13.getDesc().equals(typeOne)){
                                value = ExpenseSmallTypeEnum.TYPE_95.getType();
                            }
                        } else {
                            value = ExpenseSmallTypeEnum.getType((String) result.get("value")).getType();
                        }
                        WorkflowRequestTableField[i].setFieldValue( value+"" );
                    }
                    if ("expenseOccurTime".equals(sourceName)){
                        WorkflowRequestTableField[i].setFieldValue((String)costDetailMap.get("费用发生时间"));
                    }
                    if ("expenseAmount".equals(sourceName)){
                        WorkflowRequestTableField[i].setFieldValue(string2Integer((String)costDetailMap.get("费用金额")));
                    }
                    if ("voucherNum".equals(sourceName)){
                        WorkflowRequestTableField[i].setFieldValue(string2Integer((String)costDetailMap.get("单据张数")));
                    }
                    if ("invoice".equals(sourceName)){
                        List<CustomReimbursementNoticeDTO.ReturnInfo> invoiceList = costCategory.getInvoiceList();
                        if (CollectionUtils.isNotBlank(invoiceList)){
                            String invPicUrl = invoiceList.get(0).getInvPicUrl();
                            FileUrlValueBuilder.buildFileUrls(invPicUrl,WorkflowRequestTableField[i]);
                        }
                    }
                }
            }
        } catch (Exception e){
            log.info("转换日常费失败 : {}",e.getMessage());
        }
        return WorkflowRequestTableField;
    }

}
