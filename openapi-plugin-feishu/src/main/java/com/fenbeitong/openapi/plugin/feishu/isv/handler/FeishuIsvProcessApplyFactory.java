package com.fenbeitong.openapi.plugin.feishu.isv.handler;

import com.fenbeitong.openapi.plugin.feishu.isv.service.*;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 飞书市场版审批工厂类
 *
 * @author xiaohai
 * @date 2022/02/28
 */
@Component
public class FeishuIsvProcessApplyFactory {

    @Autowired
    private FeiShuIsvMultiTripReverseService feiShuIsvMultiTripReverseService;

    @Autowired
    private FeiShuIsvDinnerReverseService feiShuIsvDinnerReverseService;

    @Autowired
    private FeiShuIsvTakeawayReverseService feiShuIsvTakeawayReverseService;

    @Autowired
    private FeiShuIsvPurchaseReverseService feiShuIsvPurchaseReverseService;

    @Autowired
    private FeiShuIsvFbcouponReverseService feiShuIsvFbcouponReverseService;

    @Autowired
    private FeiShuIsvMileageReverseService feiShuIsvMileageReverseService;

    @Autowired
    private FeiShuIsvVirtualAmountReverseService feiShuIsvVirtualAmountReverseService;

    @Autowired
    private FeiShuIsvPrettyReverseService feiShuIsvPrettyReverseService;

    @Autowired
    private FeiShuIsvPaymentReverseService feiShuIsvPaymentReverseService;

    @Autowired
    private FeiShuIsvTripReverseService feiShuIsvTripReverseService;

    @Autowired
    private FeiShuIsvCarReverseServiceImpl feiShuIsvCarReverseService;

    /**
     * 钉钉申请处理类
     *
     * @param processType 20：非行程反向 11:用餐反向
     * @return 申请处理类
     */
    public IFeishuIsvProcessApplyService getProcessApply(int processType) {
        switch (processType) {
            case ProcessTypeConstant.MULTI_TRIP_REVERSE:
                return feiShuIsvMultiTripReverseService;
            case ProcessTypeConstant.DINNER_REVERSE:
                return feiShuIsvDinnerReverseService;
            case ProcessTypeConstant.MALL_REVERSE:
                //采购反向
                return feiShuIsvPurchaseReverseService;
            case ProcessTypeConstant.TAKEAWAY_REVERSE:
                //外卖反向
                return feiShuIsvTakeawayReverseService;
            case ProcessTypeConstant.FB_COUNPON_REVERSE:
                //分贝券反向
                return feiShuIsvFbcouponReverseService;
            case ProcessTypeConstant.MILEAGE_REVERSE:
                //里程补贴反向
                return feiShuIsvMileageReverseService;
            case ProcessTypeConstant.VIRTUAL_CARD_REVERSE:
                //虚拟卡额度反向
                return feiShuIsvVirtualAmountReverseService;
            case ProcessTypeConstant.VIRTUAL_CARD_PRETTY:
                //虚拟卡备用金反向
                return feiShuIsvPrettyReverseService;
            case ProcessTypeConstant.PAYMENT_APPLY:
                //对公付款反向
                return feiShuIsvPaymentReverseService;
            case ProcessTypeConstant.TRIP_REVERSE:
                //行程反向审批
                return feiShuIsvTripReverseService;
            case ProcessTypeConstant.CAR_REVERSE:
                //用车反向审批
                return feiShuIsvCarReverseService;
            default:
                return null;
        }
    }
}
