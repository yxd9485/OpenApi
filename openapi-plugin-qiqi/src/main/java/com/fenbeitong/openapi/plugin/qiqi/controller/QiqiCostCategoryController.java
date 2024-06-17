package com.fenbeitong.openapi.plugin.qiqi.controller;

import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResponseUtils;
import com.fenbeitong.openapi.plugin.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.qiqi.service.cost.IQiqiCostCategoryService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName QiqiCostCategoryController
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
@RestController
@RequestMapping("/qiqi/costCategory")
@Api(value = "企企费用类别同步", tags = "企企费用类别同步")
public class QiqiCostCategoryController {
    @Autowired
    IQiqiCostCategoryService qiqiCostCategoryService;

    @RequestMapping("/syncCostCategory")
    @ApiOperation(value = "费用类别全量同步", notes = "费用类别全量同步")
    QiqiResultEntity syncCostCategory(@RequestParam(value = "companyId", required = true) String companyId) throws Exception {
        qiqiCostCategoryService.syncQiqiCostCategory(companyId);
        return QiqiResponseUtils.success(Maps.newHashMap());
    }
}
