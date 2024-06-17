package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvCarApprovalService extends AbstractCarApplyService {

    @Override
    public CreateApplyRespDTO createCarApprove(String token, CarApproveCreateReqDTO req) {
        return super.createCarApprove(token, req);
    }

}
