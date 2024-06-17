package com.fenbeitong.openapi.plugin.kingdee.customize.pengkai.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.kingdee.customize.pengkai.service.KingdeeProjectSyncService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName KingdeeProjectSyncController
 * @Description 金蝶项目拉取
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/7/7 上午10:29
 **/
@RestController
@Slf4j
@RequestMapping("/customize/sync")
public class KingdeeProjectSyncController {

    @Autowired
    private KingdeeProjectSyncService kingdeeProjectSyncService;

    @Async
    @RequestMapping("/supportingInformation/{companyId}")
    @ResponseBody
    public Object syncKingdeeProject(@PathVariable("companyId") String companyId){

         kingdeeProjectSyncService.syncKingdeeProject(companyId);
         return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
