package com.fenbeitong.openapi.plugin.customize.wawj.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Created by lizhen on 2020/10/28.
 */
@RestController
@RequestMapping("/customize/5i5j/orgEmployeeSync")
public class WawjOrgEmployeeSyncController {

    @Autowired
    private OpenSysConfigService openSysConfigService;

    //@FuncAuthAnnotation
    @RequestMapping("/getSyncSubCompany")
    public Object getSyncSubCompany(ApiRequest apiRequest, HttpServletRequest httpRequest) {
        String openSysConfigByCode = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.WAWJ_SYNC_CONFIG.getCode());
        HashMap data = JsonUtils.toObj(openSysConfigByCode, HashMap.class);
        return OpenapiResponseUtils.success(data);
    }

}
