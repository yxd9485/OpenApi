package com.fenbeitong.openapi.plugin.feishu.common.service.apply;

import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractCarApplyService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 创建用车审批单
 * @author xiaohai
 * @Date 2022/07/04
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuCarApprovalService extends AbstractCarApplyService {


    public CreateApplyRespDTO createFeiShuCarApprove(String token, CarApproveCreateReqDTO carApproveCreateReqDTO) throws Exception {
        CreateApplyRespDTO carApprove = createCarApprove(token, carApproveCreateReqDTO);
        return carApprove;
    }

}
