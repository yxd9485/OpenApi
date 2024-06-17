package com.fenbeitong.openapi.plugin.customize.wantai.service;

import com.fenbeitong.openapi.plugin.customize.wantai.dto.NCCSupplierSyncReqDTO;

/**
 * @author zhangjindong
 * @date 2022/9/21 8:45 PM
 */


public interface WanTaiSupplierService {
    /**
     * 同步供应商
     *
     * @param nCCSupplierSyncReqDTO 入参
     */
    void syncNccSupplier(NCCSupplierSyncReqDTO nCCSupplierSyncReqDTO);
}
