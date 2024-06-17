package com.fenbeitong.openapi.plugin.func.mileage.service;


import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.support.mileage.service.AllowanceMileageService;
import com.fenbeitong.openapi.plugin.support.mileage.service.dto.AllowanceMileageReqDTO;
import com.fenbeitong.openapi.plugin.support.mileage.service.dto.AllowanceMileageRespDTO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 里程补贴
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FuncAllowanceMileageService {

    @Autowired
    private AllowanceMileageService allowanceMileageService;

    public AllowanceMileageRespDTO getAllowanceMileageByPage(String companyId,
        AllowanceMileageReqDTO allowanceMileageReqDTO) {
        return allowanceMileageService.getAllowanceMileageByPage(companyId, allowanceMileageReqDTO);
    }
}
