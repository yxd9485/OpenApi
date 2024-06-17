package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

@ServiceAspect
@Service
@Slf4j
public class BeisenCarApprovalService extends AbstractCarApplyService {


    public CreateApplyRespDTO createBeisenCarApprove(String token, CarApproveCreateReqDTO carApproveCreateReqDTO) throws Exception {
        return createCarApprove(token, carApproveCreateReqDTO);
    }

}
