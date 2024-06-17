package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.kit;


import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApprovalFormDTO;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.IFormFieldAliasConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.support.apply.dto.CostAttributionDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenApplyService;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.openapi.sdk.dto.common.TypeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DingtalkCarKitApplyFormParserServiceImpl extends AbstractDingtalkParseApplyService {

    @Autowired
    private IOpenApplyService openApplyService;

    /**
     * 钉钉ISV用车套件表单解析
     * @param bizData ：表单集合数据
     * @param instanceId
     * @return
     */
    public CommonApplyReqDTO parser(String token ,String  bizData , String instanceId , String orgId , String orgName , List<Map<String,Object>> bussinessTimeList, boolean useOriginal) {
        DingtalkApprovalFormDTO dingtalkIsvCarApprovalFormDTO = JsonUtils.toObj(bizData, DingtalkApprovalFormDTO.class);
        List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList = dingtalkIsvCarApprovalFormDTO.getFormValueVOS();
        //构建公用审批数据
        CommonApply commonApply = buildApply(  instanceId ,  orgId ,  orgName );
        CommonApplyTrip commonApplyTrip = buildCommonTrip();
        List<CommonApplyTrip> commonApplyTripList = new ArrayList<>();
        List<CostAttributionDTO> costAttributionList =  new ArrayList<>();
        Map<String , Object> carMap =  new HashMap<>();
        parseFormInfo(  formValueVOSList ,  commonApply ,  commonApplyTrip , costAttributionList ,  carMap , useOriginal);
        commonApply.setCostAttributionList( costAttributionList );
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        commonApplyTripList.add(commonApplyTrip);
        commonApplyReqDTO.setTripList(commonApplyTripList);
        ArrayList<TypeEntity> taxiRuleList = getTaxiRule(token , NumericUtils.obj2int( carMap.get("carCount") ), NumericUtils.obj2double( carMap.get("carAmount")) , commonApply);
        commonApplyReqDTO.setApplyTaxiRuleInfo(taxiRuleList);
        commonApplyReqDTO.setApply(commonApply);
        return commonApplyReqDTO;
    }

    /**
     *
     * @param formValueVOSList :表单数据
     * @param commonApply ：申请单基本信息
     * @param commonApplyTrip ：行程信息
     * @param costAttributionList ：费用归属信息
     * @param carMap ：用车规则
     */
    private void parseFormInfo(List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList , CommonApply commonApply , CommonApplyTrip commonApplyTrip ,
                               List<CostAttributionDTO> costAttributionList , Map<String , Object> carMap, boolean useOriginal){
        if(CollectionUtils.isBlank( formValueVOSList )) {
            return;
        }
        formValueVOSList.forEach( formValueVOS ->  {
            String bizAlias = formValueVOS.getBizAlias();
            if(StringUtils.isBlank(bizAlias)){
                return ;
            }
            switch (bizAlias){
                case IFormFieldAliasConstant.CAR_LEAVE_TYEP://申请事由
                    commonApply.setApplyReason( formValueVOS.getValue() );
                    break;
                case IFormFieldAliasConstant.CAR_SUBTEXTAREA_FIELD://事由补充
                    String reasonDesc = formValueVOS.getValue();
                    commonApply.setApplyReasonDesc(reasonDesc);
                    commonApply.setThirdRemark(reasonDesc);
                    break;
                case IFormFieldAliasConstant.CAR_CITY://用车城市
                    commonApplyTrip.setStartCityId( getCityList( formValueVOS.getExtValue() ) );
                    break;
                case IFormFieldAliasConstant.CAR_TIME_SECTION://开始时间和结束时间
                    buildApplyTripTime(  formValueVOS.getValue()  ,  commonApplyTrip );
                    break;
                case IFormFieldAliasConstant.CAR_CAR_FREQUENCY://用车次数
                    buildCarCount( formValueVOS.getExtValue() ,  carMap );
                    break;
                case IFormFieldAliasConstant.CAR_MONEY_FIELD://用车金额
                    carMap.put("carAmount" ,  formValueVOS.getValue() );
                    break;
                case IFormFieldAliasConstant.CAR_COST_DEPARTMENT_FIELD://费用归属部门
                    if(useOriginal){
                        getCostDeaprtmentList( formValueVOS.getExtValue() , costAttributionList);
                    }else{
                        getCostDeaprtmentListNew( formValueVOS.getExtValue() , costAttributionList);
                    }
                    break;
                case IFormFieldAliasConstant.CAR_COST_PROJECT_FIELD://费用归属项目
                    getCostProjectList( formValueVOS.getExtValue() , costAttributionList);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 审批单基本信息
     * @param instanceId 第三方审批单id
     * @param orgId ：用户所属部门id
     * @param orgName：用户所属部门名称
     * @return
     */
    private CommonApply buildApply( String instanceId , String orgId , String orgName ){
        CommonApply commonApply = new CommonApply();
        commonApply.setThirdId(instanceId);
        commonApply.setType(SaasApplyType.ApplyTaxi.getValue());
        commonApply.setFlowType(4);
        commonApply.setCostAttributionId(orgId);
        commonApply.setCostAttributionName(orgName);
        commonApply.setCostAttributionCategory(1);
        return commonApply;
    }


    private CommonApplyTrip buildCommonTrip(){
        CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
        commonApplyTrip.setType(OrderCategoryEnum.Taxi.getKey());
        return commonApplyTrip;
    }

    /**
     * 用车时间
     * @param value
     * @param commonApplyTrip
     */
    private void buildApplyTripTime( String value , CommonApplyTrip commonApplyTrip){
        if(StringUtils.isBlank( value )){
            return ;
        }
        //设置日期格式
        String cityValue = StringEscapeUtils.unescapeJava( value );
        List<String> timtList = JsonUtils.toObj(cityValue, List.class , String.class);
        if(!CollectionUtils.isBlank( timtList ) && timtList.size()>=2 ){
            commonApplyTrip.setStartTime( timtList.get(0) );
            commonApplyTrip.setEndTime( timtList.get(1) );
        }
    }


    private void buildCarCount( String extValue , Map<String , Object> carMap){
        if(StringUtils.isBlank( extValue )) {
            return ;
        }
        Map map = JsonUtils.toObj(extValue, Map.class);
        carMap.put("carCount" ,  map.get("key") );
    }


    /**
     *
     * @param ucToken
     * @param carUseCount ：用车次数
     * @param carAmount ：用车金额
     * @return
     */
    private ArrayList<TypeEntity> getTaxiRule(String ucToken , int carUseCount , double carAmount , CommonApply commonApply){
        Map<String, Object> queryDetail = openApplyService.getQueryDetailByToken( ucToken );
        ArrayList<TypeEntity> taxiRuleList = new ArrayList<TypeEntity>();
        if(MapUtils.isBlank(queryDetail)){
            queryDetail = MapUtils.newHashMap();
        }
        commonApply.setRuleId( StringUtils.obj2str( queryDetail.get("id") ) );
        taxiRuleList.add( TypeEntity.builder().type("allowed_taxi_type").value( queryDetail.get("allowed_taxi_type") == null ? "" : queryDetail.get("allowed_taxi_type")).build() ); //限制用车类型
        //同城限制
        taxiRuleList.add( TypeEntity.builder().type("allow_same_city").value( queryDetail.get("allow_same_city") == null ? false : queryDetail.get("allow_same_city") ).build() );
        //用车费用限制
        taxiRuleList.add( TypeEntity.builder().type("price_limit_flag").value( queryDetail.get("price_limit_flag") == null ? 0 : queryDetail.get("price_limit_flag") ).build() );
        //单次限制
        taxiRuleList.add( TypeEntity.builder().type("price_limit").value( queryDetail.get("price_limit") == null ? -1 : queryDetail.get("price_limit") ).build() );
        //单日限制
        taxiRuleList.add( TypeEntity.builder().type("day_price_limit").value( queryDetail.get("day_price_limit") == null ? -1 : queryDetail.get("day_price_limit") ).build() );
        //员工填写金额
        taxiRuleList.add( TypeEntity.builder().type("total_price").value( carAmount ).build() );
        //调度费
        taxiRuleList.add( TypeEntity.builder().type("taxi_scheduling_fee").value( queryDetail.get("taxi_scheduling_fee") == null ? -1 : queryDetail.get("taxi_scheduling_fee") ).build() );
        //是否允许为他人叫车
        taxiRuleList.add( TypeEntity.builder().type("allow_called_for_other").value( queryDetail.get("allow_called_for_other") == null ? true : queryDetail.get("allow_called_for_other") ).build() );
        //用车次数限制类型
        taxiRuleList.add( TypeEntity.builder().type("times_limit_flag").value(  queryDetail.get("times_limit_flag") == null ? 0 : queryDetail.get("times_limit_flag") ).build() );
        //限制次数
        taxiRuleList.add( TypeEntity.builder().type("times_limit").value(  carUseCount ).build() );
        //城市限制  1限制，0不限制
        taxiRuleList.add( TypeEntity.builder().type("city_limit").value(  queryDetail.get("city_limit") == null ? 0 : queryDetail.get("city_limit") ).build() );
        return taxiRuleList;

    }

}
