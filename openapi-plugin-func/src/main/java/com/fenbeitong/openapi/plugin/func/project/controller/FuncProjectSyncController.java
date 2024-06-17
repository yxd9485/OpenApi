package com.fenbeitong.openapi.plugin.func.project.controller;

import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.project.dto.ApiRequestProject;
import com.fenbeitong.openapi.plugin.func.project.service.FuncProjectSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * <p>Title: FuncProjectSyncController</p>
 * <p>Description: 项目同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-11 14:05
 */
@Slf4j
@RestController
@RequestMapping("/func/third/project")
public class FuncProjectSyncController {

    @Autowired
    FuncProjectSyncService funcProjectSyncService;


    /**
     * 全量同步
     */
    @FuncAuthAnnotation
    @RequestMapping("/allSync")
    public Object addThirdProject(@Valid ApiRequestProject apiRequestProject) {
        CompletableFuture.supplyAsync(() -> funcProjectSyncService.allProjectSync(apiRequestProject.getData())).exceptionally(e -> {
            log.warn("{}", e);
            return "false";
        });
        return FuncResponseUtils.success("success");
    }


    /**
     * 增量新增更新接口
     */
    @FuncAuthAnnotation
    @RequestMapping("/partSync")
    public Object partSync(@Valid ApiRequestProject apiRequestProject) {
        CompletableFuture.supplyAsync(() -> funcProjectSyncService.partProjectSync(apiRequestProject.getData())).exceptionally(e -> {
            log.warn("{}", e);
            return "false";
        });
        return FuncResponseUtils.success("success");
    }


    /**
     * 增量设置失效接口
     */
    @FuncAuthAnnotation
    @RequestMapping("/stopProject")
    public Object stopProject(@Valid ApiRequestProject apiRequestProject) {
        CompletableFuture.supplyAsync(() -> funcProjectSyncService.updateStatus(apiRequestProject.getData())).exceptionally(e -> {
            log.warn("{}", e);
            return "false";
        });
        return FuncResponseUtils.success("success");
    }

}
