package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse.DingtalkIntranetPushApplyService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @Description
 * @Author duhui
 * @Date 2021-03-31
 **/
@Slf4j
@ServiceAspect
@Service
@Deprecated
public class IDingtalkIsvIntranetPushApplyService extends DingtalkIntranetPushApplyService {
    public int getOpenType() {
        return OpenType.DINGTALK_ISV.getType();
    }
}
