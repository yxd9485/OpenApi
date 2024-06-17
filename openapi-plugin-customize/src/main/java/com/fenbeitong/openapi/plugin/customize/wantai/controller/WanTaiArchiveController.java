package com.fenbeitong.openapi.plugin.customize.wantai.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ArchiveCallbackReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.ArchiveDataJobReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.dto.FilingJobReqDTO;
import com.fenbeitong.openapi.plugin.customize.wantai.service.WanTaiERPArchiveService;
import com.fenbeitong.openapi.plugin.customize.wantai.service.WanTaiNCCArchiveService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lizhen
 */
@RestController
@RequestMapping("/gate/customize/wantai/archive")
public class WanTaiArchiveController {

    @Autowired
    private WanTaiNCCArchiveService nccArchiveService;

    @Autowired
    private WanTaiERPArchiveService erpArchiveService;

    @RequestMapping("/callback")
    public Object push(@RequestBody ArchiveCallbackReqDTO req) {
        nccArchiveService.callback(req.getTaskId());
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/ncc/filingUp")
    public Object filingUp(@RequestParam("jobConfig") String jobConfig) {
        FilingJobReqDTO filingJobReqDTO = JsonUtils.toObj(jobConfig, FilingJobReqDTO.class);
        nccArchiveService.filingUpLock(filingJobReqDTO);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/ncc/archiveData")
    public Object archiveDataNCC(@RequestParam("jobConfig") String jobConfig) {
        ArchiveDataJobReqDTO archiveDataJobReqDTO = JsonUtils.toObj(jobConfig, ArchiveDataJobReqDTO.class);
        nccArchiveService.syncArchiveLock(archiveDataJobReqDTO);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


    @RequestMapping("/erp/filingUp")
    public Object filingUpERP(@RequestParam("jobConfig") String jobConfig) {
        FilingJobReqDTO filingJobReqDTO = JsonUtils.toObj(jobConfig, FilingJobReqDTO.class);
        erpArchiveService.filingUpLock(filingJobReqDTO);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/erp/archiveData")
    public Object archiveDataERP(@RequestParam("jobConfig") String jobConfig) {
        ArchiveDataJobReqDTO archiveDataJobReqDTO = JsonUtils.toObj(jobConfig, ArchiveDataJobReqDTO.class);
        erpArchiveService.syncArchiveLock(archiveDataJobReqDTO);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
