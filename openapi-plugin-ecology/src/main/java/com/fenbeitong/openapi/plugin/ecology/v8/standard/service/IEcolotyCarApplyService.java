package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;

/**
 * Created by lizhen on 2021/1/6.
 */
public interface IEcolotyCarApplyService {
    CreateApplyRespDTO createCarApprove(String token, CarApproveCreateReqDTO carApproveCreateReqDTO);
}
