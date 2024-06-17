package com.fenbeitong.openapi.plugin.dingtalk.common.util;

import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.fenbeitong.fenbeipay.api.constant.enums.cashier.OrderType;
import com.fenbeitong.finhub.common.saas.entity.CostCategory;
import com.fenbeitong.openapi.plugin.support.apply.constant.ApplyTripConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.TripRoundType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.util.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析表单数据
 */
public class DingtalkParseFormUtil {

    /**
     * 行程差旅数据
     * @param apply
     * @return
     */
    public static List<OapiProcessinstanceCreateRequest.FormComponentValueVo> parseTripInfo( List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list , ApplyTripDTO apply){
        //申请事由
        String applyReason = apply.getApplyReason();
        setFormValue( ApplyTripConstant.APPLY_REASON ,  applyReason ,  list);
        setFormValue( ApplyTripConstant.APPLY_REASON_DESC ,  apply.getApplyReasonDesc() ,  list);
        List<ApplyTripDTO.TripList> tripList = apply.getTripList();
        //行程信息
        setFormValue(ApplyTripConstant.TRIP_LIST_INFO , JsonUtils.toJson(setTripInfo( tripList )) , list);
        //出行人
        setFormValue( ApplyTripConstant.APPLY_GUEST_NAME , apply.getGuestList().stream().map(ApplyTripDTO.GuestList::getName).collect(Collectors.joining(",")) ,  list);
        //出差时间
        setFormValue( ApplyTripConstant.TRAVEL_TIME , StringUtils.obj2str( apply.getTravelDay() )  + "天",  list);
        //费用归属部门和项目
        List<ApplyTripDTO.CostAttributionList> costAttributionList = apply.getCostAttributionList();
        if(CollectionUtils.isNotBlank(costAttributionList)){
            costAttributionList.forEach( costAttribution -> {
                if( costAttribution.getCostAttributionCategory() == 1){
                    //部门
                    setFormValue( ApplyTripConstant.COST_ATTRBUTION_DEPARTMENT , costAttribution.getCostAttributionName() ,  list);
                }else if(costAttribution.getCostAttributionCategory() == 2){
                    //项目
                    setFormValue( ApplyTripConstant.COST_ATTRBUTION_PROJECT ,  costAttribution.getCostAttributionName() ,  list);
                }
            });
        }
        return list;
    }


    //设置行程信息
    private static List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> setTripInfo(List<ApplyTripDTO.TripList> tripList){
        List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> formComponentValueVoList = new ArrayList<>();
        tripList.forEach( trip ->{
            List<OapiProcessinstanceCreateRequest.FormComponentValueVo> listContent = new ArrayList<>();
            int type = trip.getType();
            int tripType = trip.getTripType();
            String sceneName = convertTripSceneType(type, tripType);
            setFormValue( ApplyTripConstant.TRIP_TYPE,  sceneName , listContent);
            if(OrderType.Hotel.getKey() == type){
                // 入店时间、离店时间、目的城市
                setFormValue(ApplyTripConstant.ENTRY_DATE,   trip.getStartTime() , listContent);
                setFormValue(ApplyTripConstant.DEPARTURE_DATE,   trip.getEndTime() , listContent);
                setFormValue(ApplyTripConstant.DESTINATION_CITY,   trip.getStartCityName() , listContent);
            }else{
                //出发城市、目的城市、出发日期
                String endTime = trip.getEndTime();
                String startTime = StringUtils.isBlank(endTime) ? trip.getStartTime() : trip.getStartTime() + " ~ " + endTime ;
                setFormValue(ApplyTripConstant.START_TIME,   startTime , listContent);
                setFormValue(ApplyTripConstant.START_CITY,   trip.getStartCityName() , listContent);
                setFormValue(ApplyTripConstant.DESTINATION_CITY,   trip.getArrivalCityName() , listContent);
                if(TripRoundType.RoungTrip.getValue() == tripType){
                    //往返
                    setFormValue( ApplyTripConstant.RETURN_TIME,   trip.getEndTime() , listContent);
                }
            }
            String amount = String.format("%.2f", trip.getEstimatedAmount());
            if(!"0.00".equals(amount)){
                setFormValue( ApplyTripConstant.KEY_TRIP_FEE, "¥ " + amount , listContent);
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
     * 外卖表单解析
     * @param applyTakeAwayNoticeDTO
     * @return
     */
    public static List<OapiProcessinstanceCreateRequest.FormComponentValueVo> setTakeawayApply(ApplyTakeAwayNoticeDTO applyTakeAwayNoticeDTO){

        ApplyTakeAwayNoticeDTO.Apply apply = applyTakeAwayNoticeDTO.getApply();
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list = CollectionUtils.newArrayList();
        //申请事由
        String applyReason = apply.getApplyReason();
        String applyReasonDesc = apply.getApplyReasonDesc();
        if(!StringUtils.isBlank(applyReasonDesc)){
            applyReason = applyReason + ";" + applyReasonDesc;
        }
        setFormValue( OpenTripApplyConstant.takeawayApply.APPLY_REASON ,  applyReason ,  list);
        //送餐日期
        ApplyTakeAwayNoticeDTO.TakeOut takeOut = applyTakeAwayNoticeDTO.getTakeOut();
        setFormValue( OpenTripApplyConstant.takeawayApply.APPLY_DATE ,  takeOut.getStartTime() ,  list);
        //送餐时段
        setFormValue( OpenTripApplyConstant.takeawayApply.APPLY_INTERVAL ,  takeOut.getTimeRange() ,  list);
        //送餐地址
        setFormValue( OpenTripApplyConstant.takeawayApply.APPLY_ADDRESS ,  takeOut.getAddressName() ,  list);
        //总预估费用
        setFormValue( OpenTripApplyConstant.takeawayApply.APPLY_FEE ,   "¥ " + bigDecimalToStr( apply.getBudget() ) ,  list);
        //费用归属
        ApplyTakeAwayNoticeDTO.SaasInfo saasInfo = applyTakeAwayNoticeDTO.getSaasInfo();
        List<ApplyTakeAwayNoticeDTO.CostAttributionDTO> costAttributionGroupList = saasInfo.getCostAttributionGroupList();
        if(CollectionUtils.isNotBlank(costAttributionGroupList)){
            costAttributionGroupList.forEach( costAttribution -> {
                List<ApplyTakeAwayNoticeDTO.CostAttributionDTO.Detail> costAttributionList = costAttribution.getCostAttributionList();
                if( costAttribution.getCategory() == 1){
                    //部门
                    if(CollectionUtils.isNotBlank(costAttributionList)) {
                        String name = costAttributionList.get(0).getName();
                        setFormValue(OpenTripApplyConstant.takeawayApply.COST_ATTRIBUTION_DEPARTMENT, name, list);
                    }
                }else if(costAttribution.getCategory() == 2){
                    //项目
                    if(CollectionUtils.isNotBlank(costAttributionList)) {
                        String name = costAttributionList.get(0).getName();
                        setFormValue(OpenTripApplyConstant.takeawayApply.COST_ATTRIBUTION_PROJECT, name, list);
                    }
                }
            });
        }
        return list;
    }


    /**
     * 采购表单解析
     * @param apply
     * @return
     */
    public static List<OapiProcessinstanceCreateRequest.FormComponentValueVo> setMallApply( MallApplyDTO apply ){

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list = CollectionUtils.newArrayList();
        //申请事由
        List<MallApplyDTO.TripInfo> tripList = apply.getTripList();
        if(CollectionUtils.isBlank(tripList)){
            return list;
        }
        MallApplyDTO.TripInfo tripInfo = tripList.get(0);
        String applyOrderReason = tripInfo.getOrderReason();
        String applyReasonReasonDesc = tripInfo.getOrderReasonDesc();
        if(!StringUtils.isBlank(applyReasonReasonDesc)){
            applyOrderReason = applyOrderReason + ";" + applyReasonReasonDesc;
        }
        setFormValue(OpenTripApplyConstant.mallApply.APPLY_REASON , applyOrderReason , list);
        //申请描述
        setFormValue(OpenTripApplyConstant.mallApply.APPLY_DESCRIPTION , apply.getApplyDesc() , list);
        //采购总额
        setFormValue(OpenTripApplyConstant.mallApply.MALL_FEE , "¥ " + bigDecimalToStr(BigDecimalUtils.fen2yuan(apply.getApplyTotalPrice()) ), list);
        //费用归属
        List<MallApplyDTO.CostAttributionDTO> costAttributionList = apply.getAttributionList();
        if(CollectionUtils.isNotBlank(costAttributionList)){
            costAttributionList.forEach( costAttribution -> {
                if( costAttribution.getCostAttributionCategory() == 1){
                    //部门
                    setFormValue( OpenTripApplyConstant.mallApply.COST_ATTRIBUTION_DEPARTMENT , costAttribution.getCostAttributionName() ,  list);
                }else if(costAttribution.getCostAttributionCategory() == 2){
                    //项目
                    setFormValue( OpenTripApplyConstant.mallApply.COST_ATTRIBUTION_PROJECT ,  costAttribution.getCostAttributionName() ,  list);
                }
            });
        }
        //费用类别
        MallApplyDTO.CostCategory costCategory = apply.getCostCategory();
        if(costCategory!=null){
            setFormValue( OpenTripApplyConstant.mallApply.COST_TYPE ,  costCategory.getName() ,  list);
        }
        //商品信息
        setFormValue(OpenTripApplyConstant.mallApply.PRODUCE_INFO , JsonUtils.toJson(setProduceInfo( tripInfo )) , list);
        MallApplyDTO.MallPriceStructure mallPriceStructure = tripInfo.getMallPriceStructure();
        if(mallPriceStructure ==  null){
            return list;
        }
        //商品金额
        setFormValue(OpenTripApplyConstant.mallApply.PRODUCE_AMOUNT , "¥ " +bigDecimalToStr( mallPriceStructure.getTotalPrice() ) , list);
        //商品运费
        setFormValue(OpenTripApplyConstant.mallApply.PRODUCE_TRNSPORT_FEE , "¥ " +bigDecimalToStr( mallPriceStructure.getFreight() ) , list);

        MallApplyDTO.AddressInfo addressInfo = tripInfo.getAddressInfo();
        if(addressInfo == null){
            return list;
        }
        //配送信息
        String address = (addressInfo.getProvince() == null ? "":addressInfo.getProvince()) +
            (addressInfo.getCity() == null ? "" : addressInfo.getCity() )+
            (addressInfo.getCounty() == null ? "" : addressInfo.getCounty()) +
            (addressInfo.getTown() == null ? "" : addressInfo.getTown()) +
            (addressInfo.getDetail() == null ? "" : addressInfo.getDetail());
        setFormValue(OpenTripApplyConstant.mallApply.PRODUCE_TRANSPORT_INFO , addressInfo.getName() + "  "+  addressInfo.getPhone() +"  "+ address , list);
        return list;
    }

    /**
     * 分贝券表单解析
     * @param fbCouponApplyDetail
     * @return
     */
    public static List<OapiProcessinstanceCreateRequest.FormComponentValueVo> setFbCouponApply(FBCouponApplyDetailDTO fbCouponApplyDetail){
        FBCouponApplyDetailDTO.Apply apply = fbCouponApplyDetail.getApply();
        FBCouponApplyDetailDTO.Coupon coupon = fbCouponApplyDetail.getCoupon();
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list = CollectionUtils.newArrayList();
        //申请事由
        String applyReason = apply.getApplyReason();
        String applyReasonDesc = apply.getApplyReasonDesc();
        if(!StringUtils.isBlank(applyReasonDesc)){
            applyReason = applyReason + ";" + applyReasonDesc;
        }
        setFormValue( OpenTripApplyConstant.fbCouponApply.APPLY_REASON ,  applyReason ,  list);
        //券名称
        setFormValue( OpenTripApplyConstant.fbCouponApply.COUPON_NAME ,  coupon.getName() ,  list);
        //面值
        setFormValue( OpenTripApplyConstant.fbCouponApply.ESTIMATED_AMOUNT ,   StringUtils.obj2str(coupon.getEstimatedAmount()) +" 元" ,  list);
        //有效期（开始时间和结束时间）
        setFormValue( OpenTripApplyConstant.fbCouponApply.VALID_DATE ,   coupon.getStartTime() + " ~ " + coupon.getEndTime() ,  list);
        //申请张数
        setFormValue( OpenTripApplyConstant.fbCouponApply.COUPON_COUNT ,   StringUtils.obj2str(coupon.getCount()) ,  list);
        //有效时长
        setFormValue( OpenTripApplyConstant.fbCouponApply.VALID_TIME ,   coupon.getValidTime() ,  list);
        //申请总金额
        setFormValue( OpenTripApplyConstant.fbCouponApply.PRODUCE_INFO ,   "¥ " + bigDecimalToStr(apply.getBudget()) ,  list);
        //申请说明
        setFormValue( OpenTripApplyConstant.fbCouponApply.APPLY_REASON_DESC ,   coupon.getRemark() ,  list);
        return list;
    }

    private static void setFormValue(String formName ,  String formValue , List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list){
        if(formValue != null){
            OapiProcessinstanceCreateRequest.FormComponentValueVo formComponentValueVo = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            formComponentValueVo.setName( formName );
            formComponentValueVo.setValue( formValue );
            list.add(formComponentValueVo);
        }
    }


    /**
     * 里程补贴
     * @param mileageSubsidyNoticeDTO
     * @return
     */
    public static List<OapiProcessinstanceCreateRequest.FormComponentValueVo> setMileageApply(MileageSubsidyNoticeDTO mileageSubsidyNoticeDTO){

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list = CollectionUtils.newArrayList();

        MileageSubsidyNoticeDTO.Apply apply = mileageSubsidyNoticeDTO.getApply();
        MileageSubsidyNoticeDTO.SaasInfo saasInfo = mileageSubsidyNoticeDTO.getSaasInfo();
        //申请事由
        setFormValue( OpenTripApplyConstant.mileagesApply.APPLY_REASON ,  apply.getReason() ,  list);
        //事由补充
        setFormValue( OpenTripApplyConstant.mileagesApply.APPLY_REASON_DESC ,  apply.getReasonDesc() ,  list);
        //费用类别
        CostCategory costCategory = saasInfo.getCostCategory();
        if( costCategory != null){
            setFormValue( OpenTripApplyConstant.mileagesApply.COST_TYPE , costCategory.getName() ,  list);
        }
        //申请里程补贴合计
        setFormValue( OpenTripApplyConstant.mileagesApply.MILEAGES_AMOUNT_COUNT , StringUtils.obj2str( apply.getBudget() ),  list);
        //里程补贴明细
        setFormValue( OpenTripApplyConstant.mileagesApply.MILEAGES_DETAIL , apply.getUnableAllowanceReason() ,  list);
        //费用归属
        List<MileageSubsidyNoticeDTO.CostAttributionDTO> costAttributionGroupList = saasInfo.getCostAttributionGroupList();
        if(CollectionUtils.isNotBlank(costAttributionGroupList)){
            costAttributionGroupList.forEach( costAttribution -> {
                List<MileageSubsidyNoticeDTO.CostAttributionDTO.Detail> costAttributionList = costAttribution.getCostAttributionList();
                if( costAttribution.getCategory() == 1){
                    //部门
                    if(CollectionUtils.isNotBlank(costAttributionList)) {
                        String name = costAttributionList.get(0).getName();
                        setFormValue(OpenTripApplyConstant.mileagesApply.COST_ATTRIBUTION_DEPARTMENT , name , list);
                    }
                }else if(costAttribution.getCategory() == 2){
                    //项目
                    if(CollectionUtils.isNotBlank(costAttributionList)) {
                        String name = costAttributionList.get(0).getName();
                        setFormValue(OpenTripApplyConstant.mileagesApply.COST_ATTRIBUTION_PROJECT, name , list);
                    }
                }
            });
        }
        List<MileageSubsidyNoticeDTO.Mileage> mileages = mileageSubsidyNoticeDTO.getMileages();
        if(CollectionUtils.isNotBlank(mileages)){
            //里程补贴记录
            setFormValue( OpenTripApplyConstant.mileagesApply.MILEAGES_RECORD , JsonUtils.toJson(setMileageRecordInfo( mileages )) ,  list);
        }
        return list;
    }


    /**
     * 虚拟卡额度申请
     * @param virtualCardAmountDetailDTO
     * @return
     */
    public static List<OapiProcessinstanceCreateRequest.FormComponentValueVo> setVirtualCardAmountApply(VirtualCardAmountDetailDTO virtualCardAmountDetailDTO){

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list = CollectionUtils.newArrayList();

        VirtualCardAmountDetailDTO.Apply apply = virtualCardAmountDetailDTO.getApply();
        VirtualCardAmountDetailDTO.SaasInfo saasInfo = virtualCardAmountDetailDTO.getSaasInfo();
        //备用金名称
        setFormValue( OpenTripApplyConstant.virtualCardApply.APPLY_TITLE , apply.getApplyTitle() ,  list);
        //申请事由
        String applyReason = apply.getApplyReason();
        if(StringUtils.isBlank(apply.getApplyReasonDesc())){
            applyReason = applyReason+";"+ apply.getApplyReasonDesc();
        }
        setFormValue( OpenTripApplyConstant.virtualCardApply.APPLY_REASON , applyReason ,  list);
        //申请金额
        setFormValue( OpenTripApplyConstant.virtualCardApply.APPLY_AMOUNT , StringUtils.obj2str( apply.getApplyAmount()) ,  list);
        VirtualCardAmountDetailDTO.VirtualCard virtualCard = virtualCardAmountDetailDTO.getVirtualCard();
        if(virtualCard!=null){
            //虚拟卡使用情况
            setFormValue( OpenTripApplyConstant.virtualCardApply.VIRTUAL_CARD , JsonUtils.toJson(setVirtualCardInfo( virtualCard )) ,  list);
        }
        //费用类别和费用归属部门
        if(saasInfo==null){
            return list;
        }
        if( saasInfo.getCostCategory()!= null ){
            setFormValue( OpenTripApplyConstant.virtualCardApply.COST_TYPE, saasInfo.getCostCategory().getName() , list);
        }
        List<VirtualCardAmountDetailDTO.CostAttributionDTO> costAttributionGroupList = saasInfo.getCostAttributionGroupList();
        if(CollectionUtils.isNotBlank(costAttributionGroupList)){
            costAttributionGroupList.forEach( costAttribution -> {
                List<VirtualCardAmountDetailDTO.CostAttributionDTO.Detail> costAttributionList = costAttribution.getCostAttributionList();
                if( costAttribution.getCategory() == 1){
                    //部门
                    if(CollectionUtils.isNotBlank(costAttributionList)){
                        String name = costAttributionList.get(0).getName();
                        setFormValue( OpenTripApplyConstant.virtualCardApply.COST_ATTRIBUTION_DEPARTMENT , name ,  list);
                    }
                }else if(costAttribution.getCategory() == 2){
                    //项目
                    if(CollectionUtils.isNotBlank(costAttributionList)) {
                        String name = costAttributionList.get(0).getName();
                        setFormValue(OpenTripApplyConstant.virtualCardApply.COST_ATTRIBUTION_PROJECT, name , list);
                    }
                }
            });
        }
        return list;
    }


    /**
     * 对公付款审批单
     * @param paymentApplyDetailDTO
     * @return
     */
    public static List<OapiProcessinstanceCreateRequest.FormComponentValueVo> setPaymentApply(PaymentApplyDetailDTO paymentApplyDetailDTO , String proofInfoFile , String invoiceInfoFile){

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list = CollectionUtils.newArrayList();

        PaymentApplyDetailDTO.Apply apply = paymentApplyDetailDTO.getApply();
        PaymentApplyDetailDTO.SaasInfo saasInfo = paymentApplyDetailDTO.getSaasInfo();
        PaymentApplyDetailDTO.PaymentInfo paymentInfo = paymentApplyDetailDTO.getPaymentInfo();
        //申请事由
        setFormValue( OpenTripApplyConstant.paymentApply.APPLY_REASON , apply.getApplyReason() ,  list);
        //事由补充
        setFormValue( OpenTripApplyConstant.paymentApply.APPLY_REASON_DESC , apply.getApplyReasonDesc() ,  list);
        if(paymentInfo!=null){
            //名称
            setFormValue( OpenTripApplyConstant.paymentApply.PAYMENT_TILTE , paymentInfo.getPaymentTitle() ,  list);
            //付款金额
            setFormValue( OpenTripApplyConstant.paymentApply.PAYMENT_AMOUNT ,"¥ " + bigDecimalToStr(paymentInfo.getPaymentAmount()) ,  list);
            //付款账户
            setFormValue( OpenTripApplyConstant.paymentApply.PAYMENT_ACCOUNT ,paymentInfo.getPaymentAccount() ,  list);
            //付款账号
            setFormValue( OpenTripApplyConstant.paymentApply.PAYMENT_ACCOUNT_NUMBER ,paymentInfo.getPaymentAccountNumber() ,  list);
            //开户行名称
            // setFormValue( OpenTripApplyConstant.paymentApply.PAYMENT_BANK_NAME ,paymentInfo.getPaymentBankName() ,  list);
            //付款时间
            setFormValue( OpenTripApplyConstant.paymentApply.PAYMENT_TIME ,paymentInfo.getPaymentTime() ,  list);
            //用途
            setFormValue( OpenTripApplyConstant.paymentApply.PAYMENT_PURPUSE ,paymentInfo.getPaymentPurpose() ,  list);
        }
        //费用类别和费用归属部门
        if(saasInfo!=null){
            List<PaymentApplyDetailDTO.CostAttributionDTO> costAttributionGroupList = saasInfo.getCostAttributionGroupList();
            if(CollectionUtils.isNotBlank(costAttributionGroupList)){
                costAttributionGroupList.forEach( costAttribution -> {
                    List<PaymentApplyDetailDTO.CostAttributionDTO.Detail> costAttributionList = costAttribution.getCostAttributionList();
                    if( costAttribution.getCategory() == 1){
                        //部门
                        if(CollectionUtils.isNotBlank(costAttributionList)) {
                            String name = costAttributionList.get(0).getName();
                            setFormValue(OpenTripApplyConstant.virtualCardApply.COST_ATTRIBUTION_DEPARTMENT, name , list);
                        }
                    }else if(costAttribution.getCategory() == 2){
                        //项目
                        if(CollectionUtils.isNotBlank(costAttributionList)) {
                            String name = costAttributionList.get(0).getName();
                            setFormValue(OpenTripApplyConstant.virtualCardApply.COST_ATTRIBUTION_PROJECT, name , list);
                        }
                    }
                });
            }
        }
        //供应商信息
        PaymentApplyDetailDTO.SupplierInfo supplierInfo = paymentApplyDetailDTO.getSupplierInfo();
        if(supplierInfo!=null){
            setFormValue( OpenTripApplyConstant.paymentApply.SUPPLIER_INFO , JsonUtils.toJson(setSupplierInfo( supplierInfo )) ,  list);
        }
        //合同信息
        PaymentApplyDetailDTO.ContractInfo contractInfo = paymentApplyDetailDTO.getContractInfo();
        if(contractInfo!=null){
            setFormValue( OpenTripApplyConstant.paymentApply.CONTRACT_INFO , JsonUtils.toJson(setContractInfo( contractInfo )) ,  list);
        }
        //凭证信息
        PaymentApplyDetailDTO.ProofInfo proofInfo = paymentApplyDetailDTO.getProofInfo();
        if(proofInfo!=null){
            setFormValue( OpenTripApplyConstant.paymentApply.PROOF_INFO , JsonUtils.toJson(setProofInfo( proofInfo , proofInfoFile)) ,  list);
        }
        //发票信息
        List<PaymentApplyDetailDTO.InvoiceInfo> invoiceInfos = paymentApplyDetailDTO.getInvoiceInfos();
        if(CollectionUtils.isNotBlank(invoiceInfos)){
            String invoiceStatus = apply.getInvoiceStatus();
            setFormValue( OpenTripApplyConstant.paymentApply.INVOICE_INFO , JsonUtils.toJson(setInvoiceInfo( invoiceInfos  , invoiceStatus , invoiceInfoFile)) ,  list);
        }
        return list;
    }

    /**
     * 设置凭证信息
     * @param proofInfo
     * @return
     */
    private static List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> setProofInfo( PaymentApplyDetailDTO.ProofInfo proofInfo  , String proofInfoFile ){
        List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> formComponentValueVoList = new ArrayList<>();
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> listContent = new ArrayList<>();
        //凭证名称
        setFormValue( OpenTripApplyConstant.paymentApply.PROOF_NAME , proofInfo.getProofName() ,  listContent);
        //凭证说明
        setFormValue( OpenTripApplyConstant.paymentApply.PROOF_REMARK , proofInfo.getRemark(),  listContent);
        //凭证金额
        if(proofInfo.getProofPrice()!=null){
            setFormValue( OpenTripApplyConstant.paymentApply.PROOF_PRICE , "¥ " + bigDecimalToStr( proofInfo.getProofPrice() ),  listContent);
        }
        //凭证附件
        setFormValue( OpenTripApplyConstant.paymentApply.PROOF_ATTACHMENT_URL ,  proofInfoFile ,  listContent);
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }

    /**
     * 设置发票信息
     * @param invoiceInfos
     * @return
     */
    private static List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> setInvoiceInfo( List<PaymentApplyDetailDTO.InvoiceInfo> invoiceInfos , String invoiceStatus , String invoiceInfoFile){
        List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> formComponentValueVoList = new ArrayList<>();
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> listContent = new ArrayList<>();
        if("0".equals(invoiceStatus)){
            setFormValue( OpenTripApplyConstant.paymentApply.INVOICE_STATUS , "待开票",  listContent);
        }else if("2".equals(invoiceStatus)){
            setFormValue( OpenTripApplyConstant.paymentApply.INVOICE_STATUS , "无发票",  listContent);
        }else if("1".equals(invoiceStatus)){
            setFormValue( OpenTripApplyConstant.paymentApply.INVOICE_STATUS , "已开票",  listContent);
            BigDecimal invoiceTotalAmount = new BigDecimal(0);
            for (PaymentApplyDetailDTO.InvoiceInfo invoiceInfo : invoiceInfos) {
                if(invoiceInfo.getInvoiceTotalAmount()!=null){
                    invoiceTotalAmount = invoiceTotalAmount.add(invoiceInfo.getInvoiceTotalAmount());
                }
            }
            setFormValue( OpenTripApplyConstant.paymentApply.INVOICE_TOTAL_AMOUNT , StringUtils.obj2str(invoiceTotalAmount) ,  listContent);
            setFormValue( OpenTripApplyConstant.paymentApply.INVOICE_ATTACHMENT_URL , invoiceInfoFile ,  listContent);
        }
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }

    /**
     * 设置合同信息
     * @param contractInfo
     * @return
     */
    private static List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> setContractInfo( PaymentApplyDetailDTO.ContractInfo contractInfo ){
        List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> formComponentValueVoList = new ArrayList<>();
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> listContent = new ArrayList<>();
        //合同名称
        setFormValue( OpenTripApplyConstant.paymentApply.CONTRACT_NAME , contractInfo.getName(),  listContent);
        //合同金额
        if(contractInfo.getPrice()!=null){
            setFormValue( OpenTripApplyConstant.paymentApply.CONTRACT_PRICE , "¥ " + bigDecimalToStr(contractInfo.getPrice()),  listContent);
        }
        //合同有效期
        if(contractInfo.getStartTime()!=null){
            setFormValue( OpenTripApplyConstant.paymentApply.CONTRACT_TIME , contractInfo.getStartTime() + " ~ " + contractInfo.getEndTime(),  listContent);
        }
        //合同甲方名称
        setFormValue( OpenTripApplyConstant.paymentApply.CONTRACT_PARTYA_NAME , contractInfo.getPartyAName(),  listContent);
        //合同乙方名称
        setFormValue( OpenTripApplyConstant.paymentApply.CONTRACT_PARTYB_NAME , contractInfo.getPartyBName(),  listContent);
        //合同编号
        setFormValue( OpenTripApplyConstant.paymentApply.CONTRACT_CODE , contractInfo.getCode(),  listContent);
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }

    /**
     * 设置供应商信息
     * @param supplierInfo
     * @return
     */
    private static List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> setSupplierInfo( PaymentApplyDetailDTO.SupplierInfo supplierInfo ){
        List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> formComponentValueVoList = new ArrayList<>();
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> listContent = new ArrayList<>();
        //供应商开户行支行
        setFormValue( OpenTripApplyConstant.paymentApply.SUPPLIER_BANK_NAME , supplierInfo.getBankName() + supplierInfo.getSubBranch(),  listContent);
        //供应商收款账号
        setFormValue( OpenTripApplyConstant.paymentApply.SUPPLIER_BANK_ACCOUNT_NAME , supplierInfo.getBankAccountName(),  listContent);
        //供应商收款账户
        setFormValue( OpenTripApplyConstant.paymentApply.SUPPLIER_BANK_ACCOUNT , supplierInfo.getBankAccount(),  listContent);
        //供应商名称
        setFormValue( OpenTripApplyConstant.paymentApply.SUPPLIER_NAME , supplierInfo.getName(),  listContent);
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }

    /**
     * 设置虚拟卡使用情况信息
     * @param virtualCard
     * @return
     */
    private static List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> setVirtualCardInfo( VirtualCardAmountDetailDTO.VirtualCard virtualCard ){
        List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> formComponentValueVoList = new ArrayList<>();
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> listContent = new ArrayList<>();
        setFormValue( OpenTripApplyConstant.virtualCardApply.WRITTEN_OFF_AMOUNT , "¥ " + bigDecimalToStr(virtualCard.getWrittenOffAmount()) ,  listContent);
        setFormValue( OpenTripApplyConstant.virtualCardApply.AVAILABLE_AMOUNT , "¥ " + bigDecimalToStr(virtualCard.getAvailableAmount()) ,  listContent);
        formComponentValueVoList.add(listContent);
        return formComponentValueVoList;
    }

    //设置里程补贴记录信息
    private static List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> setMileageRecordInfo( List<MileageSubsidyNoticeDTO.Mileage> mileages ){
        List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> formComponentValueVoList = new ArrayList<>();
        mileages.forEach( mileage ->{
            List<OapiProcessinstanceCreateRequest.FormComponentValueVo> listContent = new ArrayList<>();
            OapiProcessinstanceCreateRequest.FormComponentValueVo distanceVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            distanceVO.setName(OpenTripApplyConstant.mileagesApply.MILEAGES_DISTANCE);
            distanceVO.setValue( mileage.getDistance() + "KM");
            listContent.add( distanceVO );

            OapiProcessinstanceCreateRequest.FormComponentValueVo startAddressVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            startAddressVO.setName(OpenTripApplyConstant.mileagesApply.START_ADDRESS);
            startAddressVO.setValue( mileage.getStartAddress() );
            listContent.add( startAddressVO );

            OapiProcessinstanceCreateRequest.FormComponentValueVo endAddressVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            endAddressVO.setName(OpenTripApplyConstant.mileagesApply.END_ADDRESS);
            endAddressVO.setValue( mileage.getEndAddress() );
            listContent.add( endAddressVO );

            OapiProcessinstanceCreateRequest.FormComponentValueVo dateVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            dateVO.setName(OpenTripApplyConstant.mileagesApply.MILEAGES_DATE);
            dateVO.setValue( mileage.getStartTime() + " ~ " +  mileage.getEndTime());
            listContent.add( dateVO );

            OapiProcessinstanceCreateRequest.FormComponentValueVo mountVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            mountVO.setName(OpenTripApplyConstant.mileagesApply.MILEAGES_AMOUNT);
            mountVO.setValue( "¥ " + bigDecimalToStr(mileage.getEstimatedAmount()));
            listContent.add( mountVO );

            formComponentValueVoList.add(listContent);
        });
        return formComponentValueVoList;
    }

    //设置商品信息
    private static List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> setProduceInfo(MallApplyDTO.TripInfo tripInfo){
        List<MallApplyDTO.MallDTO> mallList = tripInfo.getMallDTOList();
        List<List<OapiProcessinstanceCreateRequest.FormComponentValueVo>> formComponentValueVoList = new ArrayList<>();
        mallList.forEach( mall ->{
            List<OapiProcessinstanceCreateRequest.FormComponentValueVo> listContent = new ArrayList<>();
            setFormValue( OpenTripApplyConstant.mallApply.PRODUCE_NAME ,  mall.getName() ,  listContent);
            setFormValue( OpenTripApplyConstant.mallApply.PRODUCE_PRICE ,  "¥ " + bigDecimalToStr( mall.getSalePrice() ) ,  listContent);
            setFormValue( OpenTripApplyConstant.mallApply.PRODUCE_COUNT ,  StringUtils.obj2str( mall.getAmount() )  ,  listContent);
            formComponentValueVoList.add(listContent);
        });
        return formComponentValueVoList;
    }

    /**
     * 金额转换字符串，null时转换成--
     * @param amount
     * @return
     */
    private static String bigDecimalToStr(BigDecimal amount){
        return  StringUtils.obj2str( amount == null ? "--" : amount );
    }


}

