package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyTripApplyService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveChangeReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTripApplyService;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 泛微差旅审批
 *
 * @author lizhen
 * @date 2020/12/15
 */
@ServiceAspect
@Service
public class EcologyTripApplyServiceImpl extends AbstractTripApplyService implements IEcologyTripApplyService {

    @Override
    public CreateApplyRespDTO createTripApprove(String token, TripApproveCreateReqDTO req) throws Exception {
        return super.createTripApprove(token, req);
    }


    @Override
    public boolean cancelTripApprove(String token, TripApproveChangeReqDTO req) throws Exception {
        return super.cancelTripApprove(token, req);
    }


}
