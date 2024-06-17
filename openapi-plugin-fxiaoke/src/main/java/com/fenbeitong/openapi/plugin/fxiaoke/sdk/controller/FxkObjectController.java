package com.fenbeitong.openapi.plugin.fxiaoke.sdk.controller;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.FxkResponseUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeJobConfigDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkProjectService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CompletableFuture;

/**
 * @Description 项目同步
 * @Author duhui
 * @Date 2021/7/12
 **/

@Controller
@Slf4j
@RequestMapping("/fxiaoke/sync")
public class FxkObjectController {

    @Autowired
    IFxkProjectService iFxkProjectService;

    @RequestMapping("/project")
    @ResponseBody
    public Object syncOrganization(@RequestParam("jobConfig") String jobConfig) {
        FxiaokeJobConfigDTO fxiaokeJobConfigDTO = JsonUtils.toObj(jobConfig, FxiaokeJobConfigDTO.class);
        if (ObjectUtils.isEmpty(fxiaokeJobConfigDTO)) {
            return FxkResponseUtils.error(500, "请检查配置文件");
        }
        CompletableFuture.supplyAsync(() -> iFxkProjectService.syncProject(fxiaokeJobConfigDTO)).exceptionally(e -> {
            log.warn("纷享销客项目同步异常:", e);
            return "false";
        });
        return FxkResponseUtils.success(Maps.newHashMap());
    }

}
