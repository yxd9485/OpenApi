package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl;

import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcolotyCarApplyService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * Created by lizhen on 2021/1/6.
 */
@ServiceAspect
@Service
public class EcolotyCarApplyServiceImpl extends AbstractCarApplyService implements IEcolotyCarApplyService {

    @Override
    public CreateApplyRespDTO createCarApprove(String token, CarApproveCreateReqDTO carApproveCreateReqDTO) {
        return super.createCarApprove(token, carApproveCreateReqDTO);
    }

}
