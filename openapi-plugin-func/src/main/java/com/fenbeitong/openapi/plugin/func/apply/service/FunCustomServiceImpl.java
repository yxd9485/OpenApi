package com.fenbeitong.openapi.plugin.func.apply.service;

import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.func.order.dto.MallApplyApproveReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.MallApplyAgreeReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.ThirdApplyRequestDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractApplyService;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther zhang.peng
 * @Date 2022/1/31
 */
@ServiceAspect
@Service
@Slf4j
public class FunCustomServiceImpl extends AbstractApplyService {

    @Autowired
    private FuncEmployeeService employeeService;

    @Autowired
    private ThirdCallbackRecordDao recordDao;

    public void notifyApplyAgree(MallApplyApproveReqDTO req) {
        String token = employeeService.getEmployeeFbToken(req.getCompanyId(), req.getEmployeeId(), req.getEmployeeType());
        ThirdApplyRequestDTO build = new ThirdApplyRequestDTO().builder()
            .applyId(req.getApplyId())
            .thirdId(req.getThirdApplyId())
            .build();
        ThirdCallbackRecord record = recordDao.getApplyByApplyId(req.getApplyId(), 5);
        if (record != null) {
            agreeReimburseApply(token, build);
        } else {
            throw new OpenApiPluginSupportException(SupportRespCode.FB_APPLY_ID_NOT_EXIST,"根据 applyId 查询订单为空");
        }
    }
}
