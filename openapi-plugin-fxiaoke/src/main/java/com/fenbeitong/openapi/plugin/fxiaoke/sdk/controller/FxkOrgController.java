package com.fenbeitong.openapi.plugin.fxiaoke.sdk.controller;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeJobConfigDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeOrgConfigDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkSyncService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CompletableFuture;

/**
 * <p>Title: FxkSyncController</p>
 * <p>Description: 数据同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-31 15:45
 */

@Controller
@Slf4j
@RequestMapping("/fxiaoke/sync")
public class FxkOrgController {

    @Autowired
    IFxkSyncService iFxkSyncService;


    /**
     * @param jobConfig 入参配置
     * @Description 组织架构信息同步
     * @Author duhui
     * @Date 2020-08-31
     **/
    @RequestMapping("/syncOrganization")
    @ResponseBody
    public Object syncOrganization(@RequestParam("jobConfig") String jobConfig) {
        FxiaokeOrgConfigDTO fxiaokeOrgConfigDTO = JsonUtils.toObj(jobConfig, FxiaokeOrgConfigDTO.class);
        CompletableFuture.supplyAsync(() -> iFxkSyncService.syncOrganization(fxiaokeOrgConfigDTO)).exceptionally(e -> {
            log.warn("纷享销客组织架构同步异常:", e);
            return "false";
        });
        return FxkResponseUtils.success(Maps.newHashMap());
    }

}
