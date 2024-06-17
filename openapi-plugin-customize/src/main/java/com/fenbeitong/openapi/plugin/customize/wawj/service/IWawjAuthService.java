package com.fenbeitong.openapi.plugin.customize.wawj.service;

import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjAuthRespDTO;

public interface IWawjAuthService {

    /**
     * 应用授权免登
     * @param code
     * @param companyId
     * @return
     */
    WawjAuthRespDTO auth(String code, String companyId);
}
