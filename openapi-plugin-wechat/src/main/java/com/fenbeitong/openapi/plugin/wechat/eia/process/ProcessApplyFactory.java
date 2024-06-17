package com.fenbeitong.openapi.plugin.wechat.eia.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by dave.hansins on 19/12/16.
 */
@Component
public class ProcessApplyFactory {
    @Autowired
    private WeChatTripEiaEiaProcessApply processApply;

    @Autowired
    private WeChatCarEiaEiaProcessApply weChatCarProcessApply;

    @Autowired
    private WeChatOrderEiaEiaProcessApply weChatOrderProcessApply;

    @Autowired
    private WeChatTripEiaProcessApply weChatTripEiaProcessApply;

    @Autowired
    private WeChatTaxiEiaProcessApply weChatTaxiEiaProcessApply;

    @Autowired
    private WeChatOrderChangeEiaProcessApply weChatOrderChangeEiaProcessApply;

    @Autowired
    private WeChatOrderRefundEiaProcessApply weChatOrderRefundEiaProcessApply;

    @Autowired
    private WechatMallEiaProcessApply wechatMallEiaProcessApply;

    /**
     * 钉钉申请处理类
     *
     * @param processType 1：差旅审批单 12：用车申请单
     * @return 钉钉申请处理类
     */
    public IWeChatEiaProcessApply getProcessApply(int processType) {
        switch (processType) {
            case 1:
                return processApply;
            case 12:
                return weChatCarProcessApply;
            case 3:
                return weChatOrderProcessApply;
            case 6:
                return weChatTripEiaProcessApply;
            case 7:
                return weChatTaxiEiaProcessApply;
            case 8:
                return weChatOrderRefundEiaProcessApply;
            case 9:
                return weChatOrderChangeEiaProcessApply;
            case 5:
                //采购反向审批
                return wechatMallEiaProcessApply;
            default:
                return null;
        }
    }
}
