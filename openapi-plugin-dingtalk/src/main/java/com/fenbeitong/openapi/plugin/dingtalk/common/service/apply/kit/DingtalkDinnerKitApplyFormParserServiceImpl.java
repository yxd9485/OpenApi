package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.kit;

import com.fenbeitong.openapi.plugin.dingtalk.common.constant.IFormFieldAliasConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApprovalFormDTO;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.util.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DingtalkDinnerKitApplyFormParserServiceImpl  extends AbstractDingtalkParseApplyService{

    /**
     * 钉钉ISV用餐套件表单解析
     * @param bizData ：表单集合数据
     * @param instanceId
     * @return
     */
    public DinnerApproveCreateReqDTO parserDinnerForm(String  bizData , String instanceId , String orgId , String orgName, boolean useOriginal) {
        DingtalkApprovalFormDTO dingtalkIsvApprovalFormDTO = JsonUtils.toObj(bizData, DingtalkApprovalFormDTO.class);
        List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList = dingtalkIsvApprovalFormDTO.getFormValueVOS();
        DinnerApproveCreateReqDTO commonApplyReqDTO = new DinnerApproveCreateReqDTO();
        List<CostAttributionDTO> costAttributionList =  new ArrayList<>();
        //构建公用审批数据
        DinnerApproveApply commonApply = buildApply(  instanceId ,  orgId ,  orgName );
        List<DinnerApproveDetail> dinnerApproveDetails = parseFormInfo(formValueVOSList, commonApply, costAttributionList , useOriginal);
        commonApply.setCostAttributionList( costAttributionList );
        commonApplyReqDTO.setApply( commonApply );
        commonApplyReqDTO.setTripList(dinnerApproveDetails);
        return commonApplyReqDTO;
    }

    /**
     * 审批单基本信息
     * @param instanceId 第三方审批单id
     * @param orgId ：用户所属部门id
     * @param orgName：用户所属部门名称
     * @return
     */
    private DinnerApproveApply buildApply( String instanceId , String orgId , String orgName ){
        DinnerApproveApply commonApply = new DinnerApproveApply();
        commonApply.setThirdId(instanceId);
        commonApply.setType(SaasApplyType.Meishi.getValue());
        commonApply.setFlowType(4);
        commonApply.setBudget(0);
        commonApply.setCostAttributionId(orgId);
        commonApply.setCostAttributionName(orgName);
        commonApply.setCostAttributionCategory(1);
        return commonApply;
    }

    /**
     *
     * @param formValueVOSList :表单数据
     * @param commonApply ：申请单基本信息
     * @param costAttributionList ：费用归属信息
     */
    private List<DinnerApproveDetail> parseFormInfo(List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList , DinnerApproveApply commonApply , List<CostAttributionDTO> costAttributionList, boolean useOriginal){
        if(CollectionUtils.isBlank( formValueVOSList )) {
            return null;
        }
        DinnerApproveDetail detail = new DinnerApproveDetail();
        Map<String,Object> mapDinnerTime = MapUtils.newHashMap();
        formValueVOSList.forEach( formValueVOS ->  {
            String bizAlias = formValueVOS.getBizAlias();
            if(StringUtils.isBlank(bizAlias)){
                return ;
            }
            switch (bizAlias){
                case IFormFieldAliasConstant.DINNER_REASON://申请事由
                    commonApply.setApplyReason( formValueVOS.getValue() );
                    break;
                case IFormFieldAliasConstant.DINNER_REASON_SUPPLEMENT://事由补充
                    commonApply.setApplyReasonDesc( formValueVOS.getValue() );
                    break;
                case IFormFieldAliasConstant.DINNER_COST://用餐总费用
                    String value = formValueVOS.getValue();
                    int estimatedAmount = NumericUtils.obj2int(value) * 100;
                    detail.setEstimatedAmount(estimatedAmount);
                    commonApply.setBudget(estimatedAmount);
                    break;
                case IFormFieldAliasConstant.DINNER_PERSON://用餐人数
                    detail.setPersonCount(NumericUtils.obj2int(formValueVOS.getValue()));
                    break;
                case IFormFieldAliasConstant.DINNER_CITY://用餐城市
                    String cityExtendValue = formValueVOS.getExtValue();
                    String cityList = getCityList(cityExtendValue);
                    detail.setStartCityId( cityList );
                    break;
                case IFormFieldAliasConstant.DINNER_START_TIME ://用餐开始时间
                    mapDinnerTime.put("dinnerStartTime" , formValueVOS.getValue());
                    break;
                case IFormFieldAliasConstant.DINNER_END_TIME://用餐结束时间
                    mapDinnerTime.put("dinnerEndTime" , formValueVOS.getValue());
                    break;
                case IFormFieldAliasConstant.DINNER_TIME://用餐日期
                    mapDinnerTime.put("dinnerTime" , formValueVOS.getValue());
                    break;
                case IFormFieldAliasConstant.DINNER_INTREVAL://用餐时段
                    String extValue = formValueVOS.getExtValue();
                    mapDinnerTime.put("dinnerInterval" , extValue);
                    break;
                case IFormFieldAliasConstant.DINNER_COST_DEPARTMENT://费用归属部门
                    if(useOriginal){
                        getCostDeaprtmentList( formValueVOS.getExtValue() , costAttributionList);
                    }else{
                        getCostDeaprtmentListNew( formValueVOS.getExtValue() , costAttributionList);
                    }

                    break;
                case IFormFieldAliasConstant.DINNER_COST_PROJECT://费用归属项目
                    getCostProjectList( formValueVOS.getExtValue() , costAttributionList);
                    break;
                default:
                    break;
            }
        });
        List<DinnerApproveDetail> tripList = Lists.newArrayList();
        buildDinnerTime( mapDinnerTime , detail );
        tripList.add( detail );
        return tripList;
    }


    /**
     * 用餐时段
     */
    private void buildDinnerTime(Map<String,Object> mapDinnerTime , DinnerApproveDetail detail) {
        String dinnerStartTime = StringUtils.obj2str( mapDinnerTime.get("dinnerStartTime") );
        String dinnerEndTime = StringUtils.obj2str( mapDinnerTime.get("dinnerEndTime") );
        String dinnerTime = StringUtils.obj2str( mapDinnerTime.get("dinnerTime") );
        String dinnerInterval = StringUtils.obj2str( mapDinnerTime.get("dinnerInterval") );
        Map map = JsonUtils.toObj(dinnerInterval, Map.class);
        String startTime = (StringUtils.isBlank(dinnerStartTime) ? dinnerTime : dinnerStartTime)  + " " +StringUtils.obj2str(map.get("startTime"));
        String endTime = (StringUtils.isBlank(dinnerEndTime) ? dinnerTime : dinnerEndTime) + " " +StringUtils.obj2str(map.get("endTime"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            detail.setStartTime( sdf.parse(startTime) );
            detail.setEndTime( sdf.parse(endTime) );
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCityList(String cityExtendValue){
        if(StringUtils.isBlank(cityExtendValue)){
            return null;
        }
        Map<String,Object> city = JsonUtils.toObj(cityExtendValue,  Map.class);
        String cityId = StringUtils.obj2str( city.get("id") );
        return cityId;
    }

}
