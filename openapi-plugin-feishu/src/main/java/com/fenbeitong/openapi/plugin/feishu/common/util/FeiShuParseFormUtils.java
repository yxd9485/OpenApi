package com.fenbeitong.openapi.plugin.feishu.common.util;

import com.fenbeitong.fenbeipay.api.constant.enums.cashier.OrderType;
import com.fenbeitong.finhub.common.saas.entity.CostCategory;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.support.apply.constant.ApplyTripConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.TripRoundType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.saasplus.api.model.enums.common.CostAttributeEnum;
import com.fenbeitong.usercenter.api.model.enums.company.CostAttributionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author xiaohai
 * @date 2022/01/26
 */
@Slf4j
public class FeiShuParseFormUtils {

    /**
     * 组装行程表单数据
     * @param apply
     * @param approvalFormMap
     * @return
     */
    public static List<FeiShuApprovalSimpleFormDTO> parseTripInfo(ApplyTripDTO apply , Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap){
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = CollectionUtils.newArrayList();
        //行程信息
        if(approvalFormMap.containsKey(ApplyTripConstant.TRIP_LIST_INFO)){
            FeiShuApprovalSimpleFormDTO produceInfoForm = approvalFormMap.get(ApplyTripConstant.TRIP_LIST_INFO);
            List<FeiShuApprovalSimpleFormDTO> children = produceInfoForm.getChildren();
            produceInfoForm.setValue( setTripInfo( apply  , children) );
            approvalFormList.add( produceInfoForm );
        }
        //申请事由
        setFromValue(approvalFormMap, ApplyTripConstant.APPLY_REASON, apply.getApplyReason()  , approvalFormList);
        setFromValue(approvalFormMap, ApplyTripConstant.APPLY_REASON_DESC, apply.getApplyReasonDesc()  , approvalFormList);
        //出行人
        setFromValue(approvalFormMap, ApplyTripConstant.APPLY_GUEST_NAME, apply.getGuestList().stream().map(ApplyTripDTO.GuestList::getName).collect(Collectors.joining(","))  , approvalFormList);
        //出差时间
        setFromValue(approvalFormMap, ApplyTripConstant.TRAVEL_TIME, StringUtils.obj2str(apply.getTravelDay()) + "天", approvalFormList);
        //费用归属部门和项目
        List<ApplyTripDTO.CostAttributionList> costAttributionList = apply.getCostAttributionList();
        if(CollectionUtils.isNotBlank(costAttributionList)){
            costAttributionList.forEach( costAttribution -> {
                if( costAttribution.getCostAttributionCategory() == 1){
                    //部门
                    setFromValue( approvalFormMap , ApplyTripConstant.COST_ATTRBUTION_DEPARTMENT , costAttribution.getCostAttributionName() ,   approvalFormList);
                }else if(costAttribution.getCostAttributionCategory() == 2){
                    //项目
                    setFromValue( approvalFormMap , ApplyTripConstant.COST_ATTRBUTION_PROJECT ,  costAttribution.getCostAttributionName() ,   approvalFormList);
                }
            });
        }
        return approvalFormList;
    }

    /**
     * 设置行程信息
     * @param apply
     * @param children
     * @return
     */
    private static List<List<FeiShuApprovalSimpleFormDTO>> setTripInfo( ApplyTripDTO apply , List<FeiShuApprovalSimpleFormDTO> children){
        List<ApplyTripDTO.TripList> tripList = apply.getTripList();
        List<List<FeiShuApprovalSimpleFormDTO>> formComponentValueVoList = new ArrayList<>();
        Map<String, FeiShuApprovalSimpleFormDTO> approvalChildFormMap = children.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        tripList.forEach( trip ->{
            List<FeiShuApprovalSimpleFormDTO> listContent = new ArrayList<>();
            int type = trip.getType();
            int tripType = trip.getTripType();
            String sceneName = convertTripSceneType(type, tripType);
            setFromValue(approvalChildFormMap, ApplyTripConstant.TRIP_TYPE,  sceneName , listContent);
            if(OrderType.Hotel.getKey() == type){
                // 入店时间、离店时间、目的城市
                setFromValue(approvalChildFormMap, ApplyTripConstant.ENTRY_DATE,   trip.getStartTime() , listContent);
                setFromValue(approvalChildFormMap, ApplyTripConstant.DEPARTURE_DATE,   trip.getEndTime() , listContent);
                setFromValue(approvalChildFormMap, ApplyTripConstant.DESTINATION_CITY,   trip.getStartCityName() , listContent);
            }else{
                //出发城市、目的城市、出发日期
                String endTime = trip.getEndTime();
                String startTime = StringUtils.isBlank(endTime) ? trip.getStartTime() : trip.getStartTime() + " ~ " + endTime ;
                setFromValue(approvalChildFormMap, ApplyTripConstant.START_TIME,   startTime , listContent);
                setFromValue(approvalChildFormMap, ApplyTripConstant.START_CITY,   trip.getStartCityName() , listContent);
                setFromValue(approvalChildFormMap, ApplyTripConstant.DESTINATION_CITY,   trip.getArrivalCityName() , listContent);
                if(TripRoundType.RoungTrip.getValue() == tripType){
                    //往返
                    setFromValue(approvalChildFormMap, ApplyTripConstant.RETURN_TIME,   trip.getEndTime() , listContent);
                }
            }
            String amount = String.format("%.2f", trip.getEstimatedAmount());
            if(!"0.00".equals(amount)){
                setFromValue(approvalChildFormMap, ApplyTripConstant.KEY_TRIP_FEE, "¥ " + amount  , listContent);
            }
            formComponentValueVoList.add(listContent);
        });
        return formComponentValueVoList;
    }

    /**
     *
     * @param type ：场景类型 7：国际机票 11：酒店 15：火车
     * @param tripType ： 1：单程 2：往返
     * @return
     */
    private static String convertTripSceneType(int type , int tripType){
        if( TripRoundType.RoungTrip.getValue() == tripType && OrderType.Air.getKey() == type){
            return "国内机票（往返）";
        }else{
            if(OrderType.Air.getKey() == type){
                return "国内机票";
            }else if(OrderType.Hotel.getKey() == type){
                return "酒店";
            }else if(OrderType.Train.getKey() == type){
                return OrderType.Train.getValue();
            }
        }
        return "";
    }



    /**
     * 组装非行程表单数据
     * @param apply
     * @param approvalFormMap
     * @return
     */
    public static List<FeiShuApprovalSimpleFormDTO> setMultiTrip(IntranetApplyMultiTripDetailDTO apply , Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap){
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = CollectionUtils.newArrayList();
        //申请事由
        String applyReason = apply.getApplyReason();
        if(!StringUtils.isBlank(apply.getApplyReasonDesc())){
            applyReason = applyReason + ";" + apply.getApplyReasonDesc();
        }
        setFromValue(approvalFormMap, OpenTripApplyConstant.multiTrip.APPLY_REASON, applyReason  , approvalFormList);
        //开始时间
        setFromValue(approvalFormMap, OpenTripApplyConstant.multiTrip.APPLY_START_TIME, apply.getStartTime() , approvalFormList);
        //结束时间
        setFromValue(approvalFormMap, OpenTripApplyConstant.multiTrip.APPLY_END_TIME, apply.getEndTime() , approvalFormList);
        //出差城市
        setFromValue(approvalFormMap, OpenTripApplyConstant.multiTrip.APPLY_CITY, apply.getMultiTripCity().stream().map(IntranetApplyMultiTripDetailDTO.MultiTripCity::getValue).collect(Collectors.joining(",")) , approvalFormList);
        //使用场景
        setFromValue(approvalFormMap, OpenTripApplyConstant.multiTrip.APPLY_MULTI_TRIP_SCENE, String.join("," , apply.getMultiTripScene()) , approvalFormList);
        //总预估费用
        setFromValue(approvalFormMap, OpenTripApplyConstant.multiTrip.KEY_TRIP_FEE, "¥ " + bigDecimalToStr( apply.getEstimatedAmount() )  , approvalFormList);
        //出行人
        setFromValue(approvalFormMap, OpenTripApplyConstant.multiTrip.APPLY_GUEST_NAME, apply.getGuestList().stream().map(IntranetApplyMultiTripDetailDTO.GuestList::getName).collect(Collectors.joining(","))  , approvalFormList);
        //费用归属部门和项目
        List<IntranetApplyMultiTripDetailDTO.CostAttributionList> costAttributionList = apply.getCostAttributionList();
        if(CollectionUtils.isNotBlank(costAttributionList)){
            costAttributionList.forEach( costAttribution -> {
                if( costAttribution.getCostAttributionCategory() == 1){
                    //部门
                    setFromValue( approvalFormMap , OpenTripApplyConstant.multiTrip.COST_ATTRIBUTION_DEPARTMENT , costAttribution.getCostAttributionName() ,   approvalFormList);
                }else if(costAttribution.getCostAttributionCategory() == 2){
                    //项目
                    setFromValue( approvalFormMap , OpenTripApplyConstant.multiTrip.COST_ATTRIBUTION_PROJECT ,  costAttribution.getCostAttributionName() ,   approvalFormList);
                }
            });
        }
        return approvalFormList;
    }




    /**
     * 组装用餐表单数据
     *
     * @param applyDetail
     * @param approvalFormMap
     * @return
     */
    public static List<FeiShuApprovalSimpleFormDTO> setDinnerApply(DinnerApplyDetailDTO applyDetail, Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap) {
        if (applyDetail == null || applyDetail.getApply() == null || applyDetail.getDinner() == null) {
            return null;
        }
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = CollectionUtils.newArrayList();
        //申请原因
        setFromValue(approvalFormMap,
            OpenTripApplyConstant.dinnerApply.APPLY_REASON,
            getReason(Optional.of(applyDetail).map(DinnerApplyDetailDTO::getApply).orElse(null)),
            approvalFormList);

        //用餐城市
        setFromValue(approvalFormMap,
            OpenTripApplyConstant.dinnerApply.APPLY_CITY,
            Optional.of(applyDetail)
                .map(DinnerApplyDetailDTO::getDinner)
                .map(DinnerApplyDetailDTO.Dinner::getCity)
                .map(DinnerApplyDetailDTO.City::getValue)
                .orElse(null),
            approvalFormList);

        //用餐总费用
        setFromValue(approvalFormMap,
            OpenTripApplyConstant.dinnerApply.APPLY_FEE,
            Optional.of(applyDetail)
                .map(DinnerApplyDetailDTO::getApply)
                .map(DinnerApplyDetailDTO.Apply::getBudget)
                .map(budget -> "¥ " + bigDecimalToStr(budget))
                .orElse(null),
            approvalFormList);

        //用餐人数
        setFromValue(approvalFormMap,
            OpenTripApplyConstant.dinnerApply.APPLY_PERSON,
            Optional.of(applyDetail)
                .map(DinnerApplyDetailDTO::getDinner)
                .map(DinnerApplyDetailDTO.Dinner::getPersonCount)
                .map(Object::toString)
                .orElse(null),
            approvalFormList);

        //用餐日期
        setFromValue(approvalFormMap,
            OpenTripApplyConstant.dinnerApply.APPLY_DATE,
            Optional.of(applyDetail)
                .map(DinnerApplyDetailDTO::getDinner)
                .map(dinner -> dinner.getStartTime() + "~" + dinner.getEndTime())
                .orElse(null),
            approvalFormList);

        //用餐时段
        setFromValue(approvalFormMap,
            OpenTripApplyConstant.dinnerApply.APPLY_TIME_INTEVAL,
            Optional.of(applyDetail)
                .map(DinnerApplyDetailDTO::getDinner)
                .map(DinnerApplyDetailDTO.Dinner::getTimeRange)
                .orElse(null),
            approvalFormList);

        //费用归属部门和项目
        Optional.of(applyDetail)
            .map(DinnerApplyDetailDTO::getSaasInfo)
            .map(DinnerApplyDetailDTO.SaasInfo::getCostAttributionGroupList)
            .ifPresent(costAttributionGroups ->
                costAttributionGroups.forEach(costAttributionGroup -> {
                    if (Integer.valueOf(CostAttributionTypeEnum.ORG_UNIT.getKey()).equals(costAttributionGroup.getCategory())) {
                        Optional.ofNullable(costAttributionGroup.getCostAttributionList()).map(costAttributions -> costAttributions.get(0)).ifPresent(costAttribution ->
                            setFromValue(approvalFormMap, OpenTripApplyConstant.dinnerApply.COST_ATTRIBUTION_DEPARTMENT, costAttribution.getName(), approvalFormList));
                    } else if (Integer.valueOf(CostAttributionTypeEnum.PROJECT.getKey()).equals(costAttributionGroup.getCategory())) {
                        Optional.ofNullable(costAttributionGroup.getCostAttributionList()).map(costAttributions -> costAttributions.get(0)).ifPresent(costAttribution ->
                            setFromValue(approvalFormMap, OpenTripApplyConstant.dinnerApply.COST_ATTRIBUTION_PROJECT, costAttribution.getName(), approvalFormList));
                    }
                }));
        return approvalFormList;
    }

    private static String getReason(DinnerApplyDetailDTO.Apply apply) {
        if (apply == null) {
            return null;
        }
        if (!StringUtils.isBlank(apply.getApplyReason()) && !StringUtils.isBlank(apply.getApplyReasonDesc())) {
            return StringUtils.joinStr(";", apply.getApplyReason(), apply.getApplyReasonDesc());
        }
        if (!StringUtils.isBlank(apply.getApplyReason())) {
            return apply.getApplyReason();
        }
        if (!StringUtils.isBlank(apply.getApplyReasonDesc())) {
            return apply.getApplyReasonDesc();
        }
        return null;
    }

    /**
     * 组装外卖表单数据
     * @param applyTakeAwayNoticeDTO
     * @param approvalFormMap
     * @return
     */
    public static List<FeiShuApprovalSimpleFormDTO> setTakeawayApply(ApplyTakeAwayNoticeDTO applyTakeAwayNoticeDTO , Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap){

        List<FeiShuApprovalSimpleFormDTO> approvalFormList = CollectionUtils.newArrayList();
        ApplyTakeAwayNoticeDTO.Apply apply = applyTakeAwayNoticeDTO.getApply();
        ApplyTakeAwayNoticeDTO.TakeOut takeOut = applyTakeAwayNoticeDTO.getTakeOut();
        //申请事由
        String applyReason = apply.getApplyReason();
        String applyReasonDesc = apply.getApplyReasonDesc();
        if(!StringUtils.isBlank(applyReasonDesc)){
            applyReason = applyReason + ";" + applyReasonDesc;
        }
        setFromValue(approvalFormMap, OpenTripApplyConstant.takeawayApply.APPLY_REASON, applyReason , approvalFormList);
        //送餐日期
        setFromValue(approvalFormMap, OpenTripApplyConstant.takeawayApply.APPLY_DATE, takeOut.getStartTime() , approvalFormList);
        //送餐时段
        setFromValue(approvalFormMap, OpenTripApplyConstant.takeawayApply.APPLY_INTERVAL, takeOut.getTimeRange() , approvalFormList);
        //送餐地址
        setFromValue(approvalFormMap, OpenTripApplyConstant.takeawayApply.APPLY_ADDRESS, takeOut.getAddressName() , approvalFormList);
        //总预估费用
        setFromValue(approvalFormMap, OpenTripApplyConstant.takeawayApply.APPLY_FEE, "¥ " +  bigDecimalToStr(apply.getBudget() ), approvalFormList);
        //费用归属部门和项目
        ApplyTakeAwayNoticeDTO.SaasInfo saasInfo = applyTakeAwayNoticeDTO.getSaasInfo();
        List<ApplyTakeAwayNoticeDTO.CostAttributionDTO> costAttributionGroupList = saasInfo.getCostAttributionGroupList();
        if(CollectionUtils.isNotBlank(costAttributionGroupList)){
            costAttributionGroupList.forEach( costAttribution -> {
                List<ApplyTakeAwayNoticeDTO.CostAttributionDTO.Detail> costAttributionList = costAttribution.getCostAttributionList();
                if( costAttribution.getCategory() == 1){
                    //部门
                    if(CollectionUtils.isNotBlank(costAttributionList)) {
                        String name = costAttributionList.get(0).getName();
                        setFromValue(approvalFormMap, OpenTripApplyConstant.takeawayApply.COST_ATTRIBUTION_DEPARTMENT, name , approvalFormList);
                    }
                }else if(costAttribution.getCategory() == 2){
                    //项目
                    if(CollectionUtils.isNotBlank(costAttributionList)) {
                        String name = costAttributionList.get(0).getName();
                        setFromValue(approvalFormMap, OpenTripApplyConstant.takeawayApply.COST_ATTRIBUTION_PROJECT, name , approvalFormList);
                    }
                }
            });
        }
        return approvalFormList;
    }

    /**
     * 组装采购表单数据
     * @param apply
     * @param approvalFormMap
     * @return
     */
    public static List<FeiShuApprovalSimpleFormDTO> setMallApply(MallApplyDTO apply , Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap){

        List<FeiShuApprovalSimpleFormDTO> approvalFormList = CollectionUtils.newArrayList();

        List<MallApplyDTO.TripInfo> tripList = apply.getTripList();
        if(CollectionUtils.isBlank(tripList)){
            return approvalFormList;
        }
        MallApplyDTO.TripInfo tripInfo = tripList.get(0);
        //申请事由
        String applyOrderReason = tripInfo.getOrderReason();
        String applyReasonReasonDesc = tripInfo.getOrderReasonDesc();
        if(!StringUtils.isBlank(applyReasonReasonDesc)){
            applyOrderReason = applyOrderReason + ";" + applyReasonReasonDesc;
        }
        setFromValue(approvalFormMap, OpenTripApplyConstant.mallApply.APPLY_REASON, applyOrderReason , approvalFormList);
        //申请描述
        setFromValue(approvalFormMap, OpenTripApplyConstant.mallApply.APPLY_DESCRIPTION, apply.getApplyDesc()  , approvalFormList);
        //采购总额
        setFromValue(approvalFormMap, OpenTripApplyConstant.mallApply.MALL_FEE, "¥ " + bigDecimalToStr( BigDecimalUtils.fen2yuan(apply.getApplyTotalPrice()) )  , approvalFormList);
        //费用类别
        MallApplyDTO.CostCategory costCategory = apply.getCostCategory();
        if(costCategory!=null){
            setFromValue(approvalFormMap, OpenTripApplyConstant.mallApply.COST_TYPE, costCategory.getName()   , approvalFormList);
        }
        //费用归属部门和项目
        List<MallApplyDTO.CostAttributionDTO> costAttributionList = apply.getAttributionList();
        if(CollectionUtils.isNotBlank(costAttributionList)){
            costAttributionList.forEach( costAttribution -> {
                if( costAttribution.getCostAttributionCategory() == 1){
                    //部门
                    setFromValue( approvalFormMap , OpenTripApplyConstant.mallApply.COST_ATTRIBUTION_DEPARTMENT , costAttribution.getCostAttributionName() ,  approvalFormList);
                }else if(costAttribution.getCostAttributionCategory() == 2){
                    //项目
                    setFromValue( approvalFormMap , OpenTripApplyConstant.mallApply.COST_ATTRIBUTION_PROJECT ,  costAttribution.getCostAttributionName() ,  approvalFormList);
                }
            });
        }
        //商品信息
        if(approvalFormMap.containsKey(OpenTripApplyConstant.mallApply.PRODUCE_INFO)){
            FeiShuApprovalSimpleFormDTO produceInfoForm = approvalFormMap.get(OpenTripApplyConstant.mallApply.PRODUCE_INFO);
            List<FeiShuApprovalSimpleFormDTO> children = produceInfoForm.getChildren();
            produceInfoForm.setValue( setProduceInfo( tripInfo  , children) );
            approvalFormList.add( produceInfoForm );
        }
        MallApplyDTO.MallPriceStructure mallPriceStructure = tripInfo.getMallPriceStructure();
        if(mallPriceStructure ==  null){
            return approvalFormList;
        }
        //商品金额
        setFromValue(approvalFormMap, OpenTripApplyConstant.mallApply.PRODUCE_AMOUNT,  "¥ " + bigDecimalToStr( mallPriceStructure.getTotalPrice() )  , approvalFormList);
        //商品运费
        setFromValue(approvalFormMap, OpenTripApplyConstant.mallApply.PRODUCE_TRNSPORT_FEE,   "¥ " + bigDecimalToStr( mallPriceStructure.getFreight() )  , approvalFormList);
        //配送信息
        MallApplyDTO.AddressInfo addressInfo = tripInfo.getAddressInfo();
        String address = (addressInfo.getProvince() == null ? "":addressInfo.getProvince()) +
            (addressInfo.getCity() == null ? "" : addressInfo.getCity() )+
            (addressInfo.getCounty() == null ? "" : addressInfo.getCounty()) +
            (addressInfo.getTown() == null ? "" : addressInfo.getTown()) +
            (addressInfo.getDetail() == null ? "" : addressInfo.getDetail());
        setFromValue(approvalFormMap, OpenTripApplyConstant.mallApply.PRODUCE_TRANSPORT_INFO,   addressInfo.getName() + "  "+  addressInfo.getPhone() +"  "+ address , approvalFormList);
        return approvalFormList;
    }

    /**
     * 组装里程补贴表单数据
     * @param mileageSubsidyNoticeDTO
     * @param approvalFormMap
     * @return
     */
    public static List<FeiShuApprovalSimpleFormDTO> setMileageApply(MileageSubsidyNoticeDTO mileageSubsidyNoticeDTO , Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap){
        MileageSubsidyNoticeDTO.Apply apply = mileageSubsidyNoticeDTO.getApply();
        MileageSubsidyNoticeDTO.SaasInfo saasInfo = mileageSubsidyNoticeDTO.getSaasInfo();
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = CollectionUtils.newArrayList();
        //申请事由
        setFromValue(approvalFormMap, OpenTripApplyConstant.mileagesApply.APPLY_REASON, apply.getReason() , approvalFormList);
        //事由补充
        setFromValue(approvalFormMap, OpenTripApplyConstant.mileagesApply.APPLY_REASON_DESC, apply.getReasonDesc() , approvalFormList);
        //费用类别
        CostCategory costCategory = saasInfo.getCostCategory();
        if( costCategory != null){
            setFromValue(approvalFormMap, OpenTripApplyConstant.mileagesApply.COST_TYPE, costCategory.getName() , approvalFormList);
        }
        //里程补贴合计
        setFromValue(approvalFormMap, OpenTripApplyConstant.mileagesApply.MILEAGES_AMOUNT_COUNT, StringUtils.obj2str( apply.getBudget() ) , approvalFormList);
        //里程补贴明细
        setFromValue(approvalFormMap, OpenTripApplyConstant.mileagesApply.MILEAGES_DETAIL, apply.getUnableAllowanceReason() , approvalFormList);
        //里程补贴记录
        if(approvalFormMap.containsKey(OpenTripApplyConstant.mileagesApply.MILEAGES_RECORD)){
            List<MileageSubsidyNoticeDTO.Mileage> mileages = mileageSubsidyNoticeDTO.getMileages();
            FeiShuApprovalSimpleFormDTO mileageRecordForm = approvalFormMap.get(OpenTripApplyConstant.mileagesApply.MILEAGES_RECORD);
            List<FeiShuApprovalSimpleFormDTO> children = mileageRecordForm.getChildren();
            mileageRecordForm.setValue( setMileageRecordInfo( mileages  , children) );
            approvalFormList.add( mileageRecordForm );
        }

        //费用归属部门
        List<MileageSubsidyNoticeDTO.CostAttributionDTO> costAttributionGroupList = saasInfo.getCostAttributionGroupList();
        if(CollectionUtils.isNotBlank(costAttributionGroupList)){
            costAttributionGroupList.forEach( costAttribution -> {
                List<MileageSubsidyNoticeDTO.CostAttributionDTO.Detail> costAttributionList = costAttribution.getCostAttributionList();
                if( costAttribution.getCategory() == 1){
                    //部门
                    if(CollectionUtils.isNotBlank(costAttributionList)) {
                        String name = costAttributionList.get(0).getName();
                        setFromValue(approvalFormMap, OpenTripApplyConstant.mileagesApply.COST_ATTRIBUTION_DEPARTMENT, name , approvalFormList);
                    }
                }else if(costAttribution.getCategory() == 2){
                    //项目
                    if(CollectionUtils.isNotBlank(costAttributionList)) {
                        String name = costAttributionList.get(0).getName();
                        setFromValue(approvalFormMap, OpenTripApplyConstant.mileagesApply.COST_ATTRIBUTION_PROJECT, name , approvalFormList);
                    }
                }
            });
        }
        return approvalFormList;
    }


    /**
     * 组装分贝券表单数据
     * @param fbCouponApplyDetail
     * @param approvalFormMap
     * @return
     */
    public static List<FeiShuApprovalSimpleFormDTO> setFbCouponApply(FBCouponApplyDetailDTO fbCouponApplyDetail , Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap){
        FBCouponApplyDetailDTO.Apply apply = fbCouponApplyDetail.getApply();
        FBCouponApplyDetailDTO.Coupon coupon = fbCouponApplyDetail.getCoupon();
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = CollectionUtils.newArrayList();
        //申请事由
        setFromValue(approvalFormMap, OpenTripApplyConstant.fbCouponApply.APPLY_REASON, apply.getApplyReason() , approvalFormList);
        //券名称
        setFromValue(approvalFormMap, OpenTripApplyConstant.fbCouponApply.COUPON_NAME, coupon.getName() , approvalFormList);
        //面值
        setFromValue(approvalFormMap, OpenTripApplyConstant.fbCouponApply.ESTIMATED_AMOUNT, StringUtils.obj2str(coupon.getEstimatedAmount()) , approvalFormList);
        //有效期（开始时间和结束时间）
        setFromValue(approvalFormMap, OpenTripApplyConstant.fbCouponApply.VALID_DATE, coupon.getStartTime() + " ~ " + coupon.getEndTime() , approvalFormList);
        //申请张数
        setFromValue(approvalFormMap, OpenTripApplyConstant.fbCouponApply.COUPON_COUNT, StringUtils.obj2str(coupon.getCount()) , approvalFormList);
        //有效时长
        setFromValue(approvalFormMap, OpenTripApplyConstant.fbCouponApply.VALID_TIME, coupon.getValidTime() , approvalFormList);
        //申请总金额
        setFromValue(approvalFormMap, OpenTripApplyConstant.fbCouponApply.PRODUCE_INFO, "¥ " + bigDecimalToStr(apply.getBudget())  , approvalFormList);
        //申请说明
        setFromValue(approvalFormMap, OpenTripApplyConstant.fbCouponApply.APPLY_REASON_DESC, apply.getApplyReasonDesc() , approvalFormList);
        return approvalFormList;
    }


    /**
     * 虚拟卡额度申请
     * @param virtualCardAmountDetailDTO
     * @return
     */
    public static List<FeiShuApprovalSimpleFormDTO> setVirtualCardAmountApply(VirtualCardAmountDetailDTO virtualCardAmountDetailDTO , Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap){

        List<FeiShuApprovalSimpleFormDTO> approvalFormList = CollectionUtils.newArrayList();

        VirtualCardAmountDetailDTO.Apply apply = virtualCardAmountDetailDTO.getApply();
        VirtualCardAmountDetailDTO.SaasInfo saasInfo = virtualCardAmountDetailDTO.getSaasInfo();
        //备用金名称
        setFromValue(approvalFormMap, OpenTripApplyConstant.virtualCardApply.APPLY_TITLE, apply.getApplyTitle() , approvalFormList);
        //申请事由
        String applyReason = apply.getApplyReason();
        if(!StringUtils.isBlank(apply.getApplyReasonDesc())){
            applyReason = applyReason+";"+ apply.getApplyReasonDesc();
        }
        setFromValue(approvalFormMap, OpenTripApplyConstant.virtualCardApply.APPLY_REASON, applyReason , approvalFormList);
        //申请金额
        setFromValue(approvalFormMap, OpenTripApplyConstant.virtualCardApply.APPLY_AMOUNT, "¥ " + bigDecimalToStr(apply.getApplyAmount()) , approvalFormList);
        //费用归属类型
        if(saasInfo != null ){
            if( saasInfo.getCostCategory()!= null ){
                setFromValue(approvalFormMap, OpenTripApplyConstant.virtualCardApply.COST_TYPE, saasInfo.getCostCategory().getName() , approvalFormList);
            }
            //费用类别和费用归属部门
            List<VirtualCardAmountDetailDTO.CostAttributionDTO> costAttributionGroupList = saasInfo.getCostAttributionGroupList();
            if(CollectionUtils.isNotBlank(costAttributionGroupList)){
                costAttributionGroupList.forEach( costAttribution -> {
                    List<VirtualCardAmountDetailDTO.CostAttributionDTO.Detail> costAttributionList = costAttribution.getCostAttributionList();
                    if( costAttribution.getCategory() == 1){
                        //部门
                        if(CollectionUtils.isNotBlank(costAttributionList)) {
                            String name = costAttributionList.get(0).getName();
                            setFromValue(approvalFormMap, OpenTripApplyConstant.virtualCardApply.COST_ATTRIBUTION_DEPARTMENT, name , approvalFormList);
                        }
                    }else if(costAttribution.getCategory() == 2){
                        //项目
                        if(CollectionUtils.isNotBlank(costAttributionList)) {
                            String name = costAttributionList.get(0).getName();
                            setFromValue(approvalFormMap, OpenTripApplyConstant.virtualCardApply.COST_ATTRIBUTION_PROJECT, name , approvalFormList);
                        }
                    }
                });
            }
        }
        //虚拟卡使用情况
        VirtualCardAmountDetailDTO.VirtualCard virtualCard = virtualCardAmountDetailDTO.getVirtualCard();
        if(virtualCard!=null){
            if(approvalFormMap.containsKey(OpenTripApplyConstant.virtualCardApply.VIRTUAL_CARD)){
                FeiShuApprovalSimpleFormDTO virtualCardInfoVo = approvalFormMap.get(OpenTripApplyConstant.virtualCardApply.VIRTUAL_CARD);
                List<FeiShuApprovalSimpleFormDTO> children = virtualCardInfoVo.getChildren();
                virtualCardInfoVo.setValue( setVirtualCardInfo(  virtualCard ,  children) );
                approvalFormList.add( virtualCardInfoVo );
            }
        }
        return approvalFormList;
    }

    /**
     * 对公付款
     * @param paymentApplyDetailDTO
     * @return
     */
    public static List<FeiShuApprovalSimpleFormDTO> setPaymentApply(PaymentApplyDetailDTO paymentApplyDetailDTO , Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap,
                                                                    List<String> proofInfoCode , List<String> invoiceInfoCode){

        List<FeiShuApprovalSimpleFormDTO> approvalFormList = CollectionUtils.newArrayList();
        PaymentApplyDetailDTO.Apply apply = paymentApplyDetailDTO.getApply();
        PaymentApplyDetailDTO.SaasInfo saasInfo =  paymentApplyDetailDTO.getSaasInfo();
        PaymentApplyDetailDTO.PaymentInfo paymentInfo = paymentApplyDetailDTO.getPaymentInfo();
        //申请事由
        setFromValue( approvalFormMap , OpenTripApplyConstant.paymentApply.APPLY_REASON , apply.getApplyReason() ,  approvalFormList);
        //事由补充
        setFromValue( approvalFormMap , OpenTripApplyConstant.paymentApply.APPLY_REASON_DESC , apply.getApplyReasonDesc() ,  approvalFormList);
        if(paymentInfo != null ){
            //名称
            setFromValue( approvalFormMap , OpenTripApplyConstant.paymentApply.PAYMENT_TILTE , paymentInfo.getPaymentTitle() ,  approvalFormList);
            //付款金额
            setFromValue( approvalFormMap , OpenTripApplyConstant.paymentApply.PAYMENT_AMOUNT ,"¥ " + bigDecimalToStr(paymentInfo.getPaymentAmount()) ,  approvalFormList);
            //付款账户
            setFromValue( approvalFormMap , OpenTripApplyConstant.paymentApply.PAYMENT_ACCOUNT ,paymentInfo.getPaymentAccount() ,  approvalFormList);
            //付款账号
            setFromValue( approvalFormMap , OpenTripApplyConstant.paymentApply.PAYMENT_ACCOUNT_NUMBER ,paymentInfo.getPaymentAccountNumber() ,  approvalFormList);
            //开户行名称
            //setFromValue( approvalFormMap , OpenTripApplyConstant.paymentApply.PAYMENT_BANK_NAME ,paymentInfo.getPaymentBankName() ,  approvalFormList);
            //付款时间
            setFromValue( approvalFormMap , OpenTripApplyConstant.paymentApply.PAYMENT_TIME ,paymentInfo.getPaymentTime() ,  approvalFormList);
            //用途
            setFromValue( approvalFormMap , OpenTripApplyConstant.paymentApply.PAYMENT_PURPUSE ,paymentInfo.getPaymentPurpose() ,  approvalFormList);
        }
        //费用归属类型
        if(saasInfo != null){
            if( saasInfo.getCostCategory()!= null ){
                setFromValue(approvalFormMap, OpenTripApplyConstant.paymentApply.COST_TYPE, saasInfo.getCostCategory().getName() , approvalFormList);
            }
            //费用类别和费用归属部门
            List<PaymentApplyDetailDTO.CostAttributionDTO> costAttributionGroupList = saasInfo.getCostAttributionGroupList();
            if(CollectionUtils.isNotBlank(costAttributionGroupList)){
                costAttributionGroupList.forEach( costAttribution -> {
                    List<PaymentApplyDetailDTO.CostAttributionDTO.Detail> costAttributionList = costAttribution.getCostAttributionList();
                    if( costAttribution.getCategory() == 1){
                        //部门
                        if(CollectionUtils.isNotBlank(costAttributionList)) {
                            String name = costAttributionList.get(0).getName();
                            setFromValue(approvalFormMap, OpenTripApplyConstant.paymentApply.COST_ATTRIBUTION_DEPARTMENT, name , approvalFormList);
                        }
                    }else if(costAttribution.getCategory() == 2){
                        //项目
                        if(CollectionUtils.isNotBlank(costAttributionList)) {
                            String name = costAttributionList.get(0).getName();
                            setFromValue(approvalFormMap, OpenTripApplyConstant.paymentApply.COST_ATTRIBUTION_PROJECT, name , approvalFormList);
                        }
                    }
                });
            }
        }
        //供应商信息
        if(approvalFormMap.containsKey(OpenTripApplyConstant.paymentApply.SUPPLIER_INFO)){
            PaymentApplyDetailDTO.SupplierInfo supplierInfo = paymentApplyDetailDTO.getSupplierInfo();
            FeiShuApprovalSimpleFormDTO supplierInfoVo = approvalFormMap.get(OpenTripApplyConstant.paymentApply.SUPPLIER_INFO);
            List<FeiShuApprovalSimpleFormDTO> children = supplierInfoVo.getChildren();
            supplierInfoVo.setValue( setSupplierInfo(  supplierInfo ,  children) );
            approvalFormList.add( supplierInfoVo );
        }
        //合同信息
        if(approvalFormMap.containsKey(OpenTripApplyConstant.paymentApply.CONTRACT_INFO)){
            PaymentApplyDetailDTO.ContractInfo contractInfo = paymentApplyDetailDTO.getContractInfo();
            FeiShuApprovalSimpleFormDTO contractInfoVo = approvalFormMap.get(OpenTripApplyConstant.paymentApply.CONTRACT_INFO);
            List<FeiShuApprovalSimpleFormDTO> children = contractInfoVo.getChildren();
            contractInfoVo.setValue( setContractInfo(  contractInfo ,  children) );
            approvalFormList.add( contractInfoVo );
        }
        //凭证信息
        if(approvalFormMap.containsKey(OpenTripApplyConstant.paymentApply.PROOF_INFO)){
            PaymentApplyDetailDTO.ProofInfo proofInfo = paymentApplyDetailDTO.getProofInfo();
            FeiShuApprovalSimpleFormDTO proofInfoVo = approvalFormMap.get(OpenTripApplyConstant.paymentApply.PROOF_INFO);
            List<FeiShuApprovalSimpleFormDTO> children = proofInfoVo.getChildren();
            proofInfoVo.setValue( setProofInfo(  proofInfo ,  children , proofInfoCode) );
            approvalFormList.add( proofInfoVo );
        }
        //发票信息
        if(approvalFormMap.containsKey(OpenTripApplyConstant.paymentApply.INVOICE_INFO)){
            List<PaymentApplyDetailDTO.InvoiceInfo> invoiceInfos = paymentApplyDetailDTO.getInvoiceInfos();
            String invoiceStatus = apply.getInvoiceStatus();
            FeiShuApprovalSimpleFormDTO invoiceInfoVo = approvalFormMap.get(OpenTripApplyConstant.paymentApply.INVOICE_INFO);
            List<FeiShuApprovalSimpleFormDTO> children = invoiceInfoVo.getChildren();
            invoiceInfoVo.setValue( setInvoiceInfo(  invoiceInfos , invoiceStatus ,  children , invoiceInfoCode) );
            approvalFormList.add( invoiceInfoVo );
        }
        return approvalFormList;
    }

    /**
     * 设置发票信息
     * @param invoiceInfos
     * @return
     */
    private static List<List<FeiShuApprovalSimpleFormDTO>> setInvoiceInfo( List<PaymentApplyDetailDTO.InvoiceInfo> invoiceInfos ,  String invoiceStatus ,
                                                                           List<FeiShuApprovalSimpleFormDTO> children , List<String> invoiceInfoCode){
        List<List<FeiShuApprovalSimpleFormDTO>> formComponentValueVoList = new ArrayList<>();
        Map<String, FeiShuApprovalSimpleFormDTO> approvalChildFormMap = children.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        List<FeiShuApprovalSimpleFormDTO> listContent = new ArrayList<>();
        if("0".equals(invoiceStatus)){
            setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.INVOICE_STATUS , "待开票",  listContent);
        }else if("2".equals(invoiceStatus)){
            setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.INVOICE_STATUS , "无发票",  listContent);
        }else if("1".equals(invoiceStatus)){
            setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.INVOICE_STATUS , "已开票",  listContent);
            BigDecimal invoiceTotalAmount = new BigDecimal(0);
            for (PaymentApplyDetailDTO.InvoiceInfo invoiceInfo : invoiceInfos) {
                if(invoiceInfo.getInvoiceTotalAmount()!=null){
                    invoiceTotalAmount = invoiceTotalAmount.add(invoiceInfo.getInvoiceTotalAmount());
                }
            }
            setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.INVOICE_TOTAL_AMOUNT , StringUtils.obj2str(invoiceTotalAmount) ,  listContent);
            if(CollectionUtils.isNotBlank(invoiceInfoCode)){
                setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.INVOICE_ATTACHMENT_URL , invoiceInfoCode ,  listContent);
            }
        }
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }

    /**
     * 设置凭证信息
     * @param proofInfo
     * @return
     */
    private static List<List<FeiShuApprovalSimpleFormDTO>> setProofInfo( PaymentApplyDetailDTO.ProofInfo proofInfo , List<FeiShuApprovalSimpleFormDTO> children , List<String> proofInfoCode){

        List<List<FeiShuApprovalSimpleFormDTO>> formComponentValueVoList = new ArrayList<>();
        Map<String, FeiShuApprovalSimpleFormDTO> approvalChildFormMap = children.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        List<FeiShuApprovalSimpleFormDTO> listContent = new ArrayList<>();
        //凭证名称
        setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.PROOF_NAME , proofInfo.getProofName() ,  listContent);
        //凭证说明
        setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.PROOF_REMARK , proofInfo.getRemark(),  listContent);
        //凭证金额
        if(proofInfo.getProofPrice()!=null){
            setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.PROOF_PRICE , "¥ " + bigDecimalToStr( proofInfo.getProofPrice() ),  listContent);
        }
        //凭证附件
        if(CollectionUtils.isNotBlank(proofInfoCode)){
            setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.PROOF_ATTACHMENT_URL ,  proofInfoCode ,  listContent);
        }
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }

    /**
     * 设置合同信息
     * @param contractInfo
     * @return
     */
    private static List<List<FeiShuApprovalSimpleFormDTO>> setContractInfo( PaymentApplyDetailDTO.ContractInfo contractInfo, List<FeiShuApprovalSimpleFormDTO> children ){
        List<List<FeiShuApprovalSimpleFormDTO>> formComponentValueVoList = new ArrayList<>();
        Map<String, FeiShuApprovalSimpleFormDTO> approvalChildFormMap = children.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        List<FeiShuApprovalSimpleFormDTO> listContent = new ArrayList<>();
        //合同名称
        setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.CONTRACT_NAME , contractInfo.getName(),  listContent);
        //合同金额
        if(contractInfo.getPrice()!=null){
            setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.CONTRACT_PRICE , "¥ " + bigDecimalToStr(contractInfo.getPrice()),  listContent);
        }
        //合同有效期
        if(contractInfo.getStartTime() != null){
            setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.CONTRACT_TIME , contractInfo.getStartTime() + " ~ " + contractInfo.getEndTime(),  listContent);
        }
        //合同甲方名称
        setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.CONTRACT_PARTYA_NAME , contractInfo.getPartyAName(),  listContent);
        //合同乙方名称
        setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.CONTRACT_PARTYB_NAME , contractInfo.getPartyBName(),  listContent);
        //合同编号
        setFromValue( approvalChildFormMap,  OpenTripApplyConstant.paymentApply.CONTRACT_CODE , contractInfo.getCode(),  listContent);
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }

    /**
     * 设置供应商信息
     * @param supplierInfo
     * @return
     */
    private static List<List<FeiShuApprovalSimpleFormDTO>> setSupplierInfo( PaymentApplyDetailDTO.SupplierInfo supplierInfo, List<FeiShuApprovalSimpleFormDTO> children  ){
        List<List<FeiShuApprovalSimpleFormDTO>> formComponentValueVoList = new ArrayList<>();
        Map<String, FeiShuApprovalSimpleFormDTO> approvalChildFormMap = children.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        List<FeiShuApprovalSimpleFormDTO> listContent = new ArrayList<>();
        //供应商开户行支行
        setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.SUPPLIER_BANK_NAME , supplierInfo.getBankName() + supplierInfo.getSubBranch(),  listContent);
        //供应商收款账号
        setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.SUPPLIER_BANK_ACCOUNT_NAME , supplierInfo.getBankAccountName(),  listContent);
        //供应商收款账户
        setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.SUPPLIER_BANK_ACCOUNT , supplierInfo.getBankAccount(),  listContent);
        //供应商名称
        setFromValue( approvalChildFormMap, OpenTripApplyConstant.paymentApply.SUPPLIER_NAME , supplierInfo.getName(),  listContent);
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }


    /**
     * 设置虚拟卡使用情况信息
     * @param virtualCard
     * @param children
     * @return
     */
    private static List<List<FeiShuApprovalSimpleFormDTO>> setVirtualCardInfo( VirtualCardAmountDetailDTO.VirtualCard virtualCard , List<FeiShuApprovalSimpleFormDTO> children){
        List<List<FeiShuApprovalSimpleFormDTO>> formComponentValueVoList = new ArrayList<>();
        Map<String, FeiShuApprovalSimpleFormDTO> approvalChildFormMap = children.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        List<FeiShuApprovalSimpleFormDTO> listContent = new ArrayList<>();
        setFromValue(approvalChildFormMap, OpenTripApplyConstant.virtualCardApply.WRITTEN_OFF_AMOUNT,  "¥ " + bigDecimalToStr(virtualCard.getWrittenOffAmount()) , listContent);
        setFromValue(approvalChildFormMap, OpenTripApplyConstant.virtualCardApply.AVAILABLE_AMOUNT,  "¥ " + bigDecimalToStr(virtualCard.getAvailableAmount()) , listContent);
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }

    /**
     * 设置里程补贴记录信息
     * @param mileages
     * @param children
     * @return
     */
    private static List<List<FeiShuApprovalSimpleFormDTO>> setMileageRecordInfo( List<MileageSubsidyNoticeDTO.Mileage> mileages , List<FeiShuApprovalSimpleFormDTO> children){
        List<List<FeiShuApprovalSimpleFormDTO>> formComponentValueVoList = new ArrayList<>();
        Map<String, FeiShuApprovalSimpleFormDTO> approvalChildFormMap = children.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        mileages.forEach(
            mileage -> {
                List<FeiShuApprovalSimpleFormDTO> listContent = new ArrayList<>();
                setFromValue(approvalChildFormMap, OpenTripApplyConstant.mileagesApply.MILEAGES_DISTANCE,  mileage.getDistance() + "KM" , listContent);
                setFromValue(approvalChildFormMap, OpenTripApplyConstant.mileagesApply.START_ADDRESS,  mileage.getStartAddress() , listContent);
                setFromValue(approvalChildFormMap, OpenTripApplyConstant.mileagesApply.END_ADDRESS,  mileage.getEndAddress() , listContent);
                setFromValue(approvalChildFormMap, OpenTripApplyConstant.mileagesApply.MILEAGES_DATE,  mileage.getStartTime() + " ~ " +  mileage.getEndTime() , listContent);
                setFromValue(approvalChildFormMap, OpenTripApplyConstant.mileagesApply.MILEAGES_AMOUNT,  "¥ " + bigDecimalToStr(mileage.getEstimatedAmount()) , listContent);
                formComponentValueVoList.add(listContent);
            }
        );
        return formComponentValueVoList;
    }

    /**
     * 设置商品信息
     * @param tripInfo
     * @param children
     * @return
     */
    private static List<List<FeiShuApprovalSimpleFormDTO>> setProduceInfo( MallApplyDTO.TripInfo tripInfo , List<FeiShuApprovalSimpleFormDTO> children){
        List<MallApplyDTO.MallDTO> mallList = tripInfo.getMallDTOList();
        List<List<FeiShuApprovalSimpleFormDTO>> formComponentValueVoList = new ArrayList<>();
        Map<String, FeiShuApprovalSimpleFormDTO> approvalChildFormMap = children.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        mallList.forEach( mall ->{
            List<FeiShuApprovalSimpleFormDTO> listContent = new ArrayList<>();
            setFromValue(approvalChildFormMap, OpenTripApplyConstant.mallApply.PRODUCE_NAME,  mall.getName() , listContent);
            setFromValue(approvalChildFormMap, OpenTripApplyConstant.mallApply.PRODUCE_PRICE,  "¥ " + bigDecimalToStr( mall.getSalePrice() ) , listContent);
            setFromValue(approvalChildFormMap, OpenTripApplyConstant.mallApply.PRODUCE_COUNT,  StringUtils.obj2str( mall.getAmount() )  , listContent);
            formComponentValueVoList.add(listContent);
        });
        return formComponentValueVoList;
    }


    private static void setFromValue(Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap , String formName , Object formValue , List<FeiShuApprovalSimpleFormDTO> approvalFormList){
        if(approvalFormMap.containsKey( formName )){
            FeiShuApprovalSimpleFormDTO applyReasonFormMap = approvalFormMap.get( formName );
            FeiShuApprovalSimpleFormDTO applyReasonForm = new FeiShuApprovalSimpleFormDTO();
            applyReasonForm.setId(applyReasonFormMap.getId());
            applyReasonForm.setName(applyReasonFormMap.getName());
            applyReasonForm.setType(applyReasonFormMap.getType());
            applyReasonForm.setValue( formValue );
            approvalFormList.add( applyReasonForm );
        }
    }

    public static void setFormValue(Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap , String formName , Object formValue , List<FeiShuApprovalSimpleFormDTO> approvalFormList){
        if(approvalFormMap.containsKey( formName )){
            FeiShuApprovalSimpleFormDTO applyReasonFormMap = approvalFormMap.get( formName );
            FeiShuApprovalSimpleFormDTO applyReasonForm = new FeiShuApprovalSimpleFormDTO();
            applyReasonForm.setId(applyReasonFormMap.getId());
            applyReasonForm.setName(applyReasonFormMap.getName());
            applyReasonForm.setType(applyReasonFormMap.getType());
            applyReasonForm.setValue( formValue );
            approvalFormList.add( applyReasonForm );
        }
    }

    /**
     * 金额转换字符串，null时转换成--
     * @param amount
     * @return
     */
    public static String bigDecimalToStr(BigDecimal amount){
        return  StringUtils.obj2str( amount == null ? "--" : amount );
    }


    public static List<FeiShuApprovalSimpleFormDTO> setCarApply(IntranetApplyCarDTO carApplyDetailDTO, Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap) {
        if (ObjectUtils.isEmpty(approvalFormMap)){
            return null;
        }
        List<FeiShuApprovalSimpleFormDTO> formDTOList = new ArrayList<>();
        Optional<IntranetApplyCarDTO.Trip> tripOptional = Optional.ofNullable(carApplyDetailDTO.getTripList()).flatMap(trips -> trips.stream().findFirst());
        //申请事由
        if (!ObjectUtils.isEmpty(approvalFormMap.get(OpenTripApplyConstant.CarApply.APPLY_REASON))){
            approvalFormMap.get(OpenTripApplyConstant.CarApply.APPLY_REASON).setValue(carApplyDetailDTO.getApplyReason());
            formDTOList.add(approvalFormMap.get(OpenTripApplyConstant.CarApply.APPLY_REASON));
        }
        //补充事由
        if (!ObjectUtils.isEmpty(approvalFormMap.get(OpenTripApplyConstant.CarApply.APPLY_REASON_DESC))){
            approvalFormMap.get(OpenTripApplyConstant.CarApply.APPLY_REASON_DESC).setValue(carApplyDetailDTO.getApplyReasonDesc());
            formDTOList.add(approvalFormMap.get(OpenTripApplyConstant.CarApply.APPLY_REASON_DESC));
        }
        //用车城市
        if (!ObjectUtils.isEmpty(approvalFormMap.get(OpenTripApplyConstant.CarApply.START_CITY))){
            String startCityList = tripOptional
                .map(IntranetApplyCarDTO.Trip::getStartCityNameList)
                .map(cityList-> String.join(",", cityList))
                .orElse(null);
            approvalFormMap.get(OpenTripApplyConstant.CarApply.START_CITY).setValue(startCityList);
            formDTOList.add(approvalFormMap.get(OpenTripApplyConstant.CarApply.START_CITY));
        }
        //用车日期
        if (!ObjectUtils.isEmpty(approvalFormMap.get(OpenTripApplyConstant.CarApply.TIME_RANGE))){
            String timeRange = tripOptional.map(FeiShuParseFormUtils::getTimeRange).orElse(null);
            approvalFormMap.get(OpenTripApplyConstant.CarApply.TIME_RANGE).setValue(timeRange);
            formDTOList.add(approvalFormMap.get(OpenTripApplyConstant.CarApply.TIME_RANGE));
        }
        //用车次数
        if (!ObjectUtils.isEmpty(approvalFormMap.get(OpenTripApplyConstant.CarApply.PERSON_COUNT))){
            String personCount = buildPersonCount(tripOptional);
            approvalFormMap.get(OpenTripApplyConstant.CarApply.PERSON_COUNT).setValue(personCount);
            formDTOList.add(approvalFormMap.get(OpenTripApplyConstant.CarApply.PERSON_COUNT));
        }
        //用车费用，只有员工自己填写时，estimatedAmount才会有值
        if (!ObjectUtils.isEmpty(approvalFormMap.get(OpenTripApplyConstant.CarApply.ESTIMATED_AMOUNT))){
            String estimatedAmount = buildEstimatedAmount(tripOptional);
            approvalFormMap.get(OpenTripApplyConstant.CarApply.ESTIMATED_AMOUNT).setValue(estimatedAmount);
            formDTOList.add(approvalFormMap.get(OpenTripApplyConstant.CarApply.ESTIMATED_AMOUNT));
        }
        //费用归属部门
        if (!ObjectUtils.isEmpty(approvalFormMap.get(OpenTripApplyConstant.CarApply.COST_ATTRIBUTION_DEPT))){
            String costDept = getCostAttribution(carApplyDetailDTO.getCostAttributionList(),CostAttributionTypeEnum.ORG_UNIT.getKey());
            approvalFormMap.get(OpenTripApplyConstant.CarApply.COST_ATTRIBUTION_DEPT).setValue(costDept);
            formDTOList.add(approvalFormMap.get(OpenTripApplyConstant.CarApply.COST_ATTRIBUTION_DEPT));
        }
        //费用归属项目
        if (!ObjectUtils.isEmpty(approvalFormMap.get(OpenTripApplyConstant.CarApply.COST_ATTRIBUTION_PROJECT))){
            String costProject = getCostAttribution(carApplyDetailDTO.getCostAttributionList(),CostAttributionTypeEnum.PROJECT.getKey());
            approvalFormMap.get(OpenTripApplyConstant.CarApply.COST_ATTRIBUTION_PROJECT).setValue(costProject);
            formDTOList.add(approvalFormMap.get(OpenTripApplyConstant.CarApply.COST_ATTRIBUTION_PROJECT));
        }
        return formDTOList;
    }

    /**
     * 构建用车费用
     *
     * @param tripOptional 用车信息
     * @return 用车费用
     */
    private static String buildEstimatedAmount(Optional<IntranetApplyCarDTO.Trip> tripOptional) {
        Integer priceLimitFlag = tripOptional.map(IntranetApplyCarDTO.Trip::getPriceLimitFlag).orElse(null);
        if (priceLimitFlag == null) {
            return null;
        }
        String estimatedAmount = tripOptional.map(IntranetApplyCarDTO.Trip::getEstimatedAmount).map(BigDecimal::toString).orElse(null);
        if (Integer.valueOf(0).equals(priceLimitFlag)) {
            return "不限制";
        }
        // 1 限制 3 按城市级别 4 指定城市
        if (Integer.valueOf(1).equals(priceLimitFlag) || Integer.valueOf(2).compareTo(priceLimitFlag) < 0) {
            return "管理员已做限制";
        }
        if (Integer.valueOf(2).equals(priceLimitFlag)) {
            BigDecimal estimatedAmountBig = BigDecimalUtils.obj2big(estimatedAmount,null);
            return ObjectUtils.isEmpty(estimatedAmountBig) ? null : "¥" + estimatedAmountBig.setScale(2, RoundingMode.HALF_UP).toString();

        }
        return null;
    }


    /**
     * 构建用车次数
     *
     * @param tripOptional 用车信息
     * @return 用车次数
     */
    private static String buildPersonCount(Optional<IntranetApplyCarDTO.Trip> tripOptional) {
        Integer timesLimitFlag = tripOptional.map(IntranetApplyCarDTO.Trip::getTimesLimitFlag).orElse(null);
        String personCount = tripOptional.map(IntranetApplyCarDTO.Trip::getPersonCount).orElse(null);
        if (Integer.valueOf(0).equals(timesLimitFlag)) {
            return "不限制";
        }
        if (Integer.valueOf(1).equals(timesLimitFlag)) {
            return "管理员已做限制";
        }
        if (Integer.valueOf(2).equals(timesLimitFlag)) {
            if (String.valueOf(-1).equals(personCount)) {
                return "不限制使用次数";
            } else {
                return personCount;
            }
        }
        return null;
    }

    private static String getTimeRange(IntranetApplyCarDTO.Trip trip) {
        if (trip == null){
            return null;
        }
        if (StringUtils.isBlank(trip.getEndTime())){
            return trip.getStartTime();
        }
        return trip.getStartTime()+" ~ "+trip.getEndTime();
    }
    private static String getCostAttribution(List<IntranetApplyCarDTO.CostAttribution> costAttributionList,int costAttributionCategory){
        if (CollectionUtils.isBlank(costAttributionList)){
            return null;
        }
        return costAttributionList.stream()
            .filter(costAttribution -> Integer.valueOf(costAttributionCategory).equals(costAttribution.getCostAttributionCategory()))
            .findAny().map(IntranetApplyCarDTO.CostAttribution::getCostAttributionName).orElse(null);

    }
}

