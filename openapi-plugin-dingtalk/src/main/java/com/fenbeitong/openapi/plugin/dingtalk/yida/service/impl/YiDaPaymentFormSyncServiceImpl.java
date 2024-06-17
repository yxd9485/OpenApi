package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaCustomPaymentDispatchService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaFormSyncService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 对公付款申请单同步
 *
 * @author ctl
 * @date 2022/3/4
 */
@Service
@ServiceAspect
public class YiDaPaymentFormSyncServiceImpl implements IYiDaFormSyncService {

    @Autowired
    private IYiDaCustomPaymentDispatchService yiDaCustomPaymentDispatchService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Override
    public void execute(Map<String, Object> params, String companyId) {
        if (ObjectUtils.isEmpty(params)) {
            throw new FinhubException(9999, "宜搭对公付款申请单参数为空");
        }
        if (StringUtils.isBlank(companyId)) {
            throw new FinhubException(9999, "[companyId]不能为空");
        }
        // 根据配置判断是通用逻辑还是特殊逻辑
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList(ItemCodeEnum.YIDA_CUSTOM_PAYMENT.getCode()));
        if (ObjectUtils.isEmpty(openMsgSetups)) {
            // 如果是空 说明这家走通用逻辑
            paymentProcess(params);
        } else {
            OpenMsgSetup openMsgSetup = openMsgSetups.get(0);
            if (ObjectUtils.isEmpty(openMsgSetup)) {
                throw new FinhubException(9999, "[openMsgSetup]为空,配置有误");
            }
            // 不为空 走定制逻辑
            customPaymentProcess(params, openMsgSetup, companyId);
        }
    }

    /**
     * 通用逻辑和可使用脚本的简单定制业务
     * 目前没有通用逻辑 待实现
     *
     * @param params
     */
    private void paymentProcess(Map<String, Object> params) {
        // 暂无通用逻辑 如果有通用逻辑在这里实现（简单定制逻辑可在此增加脚本）
    }

    /**
     * 不通用且复杂的定制业务 无法使用脚本的
     *
     * @param params
     * @param openMsgSetup
     */
    private void customPaymentProcess(Map<String, Object> params, OpenMsgSetup openMsgSetup, String companyId) {
        // 根据type 取不同的定制逻辑 （逻辑复杂 无法使用脚本的）
        yiDaCustomPaymentDispatchService.dispatch(params, openMsgSetup, companyId);
    }
}
