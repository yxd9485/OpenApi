package com.fenbeitong.openapi.plugin.customize.wantai.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.NCCSupplierSyncReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.service.WanTaiSupplierService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商同步
 *
 * @author zhangjindong
 * @date 2022/9/21 8:21 PM
 */
@RestController
@RequestMapping("/customize/wantai/supplier")
public class NccSupplierController {

    @Autowired
    private WanTaiSupplierService wanTaiSupplierService;

    @Async
    @PostMapping ("/sync")
    public Object push(@RequestBody NCCSupplierSyncReqDTO req) {
        wanTaiSupplierService.syncNccSupplier(req);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
