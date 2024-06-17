package com.fenbeitong.openapi.plugin.func.opensysconfig;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dto.OpenSysConfigReqDTO;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author lizhen
 * @date 2020/12/30
 */
@RestController
@RequestMapping("/func/openSysConfig")
@Slf4j
public class OpenSysConfigController {

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @RequestMapping("/createOpenSysConfig")
    public Object createOpenSysConfig(@Valid @RequestBody OpenSysConfigReqDTO openSysConfigReqDTO) {
        openSysConfigService.saveOpenSysConfig(openSysConfigReqDTO);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/updateOpenSysConfig")
    public Object updateOpenSysConfig(@Valid @RequestBody OpenSysConfigReqDTO openSysConfigReqDTO) {
        openSysConfigService.updateOpenSysConfig(openSysConfigReqDTO);
        return FuncResponseUtils.success(Maps.newHashMap());
    }

//    @RequestMapping("/getOpenSysConfig")
//    public Object getOpenSysConfig(@RequestParam(required = true) String type, @RequestParam(required = true) String code) {
//        List<OpenSysConfig> openSysConfigList = openSysConfigService.getOpenSysConfigList(code, type);
//        return FuncResponseUtils.success(openSysConfigList);
//    }


}
