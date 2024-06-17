package com.fenbeitong.openapi.plugin.customize.qiqi.controller;

import com.fenbeitong.openapi.plugin.customize.qiqi.common.QiqiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.qiqi.common.QiqiResultEntity;
import com.fenbeitong.openapi.plugin.customize.qiqi.service.IOpenBudgetCostComparisonService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName QiqiBudgetCostComparisonController
 * @Description
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/8
 **/
@RestController
@RequestMapping("/customize/qiqi")
@Slf4j
public class QiqiBudgetCostComparisonController {

    @Autowired
    IOpenBudgetCostComparisonService budgetCostComparisonService;

    @RequestMapping("/syncArchiveAndCost")
    public QiqiResultEntity syncArchiveAndCost(@RequestParam(value = "companyId", required = true) String companyId) throws Exception {
        budgetCostComparisonService.syncAllArchiveAndCost(companyId);
        return QiqiResponseUtils.success(Maps.newHashMap());
    }
}
