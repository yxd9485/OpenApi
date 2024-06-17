package com.fenbeitong.openapi.plugin.dingtalk.eia.service.handler;

import com.fenbeitong.openapi.plugin.dingtalk.eia.service.DingtalkMallApplyReverseServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkProcessApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.*;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Title: DingtalkProcessApplyFactory</p>
 * <p>Description: 钉钉审批工厂类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/24 10:21 AM
 */
@Component
public class DingtalkProcessApplyFactory {

    @Autowired
    private DingtalkTripApplyServiceImpl dingtalkTripApplyService;

    @Autowired
    private DingtalkCarApplyServiceImpl dingtalkCarApplyService;

    @Autowired
    DingtalkOrderApplyServiceImpl dingtalkOrderApplyService;

    @Autowired
    private DingtalkDinnerApplyServiceImpl dingtalkDinnerApplyService;

    @Autowired
    private DingtalkTripApplyReverseServiceImpl dingtalkTripApplyReverseService;

    @Autowired
    private DingtalkTaxiApplyReverseServiceImpl dingtalkTaxiApplyReverseService;

    @Autowired
    private DingtalkMallApplyReverseServiceImpl dingtalkMallApplyReverseService;

    @Autowired
    private DingtalkOrderApplyRefundServiceImpl dingtalkOrderApplyRefundService;

    @Autowired
    private DingtalkOrderApplyChangeServiceImpl dingtalkOrderApplyChangeService;

    @Autowired
    private DingtalkEiaDinnerRevertProcessApplyService dingtalkEiaDinnerRevertProcessApplyService;

    @Autowired
    private DingtalkEiaMultiTripReverseApplyServiceImpl dingtalkEiaMultiTripReverseApplyServiceImpl;

    /**
     * 钉钉申请处理类
     *
     * @param processType 1：差旅审批单 3:订单审批 12：用车申请单
     * @return 钉钉申请处理类
     */
    public IDingtalkProcessApplyService getProcessApply(int processType) {
        switch (processType) {
            case 1:
                return dingtalkTripApplyService;
            case 3:
                return dingtalkOrderApplyService;
            case 12:
                return dingtalkCarApplyService;
            case 60:
                return dingtalkDinnerApplyService;
            case 6:
                return dingtalkTripApplyReverseService;
            case 7:
                return dingtalkTaxiApplyReverseService;
            case 5:
                return dingtalkMallApplyReverseService;
            case 8:
                return dingtalkOrderApplyRefundService;
            case 9:
                return dingtalkOrderApplyChangeService;
            case 11:
                return dingtalkEiaDinnerRevertProcessApplyService;
            case ProcessTypeConstant.MULTI_TRIP_REVERSE:
                //差旅非行程
                return dingtalkEiaMultiTripReverseApplyServiceImpl;
            default:
                return null;
        }
    }
}
