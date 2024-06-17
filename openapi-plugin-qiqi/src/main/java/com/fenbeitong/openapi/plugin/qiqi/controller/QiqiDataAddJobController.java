package com.fenbeitong.openapi.plugin.qiqi.controller;

import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseUtils;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.qiqi.service.addjob.IQiqiDataAddJobService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/qiqi/data/job")
public class QiqiDataAddJobController {

    @Autowired
    IQiqiDataAddJobService qiqiDataAddJobService;

    @RequestMapping("/execute")
    public QiqiResultEntity executeTask(@RequestParam(value = "companyId", required = true) String companyId) throws Exception {
        qiqiDataAddJobService.syncAddData(companyId);
        return QiqiResponseUtils.success(Maps.newHashMap());
    }
}
