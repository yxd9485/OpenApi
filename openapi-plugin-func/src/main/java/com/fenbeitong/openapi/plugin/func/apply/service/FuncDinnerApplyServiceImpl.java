package com.fenbeitong.openapi.plugin.func.apply.service;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.func.order.dto.MallApplyApproveReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractDinnerApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
public class FuncDinnerApplyServiceImpl extends AbstractDinnerApplyService {
    @Autowired
    FuncEmployeeService funcEmployeeService;
    @Autowired
    CommonApplyServiceImpl commonApplyService;
    @Autowired
    private FuncEmployeeService employeeService;
    @Autowired
    private ThirdCallbackRecordDao recordDao;

    public Object createFbDinnerApprove(String companyId, String employeeId, String employeeType, DinnerApproveCreateReqDTO dinnerApproveCreateReqDTO) throws BindException {
        String employeeFbToken = funcEmployeeService.getEmployeeFbToken(companyId, employeeId, employeeType);
        if (StringUtils.isBlank(employeeFbToken)) {
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.THIRD_USER_NOT_FOUNT));
        }
        DinnerApproveApply dinnerApproveApply  = dinnerApproveCreateReqDTO.getApply();
        dinnerApproveApply.setCompanyId(companyId);
        dinnerApproveApply.setEmployeeId(employeeId);
        dinnerApproveCreateReqDTO.setApply(dinnerApproveApply);
        return createDinnerApply(employeeFbToken, dinnerApproveCreateReqDTO);
    }

    public void notifyApplyAgree(MallApplyApproveReqDTO req) {
        String token = employeeService.getEmployeeFbToken(req.getCompanyId(), req.getEmployeeId(), req.getEmployeeType());
        DinnerApplyAgreeReqDTO build = new DinnerApplyAgreeReqDTO().builder()
                .applyId(req.getApplyId())
                .thirdId(req.getThirdApplyId())
                .build();
        ThirdCallbackRecord record = recordDao.getApplyByApplyId(req.getApplyId(), CallbackType.APPLY_DINNER_REVERSE.getType());
        if (record != null) {
            notifyDinnerApplyAgree(token, build);
        } else {
            throw new OpenApiPluginSupportException(SupportRespCode.FB_APPLY_ID_NOT_EXIST,"根据 applyId 查询订单为空");
        }
    }
}
