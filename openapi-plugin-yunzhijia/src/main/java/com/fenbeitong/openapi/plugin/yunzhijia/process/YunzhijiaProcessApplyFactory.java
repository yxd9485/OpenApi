package com.fenbeitong.openapi.plugin.yunzhijia.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YunzhijiaProcessApplyFactory {
    @Autowired
    YunzhijiaTripProcessApply yunzhijiaTripProcessApply;
    @Autowired
    YunzhijiaCarProcessApply yunzhijiaCarProcessApply;
    @Autowired
    YunzhijiaOrderProcessApply yunzhijiaOrderProcessApply;
    @Autowired
    YunzhijiaRevertTripProcessApply yunzhijiaRevertTripProcessApply;
    @Autowired
    YunzhijiaRevertCarProcessApply yunzhijiaRevertCarProcessApply;

    @Autowired
    YunzhijiaRevertDinnerProcessApply dinnerProcessApply;
    @Autowired
    YunzhijiaRevertMallProcessApply mallProcessApply;
//    @Autowired
//    YunzhijiaRevertTakeOutProcessApply takeOutProcessApply;


    public IYunzhijiaProcessApply getProcessApply(int processType) {
        switch (processType) {
            case 1:
                return yunzhijiaTripProcessApply;
            case 12:
                return yunzhijiaCarProcessApply;
            case 3:
                return yunzhijiaOrderProcessApply;
                //差旅反向审批
            case 6:
                return yunzhijiaRevertTripProcessApply;
                //用车反向审批
            case 7:
                return yunzhijiaRevertCarProcessApply;
            case 11: //用餐
                return dinnerProcessApply;
            case 5: // 采购
                return mallProcessApply;
                //外卖后续支持
//            case 13: // 外卖
//                return takeOutProcessApply;
            default:
                return null;
        }
    }
}
