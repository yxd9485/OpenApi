package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse.DingtalkIntranetPushApplyService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @Description 订单审批反向同步
 * @Author duhui
 * @Date 2020-11-05
 **/
@Slf4j
@ServiceAspect
@Service
@Deprecated
public class DingtalkEiaIntranetPushApplyService extends DingtalkIntranetPushApplyService {
    public int getOpenType() {
        return OpenType.DINGTALK_EIA.getType();
    }

}
