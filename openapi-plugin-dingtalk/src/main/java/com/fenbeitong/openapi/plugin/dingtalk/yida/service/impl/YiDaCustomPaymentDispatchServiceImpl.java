package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.yida.constant.YiDaCustomPaymentTypeEnum;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaCustomPaymentDispatchService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaCustomPaymentFormSyncService;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 宜搭对公付款业务调度类
 *
 * @author ctl
 * @date 2022/3/7
 */
@Service
@ServiceAspect
public class YiDaCustomPaymentDispatchServiceImpl implements IYiDaCustomPaymentDispatchService {

    @Autowired
    private YiDaCustomPaymentFormSyncServiceImpl yiDaCustomPaymentFormSyncService;

    @Override
    public void dispatch(Map<String, Object> params, OpenMsgSetup openMsgSetup, String companyId) {
        Integer type = openMsgSetup.getIntVal1();
        if (ObjectUtils.isEmpty(type)) {
            throw new FinhubException(9999, "[type]为空,未正确配置类型");
        }
        IYiDaCustomPaymentFormSyncService targetService = getTargetService(type);
        if (targetService == null) {
            throw new FinhubException(9999, "不正确的业务类型:[" + type + "]");
        }
        targetService.execute(params, companyId, openMsgSetup);
    }

    /**
     * 根据类型获取对应的业务类
     *
     * @param type
     * @return
     */
    public IYiDaCustomPaymentFormSyncService getTargetService(Integer type) {
        YiDaCustomPaymentTypeEnum yiDaCustomPaymentTypeEnum = YiDaCustomPaymentTypeEnum.getEnumByType(type);
        if (yiDaCustomPaymentTypeEnum == null) {
            return null;
        }
        // 增加新的业务时 增加枚举 增加实现类即可
        switch (yiDaCustomPaymentTypeEnum) {
            case HUIZHI:
                return yiDaCustomPaymentFormSyncService;
            default:
                return null;
        }
    }


}
