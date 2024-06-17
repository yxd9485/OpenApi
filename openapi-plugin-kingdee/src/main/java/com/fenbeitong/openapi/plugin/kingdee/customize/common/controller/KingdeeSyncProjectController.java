package com.fenbeitong.openapi.plugin.kingdee.customize.common.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.kingdee.customize.common.service.impl.ConnectorSyncDataServiceImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName KingdeeSyncProjectController
 * @Description 同步金蝶辅助资料
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/7/27 上午10:46
 **/
@Slf4j
@Controller
@RequestMapping("/kingdee/customize/sync")
public class KingdeeSyncProjectController {

    @Autowired
    private ConnectorSyncDataServiceImpl thirdOrgUnitSyncService;

    @Async
    @RequestMapping("/project/{companyId}/{settingCode}")
    @ResponseBody
    public Object syncKingdeeProject(@PathVariable("companyId") String companyId,@PathVariable("settingCode") String settingCode){

        thirdOrgUnitSyncService.connectorSyncKingdeeData(companyId,settingCode);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
