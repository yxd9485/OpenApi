package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveChangeReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;

/**
 * @author lizhen
 */
public interface IEcologyTripApplyService {

    CreateApplyRespDTO createTripApprove(String token, TripApproveCreateReqDTO req) throws Exception;

    boolean cancelTripApprove(String token, TripApproveChangeReqDTO req) throws Exception;

}
