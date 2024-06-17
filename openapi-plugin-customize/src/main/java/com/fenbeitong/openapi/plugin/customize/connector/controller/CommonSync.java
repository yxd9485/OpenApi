package com.fenbeitong.openapi.plugin.customize.connector.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.connector.service.CommonSyncDataService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName SyncController
 * @Description 使用连接器同步数据
 * @Company www.fenbeitong.com
 * @Author chengzhigang1
 * @Date 2022/9/16 下午15:50
 **/
@Slf4j
@Controller
@RequestMapping("/customize/connector/sync")
public class CommonSync {

    @Autowired
    private CommonSyncDataService commonSyncDataService;

    @Async
    @RequestMapping("/data/{companyId}/{settingCode}")
    @ResponseBody
    public Object syncProject(@PathVariable("companyId") String companyId, @PathVariable("settingCode") String settingCode) {
        commonSyncDataService.connectorSyncData(companyId, settingCode);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
