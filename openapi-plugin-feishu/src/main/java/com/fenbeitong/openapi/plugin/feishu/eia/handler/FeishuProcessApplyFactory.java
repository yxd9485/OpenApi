package com.fenbeitong.openapi.plugin.feishu.eia.handler;

import com.fenbeitong.openapi.plugin.feishu.eia.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 飞书审批工厂类
 *
 * @author yan.pb
 * @date 2021/2/19
 */
@Component
public class FeishuProcessApplyFactory {

    @Autowired
    private FeishuEiaTripApplyService feishuEiaTripApplyService;

    @Autowired
    private FeishuEiaTaxiApplyService feishuEiaTaxiApplyService;

    @Autowired
    private FeishuEiaTripApplyReverseService feishuEiaTripApplyReverseService;

    @Autowired
    private FeishuEiaTaxiApplyReverseService feishuEiaTaxiApplyReverseService;

    @Autowired
    private FeishuEiaOrderApplyReverseService feishuEiaOrderApplyReverseService;

    @Autowired
    private FeishuEiaOrderApplyRefundService feishuEiaOrderApplyRefundService;

    @Autowired
    private FeishuEiaOrderApplyChangeService feishuEiaOrderApplyChangeService;

    @Autowired
    private FeiShuEiaMallProcessApplyService feiShuEiaMallProcessApplyService;

    @Autowired
    private FeiShuEiaDinnerProcessApplyService feiShuEiaDinnerProcessApplyService;

    /**
     * 钉钉申请处理类
     *
     * @param processType 1：差旅审批单 3:订单审批 12：用车申请单
     * @return 申请处理类
     */
    public IFeishuProcessApplyService getProcessApply(int processType) {
        switch (processType) {
            case 1:
                return feishuEiaTripApplyService;
            case 12:
                return feishuEiaTaxiApplyService;
            case 6:
                return feishuEiaTripApplyReverseService;
            case 7:
                return feishuEiaTaxiApplyReverseService;
            case 3:
                return feishuEiaOrderApplyReverseService;
            case 8:
                return feishuEiaOrderApplyRefundService;
            case 9:
                return feishuEiaOrderApplyChangeService;
            case 5:
                // 采购
                return feiShuEiaMallProcessApplyService;
            case 11:
                // 用车
                return feiShuEiaDinnerProcessApplyService;
            default:
                return null;
        }
    }
}
