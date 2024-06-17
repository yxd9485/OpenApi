package com.fenbeitong.openapi.plugin.qiqi.controller;

import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseUtils;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.qiqi.service.projectgroup.IQiqiProjectGroupService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName QiqiProjectGroupController
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
@RestController
@RequestMapping("/qiqi/projectGroup")
@Api(value = "企企项目分组同步", tags = "企企项目分组同步")
public class QiqiProjectGroupController {
    @Autowired
    IQiqiProjectGroupService projectGroupService;

    @RequestMapping("/syncProjectGroup")
    @ApiOperation(value = "项目分组全量同步", notes = "项目分组全量同步")
    QiqiResultEntity syncProjectGroup(@RequestParam(value = "companyId", required = true) String companyId) throws Exception {
        projectGroupService.syncQiqiProjectGroup(companyId);
        return QiqiResponseUtils.success(Maps.newHashMap());
    }
}
