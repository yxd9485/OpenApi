package com.fenbeitong.openapi.plugin.qiqi.controller;

import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseUtils;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.qiqi.service.archive.IQiqiCustomArchiveService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName QiqiCustomArchiveController
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/18
 **/
@RestController
@RequestMapping("/qiqi/customArchive")
@Api(value = "企企自定义档案同步", tags = "企企自定义档案同步")
public class QiqiCustomArchiveController {

    @Autowired
    IQiqiCustomArchiveService qiqiCustomArchiveService;

    @RequestMapping("/syncCustomArchive")
    @ApiOperation(value = "自定义档案全量同步", notes = "自定义档案全量同步")
    QiqiResultEntity syncCustomArchive(@RequestParam(value = "companyId", required = true) String companyId) throws Exception {
        qiqiCustomArchiveService.syncQiqiCustomArchive(companyId);
        return QiqiResponseUtils.success(Maps.newHashMap());
    }
}
