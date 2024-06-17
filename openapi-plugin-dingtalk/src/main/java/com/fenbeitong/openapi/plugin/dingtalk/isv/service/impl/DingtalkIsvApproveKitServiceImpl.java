package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.fenbeitong.fenbeipay.api.constant.enums.cashier.OrderType;
import com.fenbeitong.openapi.plugin.core.entity.KvEntity;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApproveKitResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApproveKitResultEntity;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkKitConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkKitFieldConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkKitValueConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.IFormFieldAliasConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkTripKitTravelTimeDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.*;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvApproveKitService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvKitUtils;
import com.fenbeitong.openapi.plugin.support.apply.constant.TripRoundType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.support.apply.dto.MultiTripDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenApplyService;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.orgunit.CompanyOrgUnitDTO;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 钉钉市场版自定义审批套件
 *
 * @author xiaohai
 * @date 2021/08/23
 */
@Service
@Slf4j
public class DingtalkIsvApproveKitServiceImpl implements IDingtalkIsvApproveKitService {

    @Autowired
    private IOpenApplyService openApplyService;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private DingtalkIsvKitUtils dingtalkIsvKitUtils;

    @DubboReference(check = false)
    private IOrgUnitService orgUnitService;

    @Value("${host.dd_webapp}")
    private String webappHost;

    /**
     * 用车初始化
     * @param request
     * @return
     */
    @Override
    public List<IFormFieldDTO> getCarInitData(HttpServletRequest request) {
        List<IFormFieldDTO> formFieldDTOList = new ArrayList<>();
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData( request );
        Map<String,Object> param = setReqParam( dingtalkIsvKitReqDTO );
        //用于判断是否必填
        Map<String, Object> applyConfig = openApplyService.getApplyConfig( param );
        //申请事由
        dingtalkIsvKitUtils.getApplyReasonsInfo(param , formFieldDTOList , applyConfig , DingTalkKitConstant.KitType.CAR_TYPE);
        //费用归属
        Object applyAttributionCategory = applyConfig.get("apply_attribution_category_taxi");
        IFormDeptAndProDTO deptAndProDTO = IFormDeptAndProDTO.builder().department(IFormFieldAliasConstant.CAR_COST_DEPARTMENT_FIELD)
            .project(IFormFieldAliasConstant.CAR_COST_PROJECT_FIELD).tab(IFormFieldAliasConstant.CAR_COST_TAB_FIELD).build();
        dingtalkIsvKitUtils.getCostAttrbutionCategory( param ,  formFieldDTOList ,  deptAndProDTO , StringUtils.obj2str( applyAttributionCategory) , dingtalkIsvKitReqDTO);
        return formFieldDTOList;
    }

    /**
     *  用车城市
     *  费用归属项目（部门）
     *  用车规则
     * @param request
     * @return
     */
    @Override
    public List<IFormFieldDTO> getCarRefreshData(HttpServletRequest request) {
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData( request );
        String bizAlias = dingtalkIsvKitReqDTO.getBizAlias();
        Object extendsValues = null ;
        List<IFormFieldDTO> formFieldDTOList = new ArrayList<>();
        if(IFormFieldAliasConstant.CAR_CITY.equals(bizAlias)){
            //用车城市
            Map<String,Object> map = getExtendsValue( dingtalkIsvKitReqDTO ,  bizAlias );
            extendsValues = openApplyService.getCarCityList( map );
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( bizAlias ,JsonUtils.toJson( extendsValues) ) );
        }else if(IFormFieldAliasConstant.CAR_RULE.equals(bizAlias)){
            //用车规则
            Map<String,Object> param = new HashMap<>();
            param.put("userId" , getThirdEmployeeId(  dingtalkIsvKitReqDTO.getCompanyId() ,  dingtalkIsvKitReqDTO.getUserId() ));
            param.put("companyId" , dingtalkIsvKitReqDTO.getCompanyId());
            extendsValues = openApplyService.getQueryDetail( param );
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( bizAlias ,JsonUtils.toJson( extendsValues) ) );
        }else if(IFormFieldAliasConstant.CAR_PROJECT_FIELD.equals(bizAlias) || IFormFieldAliasConstant.CAR_COST_PROJECT_FIELD.equals(bizAlias)){
            //费用归属所在项目列表
            costProject(  dingtalkIsvKitReqDTO , bizAlias , formFieldDTOList );
        }else if(IFormFieldAliasConstant.CAR_COST_DEPARTMENT_FIELD.equals(bizAlias)){
            List<CompanyOrgUnitDTO> companyOrgUnitDTOS = orgUnitService.queryOrgUnitList(dingtalkIsvKitReqDTO.getCompanyId());
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.CAR_COST_DEPARTMENT_FIELD ,JsonUtils.toJson( companyOrgUnitDTOS ) ) );
        }
        return formFieldDTOList;
    }

    /**
     * 用车校验接口
     * @param request
     * @return
     */
    @Override
    public DingtalkApproveKitResultEntity checkSubmitData(HttpServletRequest request) {
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData(request);
        Map<String,Object> param = setReqParam( dingtalkIsvKitReqDTO );
        Map<String, IFormFieldDTO> formFieldMap = dingtalkIsvKitReqDTO.getBizDataMap();
        param.put("type" , Integer.valueOf(DingTalkKitConstant.KitType.CAR_TYPE));
        String reasonVal = formFieldMap.containsKey( IFormFieldAliasConstant.CAR_LEAVE_TYEP ) ? formFieldMap.get(IFormFieldAliasConstant.CAR_LEAVE_TYEP).getValue() : "";
        String reasonSupplementVal = formFieldMap.containsKey( IFormFieldAliasConstant.CAR_SUBTEXTAREA_FIELD ) ? formFieldMap.get(IFormFieldAliasConstant.CAR_SUBTEXTAREA_FIELD).getValue() : "";
        if(!checkApplyReason(  param , reasonVal , reasonSupplementVal)){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.CONFIGURATION_ERROR);
        }
        String deptVal = formFieldMap.containsKey(IFormFieldAliasConstant.CAR_COST_DEPARTMENT_FIELD) ? formFieldMap.get(IFormFieldAliasConstant.CAR_COST_DEPARTMENT_FIELD).getValue() : "";
        String proVal = formFieldMap.containsKey(IFormFieldAliasConstant.CAR_COST_PROJECT_FIELD) ? formFieldMap.get(IFormFieldAliasConstant.CAR_COST_PROJECT_FIELD).getValue() : "";
        Map<String, Object> applyConfig = openApplyService.getApplyConfig( param );
        Object applyAttributionCategory = applyConfig.get("apply_attribution_category_taxi") ;
        if( !checkApplyCostAttribution( dingtalkIsvKitReqDTO  ,  applyAttributionCategory ,  deptVal ,  proVal )){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.CONFIGURATION_ERROR);
        }
        return DingtalkApproveKitResponseUtils.success(null);
    }

    /**
     * 差旅初始化
     * @param request
     * @return
     */
    @Override
    public List<IFormFieldDTO> getTripInitData(HttpServletRequest request) {
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData( request );
        Map<String,Object> param = setReqParam( dingtalkIsvKitReqDTO );
        String userId =  dingtalkIsvKitReqDTO.getUserId();
        List<IFormFieldDTO> formFieldDTOList = new ArrayList<>();
        //用于判断是否必填
        Map<String, Object> applyConfig = openApplyService.getApplyConfig( param );
        //申请事由
        dingtalkIsvKitUtils.getApplyReasonsInfo(param , formFieldDTOList , applyConfig , DingTalkKitConstant.KitType.TRAVEL_TYPE);
        //费用归属
        Object applyAttributionCategory = applyConfig.get("apply_attribution_category_travel");
        IFormDeptAndProDTO deptAndProDTO = IFormDeptAndProDTO.builder().department(IFormFieldAliasConstant.TRAVEL_COST_DEPARTMENT)
            .project(IFormFieldAliasConstant.TRAVEL_COST_PROJECT_TEST).tab(IFormFieldAliasConstant.TRAVEL_COST_TAB_FIELD).build();
        dingtalkIsvKitUtils.getCostAttrbutionCategory( param ,  formFieldDTOList ,  deptAndProDTO , StringUtils.obj2str( applyAttributionCategory) , dingtalkIsvKitReqDTO);
        // 1-按行程填写申请单,2-仅填写城市、日期、出行方式等信息
        Object applyTripType = applyConfig.get("apply_trip_type");
        if(DingTalkKitConstant.ApplyTripType.APPLY_TRIP.equals(StringUtils.obj2str(applyTripType))){
            //非行程明细控件隐藏
            TravelDetaileDTO customField = getTripDetail(applyConfig);
            formFieldDTOList.add( dingtalkIsvKitUtils.setValueAndExtends( IFormFieldAliasConstant.CUSTOMER_FIELD , null ,JsonUtils.toJson(customField), false , false));
            formFieldDTOList.add( dingtalkIsvKitUtils.setInvisible( IFormFieldAliasConstant.TRAVEL_NO_DETAILRD , true ));
        }else if(DingTalkKitConstant.ApplyTripType.APPLY_MULITY_TRIP.equals(StringUtils.obj2str(applyTripType))){
            //行程明细控件隐藏
            TravelDetaileDTO customField = getMulitiTripDetail(applyConfig);
            formFieldDTOList.add( dingtalkIsvKitUtils.setValueAndExtends( IFormFieldAliasConstant.TRAVEL_NO_DETAILRD , null , JsonUtils.toJson(customField) , false , false));
            formFieldDTOList.add( dingtalkIsvKitUtils.setInvisible( IFormFieldAliasConstant.CUSTOMER_FIELD , true ));
        }
        //出差时间
        travelBussinessTime(param, userId , formFieldDTOList);
        Map<String,Object> map = MapUtils.newHashMap();
        map.put("corpId" , dingtalkIsvKitReqDTO.getCorpId() );
        map.put("url" , webappHost );
        formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.CORP_ID_TEXT , JsonUtils.toJson(map)));
        //用车是否显示
        dingtalkIsvKitUtils.checkTaxiRule(formFieldDTOList ,  dingtalkIsvKitReqDTO.getUserId() , dingtalkIsvKitReqDTO.getCompanyId());
        return formFieldDTOList;
    }

    /**
     * 差旅刷新
     * @param request
     * @return
     */
    @Override
    public List<IFormFieldDTO> getTripRefreshData(HttpServletRequest request) {
        //解析请求参数
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData( request );
        String bizAlias = dingtalkIsvKitReqDTO.getBizAlias();
        List<IFormFieldDTO> formFieldDTOList = new ArrayList<>();
        if(IFormFieldAliasConstant.CUSTOMER_FIELD.equals(bizAlias)){
            //自定义组件 城市、规则
            travelNewDetailedData(  dingtalkIsvKitReqDTO ,  formFieldDTOList ,  request);
        } else if( IFormFieldAliasConstant.TRAVEL_COST_PROJECT_TEST.equals(bizAlias) ) {
            //费用归属项目列表
            costProject(  dingtalkIsvKitReqDTO , bizAlias , formFieldDTOList );
        }else if(IFormFieldAliasConstant.TRAVEL_COST_DEPARTMENT.equals(bizAlias)){
            List<CompanyOrgUnitDTO> companyOrgUnitDTOS = orgUnitService.queryOrgUnitList(dingtalkIsvKitReqDTO.getCompanyId());
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.TRAVEL_COST_DEPARTMENT ,JsonUtils.toJson( companyOrgUnitDTOS ) ) );
        }else if(IFormFieldAliasConstant.TRAVEL_NO_DETAILRD.equals(bizAlias)){
            //非行程明细套件
            mulityDetailedData(  dingtalkIsvKitReqDTO  , formFieldDTOList);
        }
        return formFieldDTOList;
    }

    /**
     * 差旅校验接口
     * @param request
     * @return
     */
    @Override
    public DingtalkApproveKitResultEntity checkSubmitTripData(HttpServletRequest request) {
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData(request);
        Map<String,Object> param = setReqParam( dingtalkIsvKitReqDTO );
        Map<String, IFormFieldDTO> formFieldMap = dingtalkIsvKitReqDTO.getBizDataMap();
        param.put("type" , Integer.valueOf(DingTalkKitConstant.KitType.TRAVEL_TYPE));
        String reasonVal = formFieldMap.containsKey( IFormFieldAliasConstant.APPLY_SUBJECT ) ? formFieldMap.get(IFormFieldAliasConstant.APPLY_SUBJECT).getValue() : "";
        String reasonSupplementVal = formFieldMap.containsKey( IFormFieldAliasConstant.SUBJECT_SUPPLEMENT ) ? formFieldMap.get(IFormFieldAliasConstant.SUBJECT_SUPPLEMENT).getValue() : "";
        if(!checkApplyReason( param , reasonVal , reasonSupplementVal)){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.CONFIGURATION_ERROR);
        }
        //行程城市和金额校验
        Map<String, Object> applyConfig = openApplyService.getApplyConfig( param );
        //查询审批单是行程还是非行程
        Object applyTripType = applyConfig.get("apply_trip_type");
        String tripCarExtValue = formFieldMap.containsKey( IFormFieldAliasConstant.TRAVEL_OR_CAR ) ? formFieldMap.get(IFormFieldAliasConstant.TRAVEL_OR_CAR).getExtendValue() : "";
        if(DingTalkKitConstant.ApplyTripType.APPLY_TRIP.equals(StringUtils.obj2str(applyTripType))){
            //行程
            String tripListExtValue = formFieldMap.containsKey( IFormFieldAliasConstant.CUSTOMER_FIELD ) ? formFieldMap.get(IFormFieldAliasConstant.CUSTOMER_FIELD).getExtendValue() : "";
            DingtalkApproveKitResultEntity dingtalkApproveKitResultEntity = checkTripInfo( tripCarExtValue ,tripListExtValue, applyConfig);
            if(!dingtalkApproveKitResultEntity.getSuccess()){
                return dingtalkApproveKitResultEntity;
            }
            //判断出差时间是否是必填
            Object travelStatistics = openApplyService.getTravelStatistics( param );
            Map<String,Object> travelStatisticsMap = JsonUtils.toObj(JsonUtils.toJson(travelStatistics), Map.class);
            if(!MapUtils.isBlank(travelStatisticsMap)){
                // 1:必填 0:不必填
                String whetherRequired = StringUtils.obj2str( travelStatisticsMap.get("whether_required") );
                String travelTimeJson = formFieldMap.containsKey( IFormFieldAliasConstant.TRAVEL_BUSINESS_TIME ) ? formFieldMap.get(IFormFieldAliasConstant.TRAVEL_BUSINESS_TIME).getExtendValue() : "";
                if(StringUtils.obj2str(DingTalkKitConstant.FIELD_REQUIRED).equals( whetherRequired )){
                    DingtalkApproveKitResultEntity dingtalkApproveKitResult = checkTravelTime(travelTimeJson);
                    if(!dingtalkApproveKitResult.getSuccess()){
                        return dingtalkApproveKitResult;
                    }
                }
            }
        }else if(DingTalkKitConstant.ApplyTripType.APPLY_MULITY_TRIP.equals(StringUtils.obj2str(applyTripType))){
            //非行程
            String mulitiTripExtValue = formFieldMap.containsKey( IFormFieldAliasConstant.TRAVEL_NO_DETAILRD ) ? formFieldMap.get(IFormFieldAliasConstant.TRAVEL_NO_DETAILRD).getExtendValue() : "";
            DingtalkApproveKitResultEntity dingtalkApproveKitResultEntity = checkMultiTripInfo(tripCarExtValue, mulitiTripExtValue, applyConfig);
            if(!dingtalkApproveKitResultEntity.getSuccess()){
                return dingtalkApproveKitResultEntity;
            }
            //出差时间校验必填
            String travelTimeJson = formFieldMap.containsKey( IFormFieldAliasConstant.TRAVEL_BUSINESS_TIME ) ? formFieldMap.get(IFormFieldAliasConstant.TRAVEL_BUSINESS_TIME).getExtendValue() : "";
            DingtalkApproveKitResultEntity dingtalkApproveKitResult = checkTravelTime(travelTimeJson);
            if(!dingtalkApproveKitResult.getSuccess()){
                return dingtalkApproveKitResult;
            }
        }
        String deptVal = formFieldMap.containsKey(IFormFieldAliasConstant.TRAVEL_COST_DEPARTMENT) ? formFieldMap.get(IFormFieldAliasConstant.TRAVEL_COST_DEPARTMENT).getValue() : "";
        String proVal = formFieldMap.containsKey(IFormFieldAliasConstant.TRAVEL_COST_PROJECT_TEST) ? formFieldMap.get(IFormFieldAliasConstant.TRAVEL_COST_PROJECT_TEST).getValue() : "";
        Object applyAttributionCategory = applyConfig.get("apply_attribution_category_travel") ;
        if( !checkApplyCostAttribution( dingtalkIsvKitReqDTO  ,  applyAttributionCategory ,  deptVal ,  proVal )){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.CONFIGURATION_ERROR);
        }
        return DingtalkApproveKitResponseUtils.success(null);
    }

    private DingtalkApproveKitResultEntity checkTravelTime(String travelTimeJson ){
        travelTimeJson = StringEscapeUtils.unescapeJava(travelTimeJson);
        DingtalkTripKitTravelTimeDTO kitTravelTimeDTO = JsonUtils.toObj(travelTimeJson, DingtalkTripKitTravelTimeDTO.class);
        if (ObjectUtils.isEmpty(kitTravelTimeDTO)){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.TRAVEL_TIME_NOT_EMPTY);
        }
        String startTime = kitTravelTimeDTO.getStartTime();
        if(StringUtils.isBlank(startTime)){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.TRAVEL_TIME_NOT_EMPTY);
        }
        if(StringUtils.isBlank(kitTravelTimeDTO.getEndTime())){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.TRAVEL_TIME_NOT_EMPTY);
        }
        return DingtalkApproveKitResponseUtils.success(null);

    }

    private DingtalkApproveKitResultEntity checkTripInfo(String tripCarExtValue, String tripListExtValue , Map<String, Object> applyConfig ){
        if(StringUtils.isBlank( tripListExtValue )){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.TRIP_INFO_ERROR);
        }
        // 行程表单信息
        String tripListValue = StringEscapeUtils.unescapeJava(tripListExtValue);
        List<CommonApplyTrip> tripList = JsonUtils.toObj(tripListValue, List.class , CommonApplyTrip.class);
        if(CollectionUtils.isBlank(tripList)){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.TRIP_SCENE_ERROR);
        }
        String applyTripCity = StringUtils.obj2str (applyConfig.get("apply_trip_city") );
        //显示且必填
        boolean cityRequired = DingTalkKitConstant.ApplyTripCity.DISPLAY_AND_REQUIRED.equals( applyTripCity ) ? true : false;
        String tripApplyBuget = StringUtils.obj2str (applyConfig.get("whether_trip_apply_budget"));
        //预估费用显示且必填
        boolean budgetRequired = DingTalkKitConstant.TripApplyBuget.DISPLAY_AND_REQUIRED.equals(tripApplyBuget) ? true : false;
        for (CommonApplyTrip trip : tripList) {
            DingtalkApproveKitResultEntity dingtalkApproveKitResultEntity = checkTripTime(trip);
            if (!dingtalkApproveKitResultEntity.getSuccess()) {
                return dingtalkApproveKitResultEntity;
            }
            DingtalkApproveKitResultEntity checkCityAndBudget = checkCityAndBudget(trip, cityRequired, budgetRequired);
            if(!checkCityAndBudget.getSuccess()){
                return checkCityAndBudget;
            }
        }
        //判断是否有城市数据
        List<String> startCityIds = tripList.stream().filter((CommonApplyTrip trip) -> !StringUtils.isBlank(trip.getStartCityId()) ).map(trip -> trip.getStartCityId()).collect(Collectors.toList());
        DingtalkApproveKitResultEntity dingtalkApproveKitResult = checkCarCity(startCityIds, tripCarExtValue, applyTripCity);
        if(!dingtalkApproveKitResult.getSuccess()){
            return dingtalkApproveKitResult;
        }
        return DingtalkApproveKitResponseUtils.success(null);
    }

    private DingtalkApproveKitResultEntity checkCityAndBudget(CommonApplyTrip trip , boolean cityRequired , boolean budgetRequired){
        String startCityId = trip.getStartCityId();
        String arrivalCityId = trip.getArrivalCityId();
        Integer estimatedAmount = trip.getEstimatedAmount();
        if (cityRequired && trip.getType() == OrderCategoryEnum.Hotel.getKey()) {
            if (StringUtils.isBlank(startCityId)) {
                return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.HOTEL_CITY_NOT_EMPTY);
            }
        }
        if (cityRequired && trip.getType() != OrderCategoryEnum.Hotel.getKey() && (StringUtils.isBlank(startCityId) || StringUtils.isBlank(arrivalCityId))) {
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.START_AND_ENDCITY_NOT_EMPTY);
        }
        if (budgetRequired && estimatedAmount == null) {
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.ESTIMATED_AMOUNT_NOT_EMPTY);
        }
        return DingtalkApproveKitResponseUtils.success(null);
    }

    private DingtalkApproveKitResultEntity checkCarCity(List<String> startCityIds , String tripCarExtValue ,String applyTripCity ){
        Boolean useCar = Optional.ofNullable(tripCarExtValue)
            .map(value -> JsonUtils.toObj(value, Map.class))
            .map(map -> map.get(DingtalkKitFieldConstant.FIELD_KEY))
            .map(Object::toString)
            .map(DingtalkKitValueConstant.USE_CAR_VALUE::equals)
            .orElse(Boolean.FALSE);
        if( DingTalkKitConstant.ApplyTripCity.NOT_DISPLAY.equals( applyTripCity ) && useCar){
            //差旅行程中不展示城市，则无法生成用车，提示用户将生成用车修改为否
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.NOT_USER_CAR);
        }else if( CollectionUtils.isBlank(startCityIds) && useCar){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.USE_AND_NOCITY);
        }
        return DingtalkApproveKitResponseUtils.success(null);
    }

    //时间必填校验
    private DingtalkApproveKitResultEntity checkTripTime(CommonApplyTrip trip){
        Integer tripType = trip.getTripType();
        String startTime = trip.getStartTime();
        String endTime = trip.getEndTime();
        if(tripType != null && TripRoundType.RoungTrip.getValue() == tripType  && (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime))){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.START_AND_ENDTIME_NOT_EMPTY);
        }else if(OrderCategoryEnum.Hotel.getKey() == trip.getType()  && (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime))){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.HOTEL_TIME_NOT_EMPTY);
        }else if(StringUtils.isBlank(startTime)){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.START_TIME_NOT_EMPTY);
        }
        return DingtalkApproveKitResponseUtils.success(null);
    }

    private DingtalkApproveKitResultEntity checkMultiTripInfo(String tripCarExtValue, String mulitiTripExtValue , Map<String, Object> applyConfig ){
        if(StringUtils.isBlank( mulitiTripExtValue )){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.TRIP_INFO_ERROR);
        }
        String applyTripCity = StringUtils.obj2str (applyConfig.get("apply_trip_city") );
        //显示且必填
        boolean cityRequired = DingTalkKitConstant.ApplyTripCity.DISPLAY_AND_REQUIRED.equals( applyTripCity ) ? true : false;
        String tripApplyBuget = StringUtils.obj2str (applyConfig.get("whether_trip_apply_budget"));
        //预估费用显示且必填
        boolean budgetRequired = DingTalkKitConstant.TripApplyBuget.DISPLAY_AND_REQUIRED.equals(tripApplyBuget) ? true : false;
        // 非行程表单信息
        String tripListValue = StringEscapeUtils.unescapeJava(mulitiTripExtValue);
        MultiTripDTO multiTripDTO = JsonUtils.toObj(tripListValue,  MultiTripDTO.class);
        if(multiTripDTO == null){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.TRIP_INFO_ERROR);
        }
        List<Integer> multiTripScene = multiTripDTO.getMultiTripScene();
        if(CollectionUtils.isBlank(multiTripScene)){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.MULTI_TRIP_SCENE_ERROR);
        }
        List<KvEntity> multiTripCity = multiTripDTO.getMultiTripCity();
        //判断是否有城市数据
        List<String> startCityIds = multiTripCity.stream().filter((KvEntity city) -> !StringUtils.isBlank(city.getValue())).map(trip -> trip.getValue()).collect(Collectors.toList());
        DingtalkApproveKitResultEntity dingtalkApproveKitResult = checkCarCity(startCityIds, tripCarExtValue, applyTripCity);
        if(!dingtalkApproveKitResult.getSuccess()){
            return dingtalkApproveKitResult;
        }
        if(!cityRequired && !budgetRequired){
            return DingtalkApproveKitResponseUtils.success(null);
        }else{
            List<Integer> multiTripScenes = multiTripDTO.getMultiTripScene();
            if(CollectionUtils.isBlank(multiTripCity) && cityRequired){
               return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.CITY_NOT_EMPTY);
            }else if(cityRequired && !CollectionUtils.isBlank(multiTripCity) && multiTripCity.size()==1 && !(multiTripScenes.size()==1 && multiTripScenes.get(0) == OrderCategoryEnum.Hotel.getKey() )){
                return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.STARTCITY_AND_ENDCITY_NOT_EMPTY);
            }
            if(budgetRequired && multiTripDTO.getEstimatedAmount() == null){
                return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.ESTIMATED_AMOUNT_NOT_EMPTY);
            }
        }
        return DingtalkApproveKitResponseUtils.success(null);
    }

    /**
     * 用餐初始化
     * @param request
     * @return
     */
    @Override
    public List<IFormFieldDTO> getDinnerInitData(HttpServletRequest request) {
        //申请事由、事由补充、申请单日期（精确，范围）、费用归属
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData( request );
        Map<String,Object> param = new HashMap<>();
        param.put("companyId" , dingtalkIsvKitReqDTO.getCompanyId());
        String userId =  dingtalkIsvKitReqDTO.getUserId();
        List<IFormFieldDTO> formFieldDTOList = new ArrayList<>();
        //用于判断是否必填
        Map<String, Object> applyConfig = openApplyService.getApplyConfig( param );
        //申请事由
        dingtalkIsvKitUtils.getApplyReasonsInfo(param , formFieldDTOList , applyConfig , DingTalkKitConstant.KitType.DINNER_TYPE);
        //费用归属
        Object applyAttributionCategory = applyConfig.get("apply_attribution_category_meishi");
        IFormDeptAndProDTO deptAndProDTO = IFormDeptAndProDTO.builder().department(IFormFieldAliasConstant.DINNER_COST_DEPARTMENT).project(IFormFieldAliasConstant.DINNER_COST_PROJECT).tab(IFormFieldAliasConstant.DINNER_DEP_PROJ_TAB).build();
        dingtalkIsvKitUtils.getCostAttrbutionCategory( param ,  formFieldDTOList ,  deptAndProDTO , StringUtils.obj2str( applyAttributionCategory) , dingtalkIsvKitReqDTO);
        // 使用日期  0-精确时间 1-范围时间
        setTimeInverval(applyConfig.get("apply_meishi_time_limit") ,  formFieldDTOList );
        return formFieldDTOList;
    }

    /**
     * 精确时间和范围时间
     * @param timeLimit  0-精确时间 1-范围时间
     * @param formFieldDTOList
     */
    public void setTimeInverval(Object timeLimit , List<IFormFieldDTO> formFieldDTOList ) {
        if(DingTalkKitConstant.PRECISE_TIME.equals( timeLimit )){
            //精确时间  DINNER_START_TIME、DINNER_END_TIME 隐藏 DINNER_TIME 显示
            formFieldDTOList.add( dingtalkIsvKitUtils.setReqAndInvisible( IFormFieldAliasConstant.DINNER_START_TIME , false , true));
            formFieldDTOList.add( dingtalkIsvKitUtils.setReqAndInvisible( IFormFieldAliasConstant.DINNER_END_TIME  , false ,true));
            formFieldDTOList.add( dingtalkIsvKitUtils.setReqAndInvisible( IFormFieldAliasConstant.DINNER_TIME  , true ,false));
        }else if(DingTalkKitConstant.RANGE_TIME.equals( timeLimit )){
            //范围时间  DINNER_START_TIME、DINNER_END_TIME 显示 DINNER_TIME 隐藏
            formFieldDTOList.add( dingtalkIsvKitUtils.setReqAndInvisible( IFormFieldAliasConstant.DINNER_START_TIME , true , false));
            formFieldDTOList.add( dingtalkIsvKitUtils.setReqAndInvisible( IFormFieldAliasConstant.DINNER_END_TIME  , true ,false));
            formFieldDTOList.add( dingtalkIsvKitUtils.setReqAndInvisible( IFormFieldAliasConstant.DINNER_TIME  ,false ,true));
        }
    }

    /**
     * 用餐刷新
     * @param request
     * @return
     */
    @Override
    public List<IFormFieldDTO> getDinnerRefreshData(HttpServletRequest request) {
        //解析请求参数
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData( request );
        String bizAlias = dingtalkIsvKitReqDTO.getBizAlias();
        List<IFormFieldDTO> formFieldDTOList = new ArrayList<>();
        //自定义组件
        if(IFormFieldAliasConstant.DINNER_CITY.equals(bizAlias)) {
            //用餐城市
            Map<String,Object> map = getExtendsValue( dingtalkIsvKitReqDTO ,  bizAlias );
            Object extendsValues = openApplyService.getDinnerCityList( map );
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( bizAlias ,JsonUtils.toJson( extendsValues) ) );
        }else if( IFormFieldAliasConstant.DINNER_COST_PROJECT.equals(bizAlias)){
            //费用归属项目
            costProject(  dingtalkIsvKitReqDTO ,  bizAlias, formFieldDTOList );
        }else if(IFormFieldAliasConstant.DINNER_RULE.equals(bizAlias)){
            //用餐规则
            Map<String,Object> param = new HashMap<>();
            param.put("companyId" , dingtalkIsvKitReqDTO.getCompanyId());
            param.put("userId" , getThirdEmployeeId(  dingtalkIsvKitReqDTO.getCompanyId() ,  dingtalkIsvKitReqDTO.getUserId() ));
            Object rules = openApplyService.getTripRule( param );
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.DINNER_RULE ,JsonUtils.toJson( rules ) ) );
        }else if(IFormFieldAliasConstant.DINNER_COST_DEPARTMENT.equals(bizAlias)){
            List<CompanyOrgUnitDTO> companyOrgUnitDTOS = orgUnitService.queryOrgUnitList(dingtalkIsvKitReqDTO.getCompanyId());
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.DINNER_COST_DEPARTMENT ,JsonUtils.toJson( companyOrgUnitDTOS ) ) );
        }
        return formFieldDTOList;
    }

    @Override
    public DingtalkApproveKitResultEntity getDinnerSubmitData(HttpServletRequest request) {
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData(request);
        Map<String,Object> param = setReqParam( dingtalkIsvKitReqDTO );
        Map<String, IFormFieldDTO> formFieldMap = dingtalkIsvKitReqDTO.getBizDataMap();
        param.put("type" , Integer.valueOf(DingTalkKitConstant.KitType.DINNER_TYPE));
        String reasonVal = formFieldMap.containsKey( IFormFieldAliasConstant.DINNER_REASON ) ? formFieldMap.get(IFormFieldAliasConstant.DINNER_REASON).getValue() : "";
        String reasonSupplementVal = formFieldMap.containsKey( IFormFieldAliasConstant.DINNER_REASON_SUPPLEMENT ) ? formFieldMap.get(IFormFieldAliasConstant.DINNER_REASON_SUPPLEMENT).getValue() : "";
        if(!checkApplyReason(  param , reasonVal , reasonSupplementVal)){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.CONFIGURATION_ERROR);
        }
        String deptVal = formFieldMap.containsKey(IFormFieldAliasConstant.DINNER_COST_DEPARTMENT) ? formFieldMap.get(IFormFieldAliasConstant.DINNER_COST_DEPARTMENT).getValue() : "";
        String proVal = formFieldMap.containsKey(IFormFieldAliasConstant.DINNER_COST_PROJECT) ? formFieldMap.get(IFormFieldAliasConstant.DINNER_COST_PROJECT).getValue() : "";
        Map<String, Object> applyConfig = openApplyService.getApplyConfig( param );
        Object applyAttributionCategory = applyConfig.get("apply_attribution_category_meishi") ;
        if( !checkApplyCostAttribution( dingtalkIsvKitReqDTO  ,  applyAttributionCategory ,  deptVal ,  proVal )){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.CONFIGURATION_ERROR);
        }
        return DingtalkApproveKitResponseUtils.success(null);
    }

    /**
     * 外卖初始化
     * @param request
     * @return
     */
    @Override
    public List<IFormFieldDTO> getTakeawayInitData(HttpServletRequest request) {
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData( request );
        Map<String,Object> param = setReqParam( dingtalkIsvKitReqDTO );
        List<IFormFieldDTO> formFieldDTOList = new ArrayList<>();
        //用于判断是否必填
        Map<String, Object> applyConfig = openApplyService.getApplyConfig( param );
        //申请事由
        dingtalkIsvKitUtils.getApplyReasonsInfo(param , formFieldDTOList , applyConfig , DingTalkKitConstant.KitType.TAKEAWAY_TYPE);
        //费用归属
        Object applyAttributionCategory = applyConfig.get("apply_attribution_category_takeaway");
        IFormDeptAndProDTO deptAndProDTO = IFormDeptAndProDTO.builder().department(IFormFieldAliasConstant.TAKEAWAY_COST_DEPARTMENT)
            .project(IFormFieldAliasConstant.TAKEAWAY_COST_PROJECT).tab(IFormFieldAliasConstant.TAKEAWAY_COST_TAB_FIELD).build();
        dingtalkIsvKitUtils.getCostAttrbutionCategory( param ,  formFieldDTOList ,  deptAndProDTO , StringUtils.obj2str( applyAttributionCategory) , dingtalkIsvKitReqDTO);
        return formFieldDTOList;
    }

    @Override
    public List<IFormFieldDTO> getTakeawayRefreshData(HttpServletRequest request) {
        //解析请求参数
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData( request );
        String bizAlias = dingtalkIsvKitReqDTO.getBizAlias();
        List<IFormFieldDTO> formFieldDTOList = new ArrayList<>();
        //外卖规则
        if(IFormFieldAliasConstant.TAKEAWAY_RULE.equals(bizAlias)) {
            //用餐规则
            Map<String,Object> param = new HashMap<>();
            param.put("companyId" , dingtalkIsvKitReqDTO.getCompanyId());
            param.put("userId" , getThirdEmployeeId(  dingtalkIsvKitReqDTO.getCompanyId() ,  dingtalkIsvKitReqDTO.getUserId() ));
            Object rules = openApplyService.getTripRule( param );
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.TAKEAWAY_RULE ,JsonUtils.toJson( rules ) ) );
        }else if( IFormFieldAliasConstant.TAKEAWAY_COST_PROJECT.equals(bizAlias)){
            //费用归属项目
            costProject(  dingtalkIsvKitReqDTO ,  bizAlias, formFieldDTOList );
        }else if(IFormFieldAliasConstant.TAKEAWAY_ADDRESS.equals(bizAlias)){
            //用餐地址
            Map<String,Object> param = new HashMap<>();
            param.put("companyId" , dingtalkIsvKitReqDTO.getCompanyId());
            param.put("employeeId" , getThirdEmployeeId(  dingtalkIsvKitReqDTO.getCompanyId() ,  dingtalkIsvKitReqDTO.getUserId() ));
            Map<String, Object> addressMap = openApplyService.getTakeawayAddress( param );
            AddressReqDTO addressReqDTO = handleAddress(addressMap);
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.TAKEAWAY_ADDRESS ,JsonUtils.toJson( addressReqDTO ) ) );
        }else if(IFormFieldAliasConstant.TAKEAWAY_COST_DEPARTMENT.equals(bizAlias)){
            List<CompanyOrgUnitDTO> companyOrgUnitDTOS = orgUnitService.queryOrgUnitList(dingtalkIsvKitReqDTO.getCompanyId());
            formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.TAKEAWAY_COST_DEPARTMENT ,JsonUtils.toJson( companyOrgUnitDTOS ) ) );
        }
        return formFieldDTOList;
    }

    private AddressReqDTO handleAddress(Map<String, Object> addressMap){
        AddressReqDTO addressReqDTO = JsonUtils.toObj(JsonUtils.toJson(addressMap), AddressReqDTO.class);
        List<AddressDetailReqDTO> companyAddressList = addressReqDTO.getCompanyAddressList();
        if(CollectionUtils.isBlank(companyAddressList)){
            return addressReqDTO;
        }
        Map<String, AddressDetailReqDTO> companyAddressMap = companyAddressList.stream().collect(Collectors.toMap(AddressDetailReqDTO::getId, Function.identity(), (key1, key2) -> key2));
        boolean haveCompanyAddress = addressReqDTO.isHaveCompanyAddress();
        if(haveCompanyAddress){
            List<AddressDetailReqDTO> employeeAddressList = addressReqDTO.getEmployeeAddressList();
            if(CollectionUtils.isBlank(companyAddressList)){
                return addressReqDTO;
            }
            List<AddressDetailReqDTO> addressList = CollectionUtils.newArrayList();
            employeeAddressList.forEach( addressDetailReqDTO -> {
                if(!StringUtils.isBlank(addressDetailReqDTO.getCompanyAddressId())){
                    AddressDetailReqDTO companyAddress = companyAddressMap.get(addressDetailReqDTO.getCompanyAddressId());
                    addressDetailReqDTO.setCityName(companyAddress.getCityName());
                    addressDetailReqDTO.setAddress(companyAddress.getAddress());
                    addressDetailReqDTO.setCityCode(companyAddress.getCityCode());
                    addressDetailReqDTO.setCityName(companyAddress.getCityName());
                    addressDetailReqDTO.setLat(companyAddress.getLat());
                    addressDetailReqDTO.setLng(companyAddress.getLng());
                }
                addressList.add(addressDetailReqDTO);
            });
            addressReqDTO.setEmployeeAddressList(addressList);
        }
        return addressReqDTO;
    }

    @Override
    public DingtalkApproveKitResultEntity getTakeawaySubmitData(HttpServletRequest request) {
        DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO = parseRequestData(request);
        Map<String,Object> param = setReqParam( dingtalkIsvKitReqDTO );
        //申请事由、事由补充、费用归属项目和部门
        Map<String, IFormFieldDTO> formFieldMap = dingtalkIsvKitReqDTO.getBizDataMap();
        param.put("type" , Integer.valueOf(DingTalkKitConstant.KitType.TAKEAWAY_TYPE));
        String reasonVal = formFieldMap.containsKey( IFormFieldAliasConstant.TAKEAWAY_REASON ) ? formFieldMap.get(IFormFieldAliasConstant.TAKEAWAY_REASON).getValue() : "";
        String reasonSupplementVal = formFieldMap.containsKey( IFormFieldAliasConstant.TAKEAWAY_REASON_SUPPLEMENT ) ? formFieldMap.get(IFormFieldAliasConstant.TAKEAWAY_REASON_SUPPLEMENT).getValue() : "";
        if(!checkApplyReason(  param , reasonVal , reasonSupplementVal)){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.CONFIGURATION_ERROR);
        }
        String deptVal = formFieldMap.containsKey(IFormFieldAliasConstant.TAKEAWAY_COST_DEPARTMENT) ? formFieldMap.get(IFormFieldAliasConstant.TAKEAWAY_COST_DEPARTMENT).getValue() : "";
        String proVal = formFieldMap.containsKey(IFormFieldAliasConstant.TAKEAWAY_COST_PROJECT) ? formFieldMap.get(IFormFieldAliasConstant.TAKEAWAY_COST_PROJECT).getValue() : "";
        Map<String, Object> applyConfig = openApplyService.getApplyConfig( param );
        Object applyAttributionCategory = applyConfig.get("apply_attribution_category_takeaway") ;
        if( !checkApplyCostAttribution( dingtalkIsvKitReqDTO  ,  applyAttributionCategory ,  deptVal ,  proVal )){
            return DingtalkApproveKitResponseUtils.error(DingTalkKitConstant.ErrorTip.CONFIGURATION_ERROR);
        }
        return DingtalkApproveKitResponseUtils.success(null);
    }


    /**
     * 申请事由和事由补充
     *
     * @return
     */
    private boolean checkApplyReason( Map<String,Object> param , String  reasonVal , String reasonSupplementVal){
        //查询申请事由列表数据
        Map<String, Object> applyReasons = openApplyService.getApplyReasons( param );
        Object reason = applyReasons.get("reason");
        Object reasonDesc = applyReasons.get("reason_desc");
        if(DingTalkKitConstant.FIELD_REQUIRED.equals(reason) && StringUtils.isBlank(reasonVal)){
            return false;
        }
        if(DingTalkKitConstant.FIELD_REQUIRED.equals(reasonDesc) && StringUtils.isBlank(reasonSupplementVal)){
            return false;
        }
        return true;
    }

    /**
     * 费用归属
     * @param dingtalkIsvKitReqDTO
     * @param applyAttributionCategory
     * @param deptVal
     * @param proVal
     * @return
     */
    private boolean checkApplyCostAttribution(DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO  , Object applyAttributionCategory , String deptVal , String proVal ){
        Map<String,Object> param = setReqParam( dingtalkIsvKitReqDTO );
        if(DingTalkKitConstant.CostAttribution.DISPLAY_AND_REQUIRED.equals( StringUtils.obj2str(applyAttributionCategory) )){
            Map<String, Object> queryCostAttrbution = openApplyService.getCostAttribution( param );
            Object costAttributionScope = queryCostAttrbution.get("costAttributionScope");
            //1：两者都必填 2：两者选其一
            if(DingTalkKitConstant.CostAttribution.COST_ATTRBUTION_SCOPE.equals( StringUtils.obj2str(costAttributionScope) )){
                if(StringUtils.isBlank(deptVal) && StringUtils.isBlank(proVal)) {
                    return false;
                }
            }else{
                //两者都必填
                if(StringUtils.isBlank(deptVal) || StringUtils.isBlank(proVal)) {
                    return false;
                }
            }
        }
        return true;
    }

    //行程明细刷新数据
    private void travelNewDetailedData( DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO  , List<IFormFieldDTO> formFieldDTOList , HttpServletRequest request){
        Map<String,Object> map = getExtendsValue( dingtalkIsvKitReqDTO ,  IFormFieldAliasConstant.CUSTOMER_FIELD);
        String type = StringUtils.obj2str( map.get("type") );
        Object cityValue = null ;
        List<IFormFieldDTO> customFormFieldDTOList = new ArrayList<>();
        int orderTypeInt = type != null ? Integer.valueOf(type) : -1;
        OrderType ot = OrderType.getEnum(orderTypeInt);
        switch (ot) {
            case Air:
                //国内机票
                cityValue =  openApplyService.getAirCityList( map );
                break;
            case Hotel:
                //酒店列表
                cityValue =  openApplyService.getHotelCityList( map );
                break;
            case Train:
                //火车列表
                cityValue =  openApplyService.getTrainCityList( map )  ;
                break;
            default:
                //用车规则
                Map<String,Object> param = new HashMap<>();
                param.put("companyId" , dingtalkIsvKitReqDTO.getCompanyId());
                param.put("userId" , getThirdEmployeeId(  dingtalkIsvKitReqDTO.getCompanyId() ,  dingtalkIsvKitReqDTO.getUserId() ));
                Object tripRule = openApplyService.getTripRule( param );
                customFormFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.TRAVEL_RULE ,JsonUtils.toJson( tripRule) ) );
                break;
        }
        customFormFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.SET_OUT_CITY ,JsonUtils.toJson( cityValue) ) );
        formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.CUSTOMER_FIELD ,JsonUtils.toJson( customFormFieldDTOList) ) );
    }

    //非行程明细刷新数据
    private void mulityDetailedData( DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO  , List<IFormFieldDTO> formFieldDTOList){
        Map<String,Object> map = getExtendsValue( dingtalkIsvKitReqDTO ,  IFormFieldAliasConstant.TRAVEL_NO_DETAILRD);
        Object commonTripCityList = openApplyService.getCommonTripCityList(map);
        List<IFormFieldDTO> customFormFieldDTOList = new ArrayList<>();
        customFormFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.TRAVEL_NO_SET_OUT_CITY ,JsonUtils.toJson( commonTripCityList) ) );
        formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( IFormFieldAliasConstant.TRAVEL_NO_DETAILRD ,JsonUtils.toJson( customFormFieldDTOList) ) );
    }

    /**
     * 费用归属项目数据
     * @param dingtalkIsvKitReqDTO
     * @param formFieldDTOList
     */
    private void costProject( DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO , String bizAlias , List<IFormFieldDTO> formFieldDTOList ){
        Map<String,Object> map = getExtendsValue( dingtalkIsvKitReqDTO ,  bizAlias);
        map.put("state" , DingTalkKitConstant.ProjectInfo.PROJECT_STATUS);
        map.put("seeRange" , DingTalkKitConstant.ProjectInfo.PROJECT_SEERANGE);
        map.put("type" , DingTalkKitConstant.ProjectInfo.USER_TYPE);
        map.put("userId" , dingtalkIsvKitReqDTO.getUserId());
        //获取分贝通的公司id
        map.put("companyId" , getFbtCompanyId(  dingtalkIsvKitReqDTO.getCorpId() ));
        //项目列表
        Object extendsValues =  openApplyService.getProjectList( map );
        formFieldDTOList.add( dingtalkIsvKitUtils.setExtendsValue( dingtalkIsvKitReqDTO.getBizAlias() ,JsonUtils.toJson( extendsValues) ) );
    }

    /**
     * 差旅行程明细（差旅城市、出发日期、预估费用）
     * @param applyConfig
     * @return
     */
    public TravelDetaileDTO  getTripDetail(  Map<String, Object> applyConfig ){
        //城市1-显示且必填 2-显示非必填 3-不显示
        String applyTripCity = StringUtils.obj2str (applyConfig.get("apply_trip_city") );
        //城市选填必填
        boolean cityRequired = DingTalkKitConstant.ApplyTripCity.DISPLAY_AND_REQUIRED.equals( applyTripCity ) ? true : false;
        //城市显示不显示(默认显示)
        boolean cityInvisible = DingTalkKitConstant.ApplyTripCity.NOT_DISPLAY.equals( applyTripCity ) ? true : false;
        IFormFieldDTO setOutCity = dingtalkIsvKitUtils.setValue(IFormFieldAliasConstant.SET_OUT_CITY, "", cityRequired, cityInvisible);
        //到达城市
        IFormFieldDTO objectiveCity = dingtalkIsvKitUtils.setValue(IFormFieldAliasConstant.OBJECTIVE_CITY, "", cityRequired, cityInvisible);
        //出发日期 0-精确时间（天） 1-范围时间
        String applyDepartureDate = StringUtils.obj2str( applyConfig.get("apply_departure_date") );
        IFormFieldDTO travelTimeInterval = dingtalkIsvKitUtils.setValue( IFormFieldAliasConstant.TRAVEL_TIME_INTERVAL ,applyDepartureDate , null , null);
        //预估费用 0-不显示 1-显示且必填
        String tripApplyBuget = StringUtils.obj2str (applyConfig.get("whether_trip_apply_budget"));
        IFormFieldDTO travelMoneyField = dingtalkIsvKitUtils.setValue( IFormFieldAliasConstant.TRAVEL_MONEY_FIELD ,"" ,  DingTalkKitConstant.TripApplyBuget.DISPLAY_AND_REQUIRED.equals( tripApplyBuget) ? true : false  ,  DingTalkKitConstant.TripApplyBuget.DISPLAY_AND_REQUIRED.equals( tripApplyBuget) ? false : true);
        return TravelDetaileDTO.builder().setOutCity(setOutCity).objectiveCity(objectiveCity).travelTimeInterval(travelTimeInterval).travelMoneyField(travelMoneyField).build();
    }

    /**
     * 差旅非行程明细（差旅城市、出发日期、预估费用）
     * @param applyConfig
     * @return
     */
    public TravelDetaileDTO getMulitiTripDetail(  Map<String, Object> applyConfig ){
        //城市1-显示且必填 2-显示非必填 3-不显示
        String applyTripCity = StringUtils.obj2str (applyConfig.get("apply_trip_city") );
        //城市选填必填
        boolean cityRequired = DingTalkKitConstant.ApplyTripCity.DISPLAY_AND_REQUIRED.equals( applyTripCity ) ? true : false;
        //城市显示不显示(默认显示)
        boolean cityInvisible = DingTalkKitConstant.ApplyTripCity.NOT_DISPLAY.equals( applyTripCity ) ? true : false;
        IFormFieldDTO travelNoSetOutCity = dingtalkIsvKitUtils.setValue(IFormFieldAliasConstant.TRAVEL_NO_SET_OUT_CITY, "", cityRequired, cityInvisible);
        //预估费用 0-不显示 1-显示且必填
        String tripApplyBuget = StringUtils.obj2str (applyConfig.get("whether_trip_apply_budget"));
        IFormFieldDTO travelNoMoneyField = dingtalkIsvKitUtils.setValue( IFormFieldAliasConstant.TRAVEL_NO_MONEY_FIELD ,"" ,  DingTalkKitConstant.TripApplyBuget.DISPLAY_AND_REQUIRED.equals( tripApplyBuget) ? true : false  ,  DingTalkKitConstant.TripApplyBuget.DISPLAY_AND_REQUIRED.equals( tripApplyBuget) ? false : true);
        return TravelDetaileDTO.builder().travelNoSetOutCity(travelNoSetOutCity).travelNoMoneyField(travelNoMoneyField).build();
    }

    private void travelBussinessTime(Map<String,Object> param , String userId , List<IFormFieldDTO> formFieldDTOList ){
        //出差时间
        Object travelStatistics = openApplyService.getTravelStatistics( param );
        Map<String,Object> travelStatisticsMap = JsonUtils.toObj(JsonUtils.toJson(travelStatistics), Map.class);
        if(travelStatisticsMap == null){
            //查询失败
            formFieldDTOList.add( dingtalkIsvKitUtils.setValueAndExtends( IFormFieldAliasConstant.TRAVEL_BUSINESS_TIME ,"" ,null,false , false ));
        }
        // 1:必填 0:不必填
        String whetherRequired = StringUtils.obj2str( travelStatisticsMap.get("whether_required") );
        //0:关闭 1:开启
        String whetherTravelStaticsRequired = StringUtils.obj2str( travelStatisticsMap.get("whether_travel_statistics") );

        getThirdEmployeeId( StringUtils.obj2str( param.get("companyId") ) ,  userId );
        param.put( "user_id" , userId );
        //查询历史出差记录
        Object historyTravelStatistics = openApplyService.getHistoryTravelStatistics( param );
        //必填
        String required = StringUtils.obj2str(DingTalkKitConstant.FIELD_REQUIRED);
        formFieldDTOList.add( dingtalkIsvKitUtils.setValueAndExtends( IFormFieldAliasConstant.TRAVEL_BUSINESS_TIME ,"" , JsonUtils.toJson( historyTravelStatistics )  , required.equals( whetherRequired )? true : false  ,  DingTalkKitConstant.TravelStatics.TRAVEL_STATICS_CLOSE.equals(whetherTravelStaticsRequired) ? true : false ));
        formFieldDTOList.add( dingtalkIsvKitUtils.setValueAndExtends( IFormFieldAliasConstant.TRAVEL_ALL_NUMBER ,"" , JsonUtils.toJson( historyTravelStatistics )  , required.equals( whetherRequired )? true : false  ,  DingTalkKitConstant.TravelStatics.TRAVEL_STATICS_CLOSE.equals(whetherTravelStaticsRequired) ? true : false ));
    }

    /**
     * 获取extendsValues值
     * @param dingtalkIsvKitReqDTO
     * @param bizAlias
     * @return
     */
    private Map<String,Object> getExtendsValue( DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO , String bizAlias ){
        IFormFieldDTO iFormFieldDTO = dingtalkIsvKitReqDTO.getBizDataMap().get(bizAlias);
        String extendValue = iFormFieldDTO.getExtendValue();
        Map<String,Object> map = JsonUtils.toObj(extendValue, Map.class);
        if(MapUtils.isBlank(map)){
            return MapUtils.newHashMap();
        }
        return map;
    }

    /**
     * 解析request中提交的数据
     * @param request
     * @return
     */
    private DingtalkIsvKitReqDTO parseRequestData( HttpServletRequest request ){
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] bizAsyncData = parameterMap.get("bizAsyncData");
        String[] userIds = parameterMap.get("userId");
        String[] corpIds = parameterMap.get("corpId");
        if ( userIds == null || userIds.length==0 ||  userIds == null ||  corpIds.length==0 ) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        String userId = userIds[0];
        String corpId =  corpIds[0];
        //通过corpId查询companyId
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        String companyId = dingtalkIsvCompany.getCompanyId();
        String reqData = "";
        String bizAlias = "";
        Map<String, IFormFieldDTO> bizDataMap = new HashMap<>();
        if(bizAsyncData != null ) {
            reqData = bizAsyncData[0];
            List<IFormFieldDTO> bizDataList = JsonUtils.toObj(bizAsyncData[0], List.class , IFormFieldDTO.class);
            bizDataMap = bizDataList.stream().collect(Collectors.toMap(IFormFieldDTO::getBizAlias, Function.identity(), (key1, key2) -> key2));
            if(!CollectionUtils.isBlank( bizDataList ) && bizDataList.size()>0){
                bizAlias = bizDataList.get(0).getBizAlias();
            }
        }
        return DingtalkIsvKitReqDTO.builder().reqData(reqData).userId(userId).companyId(companyId).corpId(corpId).bizAlias(bizAlias).bizDataMap(bizDataMap).build();
    }

    private Map<String,Object> setReqParam( DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO){
        Map<String,Object> param = new HashMap<>();
        param.put("companyId" , dingtalkIsvKitReqDTO.getCompanyId());
        return param ;
    }

    private String getFbtCompanyId( String corpId){
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        return dingtalkIsvCompany.getCompanyId();
    }

    //获取三方人员id
    private String getThirdEmployeeId( String companyId , String userId ){
        List<String> ids = new ArrayList<>();
        ids.add(userId);
        List<CommonIdDTO> commonIdDTOS = commonService.queryIdDTO(companyId, ids, 2, 3);
        if(commonIdDTOS == null || commonIdDTOS.size()<=0) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_USER_NOT_EXISTS);
        }
        return commonIdDTOS.get(0).getThirdId();
    }

}
