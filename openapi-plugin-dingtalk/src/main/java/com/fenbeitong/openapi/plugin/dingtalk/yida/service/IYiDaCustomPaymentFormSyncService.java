package com.fenbeitong.openapi.plugin.dingtalk.yida.service;

import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;

import java.util.Map;

/**
 * 宜搭对公付款表单同步定制业务
 *
 * @author ctl
 * @date 2022/3/7
 */
public interface IYiDaCustomPaymentFormSyncService {

    /**
     * 执行业务
     *
     * @param companyId
     * @param params
     * @param openMsgSetup
     */
    void execute(Map<String, Object> params, String companyId, OpenMsgSetup openMsgSetup);
}
