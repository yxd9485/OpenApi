package com.fenbeitong.openapi.plugin.feishu.common.service.apply;

import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveChangeReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTripApplyService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@ServiceAspect
@Service
@Slf4j
public class FeiShuTripApprovalService extends AbstractTripApplyService {


    public CreateApplyRespDTO createFeiShuTripApprove(String token, TripApproveCreateReqDTO tripApproveCreateReqDTO) throws Exception {
        CreateApplyRespDTO tripApprove = createTripApprove(token, tripApproveCreateReqDTO);
        return tripApprove;
    }

    /**
     * 取消审批单
     *
     * @param token
     * @param reqDTO
     * @return
     */
    public boolean cancelFeiShuTripApprove(String token, TripApproveChangeReqDTO reqDTO) throws Exception {
        boolean tripApprove = cancelTripApprove(token, reqDTO);
        return tripApprove;
    }

}
