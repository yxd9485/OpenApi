package com.fenbeitong.openapi.plugin.feishu.eia.service;

import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

@ServiceAspect
@Service
@Slf4j
public class FeiShuEiaCarApprovalService extends AbstractCarApplyService {


    public CreateApplyRespDTO createFeiShuCarApprove(String token, CarApproveCreateReqDTO carApproveCreateReqDTO) throws Exception {
        CreateApplyRespDTO carApprove = createCarApprove(token, carApproveCreateReqDTO);
        return carApprove;
    }

}
