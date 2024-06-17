package com.fenbeitong.openapi.plugin.func.apply.service;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CostAttributionDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TakeawayApproveApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.TakeawayApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTakeawayApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FuncTakeawayApplyServiceImpl extends AbstractTakeawayApplyService {
    @Autowired
    FuncEmployeeService funcEmployeeService;
    @Autowired
    CommonApplyServiceImpl commonApplyService;


    public OpenApiResponseDTO<CreateApplyRespDTO> createFbTakeawayApprove(String companyId, String employeeId, String employeeType, TakeawayApproveCreateReqDTO reqDTO) throws BindException {
        //参数非空校验
        ValidatorUtils.validateBySpring(reqDTO);
        String employeeFbToken = funcEmployeeService.getEmployeeFbToken(companyId, employeeId,employeeType);
        if (StringUtils.isBlank(employeeFbToken)) {
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.THIRD_USER_NOT_FOUNT));
        }
        @Valid @NotNull(message = "审批单申请内容[apply]不可为空") TakeawayApproveApply apply = reqDTO.getApply();
        apply.setType(14);
        apply.setCompanyId(companyId);
        apply.setEmployeeId(employeeId);

        return createTakeawayApply(employeeFbToken, reqDTO);
    }
}
