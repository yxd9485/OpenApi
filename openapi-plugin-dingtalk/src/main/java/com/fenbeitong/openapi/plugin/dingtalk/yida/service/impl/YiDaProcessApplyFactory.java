package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaProcessApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Title: DingtalkProcessApplyFactory</p>
 * <p>Description: 易搭审批工厂类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/24 10:21 AM
 */
@Component
public class YiDaProcessApplyFactory {

    @Autowired
    private YiDaTripApplyServiceImpl yiDaTripApplyService;

    /**
     * 易搭申请处理类
     *
     * @param processType 1：差旅审批单 3:订单审批 12：用车申请单
     * @return 易搭申请处理类
     */
    public IYiDaProcessApplyService getProcessApply(int processType) {
        switch (processType) {
            case 1:
                return yiDaTripApplyService;
            default:
                return null;
        }
    }
}
