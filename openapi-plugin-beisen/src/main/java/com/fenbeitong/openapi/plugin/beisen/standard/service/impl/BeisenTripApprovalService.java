package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTripApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

@ServiceAspect
@Service
@Slf4j
public class BeisenTripApprovalService extends AbstractTripApplyService {


    public CreateApplyRespDTO createBeisenTripApprove(String token, TripApproveCreateReqDTO tripApproveCreateReqDTO) throws Exception{
        return createTripApprove(token, tripApproveCreateReqDTO);
    }

}
