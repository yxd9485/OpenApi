package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.kit;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApprovalFormDTO;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkKitFieldConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkKitValueConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.IFormFieldAliasConstant;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkTripCommonApplyDTO;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkTripKitTravelTimeDTO;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.constant.TripRoundType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.support.common.service.impl.OpenIdTranServiceImpl;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DingtalkTripKitApplyFormParserServiceImpl extends AbstractDingtalkParseApplyService {
    @Autowired
    private OpenIdTranServiceImpl openIdTranService;

    /**
     * 钉钉ISV差旅套件表单解析
     * @param bizData ：钉钉同步表单详情数据
     * @param commonApplyReqDTO 组装通用的申请单请求DTO
     */
    public void parser(String  bizData , CommonApplyReqDTO commonApplyReqDTO ) {
        DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO = JsonUtils.toObj(bizData, DingtalkApprovalFormDTO.class);
        List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList = dingtalkIsvTripApprovalFormDTO.getFormValueVOS();
        parseFormInfo(formValueVOSList, commonApplyReqDTO);
    }

    /**
     * 审批单基本信息
     * @param instanceId 第三方审批单id
     * @param orgId ：用户所属部门id
     * @param orgName：用户所属部门名称
     * @return
     */
    public DingtalkTripCommonApplyDTO buildApply( String instanceId , String orgId , String orgName ){
        DingtalkTripCommonApplyDTO commonApply = new DingtalkTripCommonApplyDTO();
        commonApply.setThirdId(instanceId);
        commonApply.setType(SaasApplyType.ChaiLv.getValue());
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
     * @param commonApplyReqDTO ：申请单信息
     */
    private void parseFormInfo(List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList,
                               CommonApplyReqDTO commonApplyReqDTO) {
        if (CollectionUtils.isBlank(formValueVOSList)) {
            return;
        }
        DingtalkTripCommonApplyDTO commonApplyDTO = (DingtalkTripCommonApplyDTO) commonApplyReqDTO.getApply();
        List<CostAttributionDTO> costAttributionList = new ArrayList<>();
        formValueVOSList.forEach(formValueVO -> {
            String bizAlias = formValueVO.getBizAlias();
            if(StringUtils.isBlank(bizAlias)){
                return ;
            }
            switch (bizAlias) {
                //申请事由
                case IFormFieldAliasConstant.APPLY_SUBJECT:
                    commonApplyDTO.setApplyReason(formValueVO.getValue());
                    break;
                //事由补充
                case IFormFieldAliasConstant.SUBJECT_SUPPLEMENT:
                    String reasonDesc = formValueVO.getValue();
                    commonApplyDTO.setApplyReasonDesc(reasonDesc);
                    commonApplyDTO.setThirdRemark(reasonDesc == null ? "备注" : reasonDesc);
                    break;
                //是否用车
                case IFormFieldAliasConstant.TRAVEL_OR_CAR:
                    commonApplyDTO.setUseCarFlag(bulidUseCarFlag(formValueVO.getExtValue()));
                    break;
                //差旅行程
                case IFormFieldAliasConstant.CUSTOMER_FIELD:
                    commonApplyReqDTO.setTripList(buildTripList(formValueVO.getExtValue()));
                    break;
                //差旅非行程
                case IFormFieldAliasConstant.TRAVEL_NO_DETAILRD:
                    commonApplyReqDTO.setMultiTrip(buildMultiTrip(formValueVO.getExtValue()));
                    break;
                //出行人
                case IFormFieldAliasConstant.TRAVEL_TRAVELER:
                    commonApplyReqDTO.setGuestList(buildApplyGuestList(formValueVO.getExtValue(), commonApplyDTO.getCompanyId()));
                    break;
                //出差时间
                case IFormFieldAliasConstant.TRAVEL_BUSINESS_TIME:
                    String extValue = formValueVO.getExtValue();
                    parseBussinessTimeNew(extValue, commonApplyDTO);
                    break;
                //出差时长
                case IFormFieldAliasConstant.TRAVEL_ALL_NUMBER:
                    if (!StringUtils.isBlank(formValueVO.getExtValue())){
                        commonApplyDTO.setTravelDay(new BigDecimal(formValueVO.getExtValue()));
                    }
                    break;
                //费用归属部门
                case IFormFieldAliasConstant.TRAVEL_COST_DEPARTMENT:
                    getCostDeaprtmentListNew(formValueVO.getExtValue(), costAttributionList);
                    break;
                //费用归属项目
                case IFormFieldAliasConstant.TRAVEL_COST_PROJECT_TEST:
                    getCostProjectList(formValueVO.getExtValue(), costAttributionList);
                    break;
                default:
                    break;
            }
        });
        commonApplyDTO.setCostAttributionList(costAttributionList);
    }

    /**
     * 解析表单是否用车字段
     * @param extValue 是否用车表单数据
     * @return 是否用车
     */
    private Boolean bulidUseCarFlag(String extValue) {
        return Optional.ofNullable(extValue)
            .map(value -> JsonUtils.toObj(value, Map.class))
            .map(map -> map.get(DingtalkKitFieldConstant.FIELD_KEY))
            .map(Object::toString)
            .map(DingtalkKitValueConstant.USE_CAR_VALUE::equals)
            .orElse(Boolean.FALSE);
    }


    /**
     * 组装申请单行程信息
     */
    private List<CommonApplyTrip> buildTripList(String tripListExtValue) {
        if(StringUtils.isBlank( tripListExtValue )){
            return null;
        }
        // 行程表单信息
        String tripListValue = StringEscapeUtils.unescapeJava(tripListExtValue);
        List<CommonApplyTrip> tripList = JsonUtils.toObj(tripListValue, List.class , CommonApplyTrip.class);
        for (CommonApplyTrip trip : tripList) {
            Integer estimatedAmount = trip.getEstimatedAmount();
            if(estimatedAmount!=null){
                BigDecimal bigDecimal = BigDecimalUtils.yuan2fen(  BigDecimalUtils.obj2big( estimatedAmount ) );
                trip.setEstimatedAmount( NumericUtils.obj2int( bigDecimal ) );
            }else{
                trip.setEstimatedAmount( 0 );
            }
            if(trip.getTripType() == null){
                trip.setTripType(TripRoundType.SingleTrip.getValue());
            }
            //如果是酒店或者是机票往返 则不需要重置结束时间
            boolean resetEndTimeFlag = !(trip.getType() == OrderCategoryEnum.Hotel.getKey() ||
                (trip.getType() == OrderCategoryEnum.Air.getKey() && trip.getTripType() == TripRoundType.RoungTrip.getValue()));
            if(resetEndTimeFlag){
                trip.setEndTime(trip.getStartTime());
            }
        }
        return tripList;
    }

    /**
     * 组装非行程申请单信息
     */
    private MultiTripDTO buildMultiTrip(String mulitiTripExtValue) {
        if(StringUtils.isBlank( mulitiTripExtValue )){
            return null;
        }
        // 行程表单信息
        String tripListValue = StringEscapeUtils.unescapeJava(mulitiTripExtValue);
        MultiTripDTO multiTripDTO = JsonUtils.toObj(tripListValue,  MultiTripDTO.class);
        return multiTripDTO;
    }

    /**
     * 组装申请单行程信息
     */
    private List<CommonApplyGuest> buildApplyGuestList(String tripListExtValue, String companyId) {
        if (StringUtils.isBlank(tripListExtValue) || StringUtils.isBlank(companyId)) {
            return null;
        }
        // 行程表单信息
        String tripListValue = StringEscapeUtils.unescapeJava(tripListExtValue);
        List<Map> guestList = JsonUtils.toObj(tripListValue, List.class, Map.class);
        if (CollectionUtils.isBlank(guestList)) {
            return null;
        }
        List<String> thirdEmployeeIds = guestList.stream()
            .map(guestMap -> guestMap.get("emplId"))
            .filter(Objects::nonNull)
            .map(Objects::toString)
            .collect(Collectors.toList());
        //查询出所有在该企业下的三方员工，并转成map
        Map<String, String> thirdFbIdMap = openIdTranService.thirdIdToFbIdBatch(companyId,
            thirdEmployeeIds,
            IdBusinessTypeEnums.EMPLOYEE.getKey(),
            true);
        List<CommonApplyGuest> commonApplyGuestList = new ArrayList<>();
        guestList.forEach(guest -> {
            CommonApplyGuest commonApplyGuest = new CommonApplyGuest();
            commonApplyGuest.setId(StringUtils.obj2str(guest.get("emplId")));
            commonApplyGuest.setName(StringUtils.obj2str(guest.get("name")));
            commonApplyGuest.setEmployeeType(1);
            if (thirdFbIdMap.containsKey(commonApplyGuest.getId())) {
                commonApplyGuest.setIsEmployee(true);
                commonApplyGuest.setEmployeeType(0);
                commonApplyGuest.setId(thirdFbIdMap.get(commonApplyGuest.getId()));
            } else {
                commonApplyGuest.setIsEmployee(false);
            }
            commonApplyGuestList.add(commonApplyGuest);
        });
        return commonApplyGuestList;
    }

    /**
     * 解析出差时间（新）
     */
    private static void parseBussinessTimeNew(String bussinessTimeExtValue ,DingtalkTripCommonApplyDTO commonApplyDTO) {
        if(StringUtils.isBlank( bussinessTimeExtValue )){
            return ;
        }
        commonApplyDTO.setTravelTimeList(getTravelTimeList(bussinessTimeExtValue));
    }

    private static List<TripTimeDTO> getTravelTimeList(String travelTimeJson) {
        travelTimeJson = StringEscapeUtils.unescapeJava(travelTimeJson);
        DingtalkTripKitTravelTimeDTO kitTravelTimeDTO = JsonUtils.toObj(travelTimeJson, DingtalkTripKitTravelTimeDTO.class);
        if (ObjectUtils.isEmpty(kitTravelTimeDTO)){
            return null;
        }
        Date endTime = DateUtils.toDate(kitTravelTimeDTO.getEndTime());
        Date startTime = DateUtils.toDate(kitTravelTimeDTO.getStartTime());
        Integer startDayType = kitTravelTimeDTO.getStartDayType();
        Integer endDayType = kitTravelTimeDTO.getEndDayType();
        if (ObjectUtils.isEmpty(startTime)
            ||ObjectUtils.isEmpty(endTime)
            ||ObjectUtils.isEmpty(startDayType)
            ||ObjectUtils.isEmpty(endDayType)
            || startTime.compareTo(endTime)>1){
            return null;
        }
        List<TripTimeDTO> tripTimeDTOList = new ArrayList<>();
        if (startTime.compareTo(endTime) == 0){
            if (startDayType>endDayType){
                return null;
            }
            TripTimeDTO tripTimeDTO = new TripTimeDTO();
            tripTimeDTO.setTravel_time(DateUtils.toStr(startTime,"yyyyMMdd"));
            tripTimeDTO.setTravel_type(endDayType.equals(startDayType)?DingtalkKitValueConstant.HALF_DAY:DingtalkKitValueConstant.WHOLE_DAY);
            tripTimeDTOList.add(tripTimeDTO);
            return tripTimeDTOList;
        }
        int days = DateUtils.differentDaysByMillisecond(startTime,endTime);
        for (int i = 0; i <= days; i++) {
            TripTimeDTO tripTimeDTO = new TripTimeDTO();
            if ((i == 0 && DingtalkKitValueConstant.PM_TYPE.equals(startDayType))
                || (i == days && DingtalkKitValueConstant.AM_TYPE.equals(endDayType))) {
                tripTimeDTO.setTravel_type(DingtalkKitValueConstant.HALF_DAY);
            } else {
                tripTimeDTO.setTravel_type(DingtalkKitValueConstant.WHOLE_DAY);
            }
            tripTimeDTO.setTravel_time(DateUtils.toStr(DateUtils.addDay(startTime,i),"yyyyMMdd"));
            tripTimeDTOList.add(tripTimeDTO);
        }
        return tripTimeDTOList;
    }

}
