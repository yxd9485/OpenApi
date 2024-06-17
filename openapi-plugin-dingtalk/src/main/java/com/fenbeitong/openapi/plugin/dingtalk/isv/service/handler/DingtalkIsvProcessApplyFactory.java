package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler;

import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvProcessApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl.*;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Title: DingtalkProcessApplyFactory</p>
 * <p>Description: 钉钉审批工厂类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020/8/24 10:21 AM
 */
@Component
public class DingtalkIsvProcessApplyFactory {

    @Autowired
    private DingtalkIsvOrderApplyServiceImpl dingtalkIsvOrderApplyService;

    @Autowired
    private DingtalkIsvCarApplyServiceImpl dingtalkIsvCarApplyService;

    @Autowired
    private DingtalkIsvTripApplyServiceImpl dingtalkIsvTripApplyService;

    @Autowired
    private DingtalkIsvTripApplyReverseServiceImpl dingtalkIsvTripApplyReverseService;

    @Autowired
    private DingtalkIsvDinnerApplyReverseServiceImpl dingtalkIsvDinnerApplyReverseService;

    @Autowired
    private DingtalkIsvTaxiApplyReverseServiceImpl dingtalkIsvTaxiApplyReverseService;

    @Autowired
    private DingtalkIsvApproveServiceImpl dingtalkIsvApproveService;

    /**
     * 分贝通发起非行程差旅，三方审批
     */
    @Autowired
    private DingtalkIsvReverseApplyServiceImpl dingtalkIsvReverseApplyServiceImpl;

    @Autowired
    private DingtalkIsvPurchaseReverseApplyServiceImpl dingtalkIsvPurchaseReverseApplyServiceImpl;

    /**
     * 外卖反向审批
     */
    @Autowired
    private DingtalkIsvTakeawayReverseApplyServiceImpl dingtalkIsvTakeawayReverseApplyServiceImpl;

    /**
     * 分贝券反向审批
     */
    @Autowired
    private DingtalkIsvFbcouponReverseApplyServiceImpl dingtalkIsvFbcouponReverseApplyServiceImpl;

    /**
     * 里程补贴反向审批
     */
    @Autowired
    private DingtalkIsvMileageReverseApplyServiceImpl dingtalkIsvMileageReverseApplyServiceImpl;

    /**
     * 虚拟卡额度反向审批
     */
    @Autowired
    private DingtalkIsvVirtualAmountReverseApplyServiceImpl dingtalkIsvVirtualAmountReverseApplyServiceImpl;

    /**
     * 虚拟卡备用金反向审批
     */
    @Autowired
    private DingtalkIsvPrettyReverseApplyServiceImpl dingtalkIsvPrettyReverseApplyServiceImpl;

    /**
     * 对公付款反向审批
     */
    @Autowired
    private DingtalkIsvPaymentReverseApplyServiceImpl dingtalkIsvPaymentReverseApplyServiceImpl;

    /**
     * 用车套件用车审批
     */
    @Autowired
    private DingtalkIsvCarkitApplyServiceImpl dingtalkIsvCarkitApplyServiceImpl;

    /**
     * 差旅套件审批
     */
    @Autowired
    private DingtalkIsvTripkitApplyServiceImpl dingtalkIsvTripkitApplyServiceImpl;

    /**
     * 用餐审批套件
     */
    @Autowired
    private DingtalkIsvDinnerkitApplyServiceImpl dingtalkIsvDinnerkitApplyServiceImpl;

    /**
     * 外卖审批套件
     */
    @Autowired
    DingtalkIsvTakeawaykitApplyServiceImpl dingtalkIsvTakeawaykitApplyServiceImpl;


    /**
     * 钉钉申请处理类
     *
     * @param processType 1：差旅审批单 3:订单反向审批 12：用车申请单 6:差旅反向审批 7:用车反向审批 11：用餐反向审批 13:用车审批套件 14：差旅审批套件 15：用餐审批套件 16：外卖审批套件
     * @return 钉钉申请处理类
     */
    public IDingtalkIsvProcessApplyService getProcessApply(int processType) {
        switch (processType) {
            case 1:
                return dingtalkIsvApproveService;
            case 3:
                return dingtalkIsvOrderApplyService;
            case 12:
                return dingtalkIsvCarApplyService;
            case 6:
                return dingtalkIsvTripApplyReverseService;
            case 7:
                return dingtalkIsvTaxiApplyReverseService;
            case 11:
                return dingtalkIsvDinnerApplyReverseService;
            case ProcessTypeConstant.MULTI_TRIP_REVERSE:
                //差旅非行程
                return dingtalkIsvReverseApplyServiceImpl;
            case ProcessTypeConstant.MALL_REVERSE:
                //采购反向
                return dingtalkIsvPurchaseReverseApplyServiceImpl;
            case ProcessTypeConstant.TAKEAWAY_REVERSE:
                //外卖反向
                return dingtalkIsvTakeawayReverseApplyServiceImpl;
            case ProcessTypeConstant.FB_COUNPON_REVERSE:
                //分贝券反向
                return dingtalkIsvFbcouponReverseApplyServiceImpl;
            case ProcessTypeConstant.MILEAGE_REVERSE:
                //里程补贴反向
                return dingtalkIsvMileageReverseApplyServiceImpl;
            case ProcessTypeConstant.VIRTUAL_CARD_REVERSE:
                //虚拟卡额度反向
                return dingtalkIsvVirtualAmountReverseApplyServiceImpl;
            case ProcessTypeConstant.VIRTUAL_CARD_PRETTY:
                //虚拟卡备用金反向
                return dingtalkIsvPrettyReverseApplyServiceImpl;
            case ProcessTypeConstant.PAYMENT_APPLY:
                //对公付款反向
                return dingtalkIsvPaymentReverseApplyServiceImpl;
            case 13:
                return dingtalkIsvCarkitApplyServiceImpl;
            case 14:
                return dingtalkIsvTripkitApplyServiceImpl;
            case 15:
                return dingtalkIsvDinnerkitApplyServiceImpl;
            case 16:
                return dingtalkIsvTakeawaykitApplyServiceImpl;
            default:
                return null;
        }
    }
}
