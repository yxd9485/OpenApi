package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenSyncThirdOrgServiceImpl;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * Created by lizhen on 2020/9/5.
 */
@ServiceAspect
@Service
public class WeChatIsvSyncThirdOrgService extends OpenSyncThirdOrgServiceImpl {

    @Override
    protected WeChatIsvSyncThirdEmployeeService getOpenEmployeeService() {
        return SpringUtils.getBean(WeChatIsvSyncThirdEmployeeService.class);
    }
}
