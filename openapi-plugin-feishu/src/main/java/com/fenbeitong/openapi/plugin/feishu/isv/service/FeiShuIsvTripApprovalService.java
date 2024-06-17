package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveChangeReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTripApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvTripApprovalService extends AbstractTripApplyService {

    @Override
    public CreateApplyRespDTO createTripApprove(String token, TripApproveCreateReqDTO req) throws Exception {
        return super.createTripApprove(token, req);
    }


    @Override
    public boolean cancelTripApprove(String token, TripApproveChangeReqDTO req) throws Exception {
        return super.cancelTripApprove(token, req);
    }


}
