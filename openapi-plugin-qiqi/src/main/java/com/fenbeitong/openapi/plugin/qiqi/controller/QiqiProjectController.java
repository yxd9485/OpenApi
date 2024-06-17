package com.fenbeitong.openapi.plugin.qiqi.controller;

import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseUtils;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.qiqi.service.project.IQiqiProjectService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName QiqiProjectController
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
@RestController
@RequestMapping("/qiqi/project")
@Api(value = "企企项目同步", tags = "企企项目同步")
public class QiqiProjectController {
    @Autowired
    IQiqiProjectService projectService;

    @RequestMapping("/syncProject")
    @ApiOperation(value = "项目全量同步", notes = "项目全量同步")
    QiqiResultEntity syncProject(@RequestParam(value = "companyId", required = true) String companyId) throws Exception {
        projectService.syncQiqiProject(companyId);
        return QiqiResponseUtils.success(Maps.newHashMap());
    }
}
