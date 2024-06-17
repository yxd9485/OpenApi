package com.fenbeitong.openapi.plugin.dingtalk.yida.service;

import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;

import java.util.Map;

/**
 * 宜搭自定义对公付款处理
 *
 * @author ctl
 * @date 2022/3/7
 */
public interface IYiDaCustomPaymentDispatchService {

    /**
     * 调度业务
     *
     * @param parameterMap
     * @param openMsgSetup 取其中的intVal1作为type
     * @param companyId
     * @see com.fenbeitong.openapi.plugin.dingtalk.yida.constant.YiDaCustomPaymentTypeEnum
     */
    void dispatch(Map<String, Object> parameterMap, OpenMsgSetup openMsgSetup, String companyId);
}
