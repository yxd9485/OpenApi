package com.fenbeitong.openapi.plugin.customize.yidiantianxia;


import org.apache.dubbo.config.annotation.DubboReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyTripInfoDTO;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.func.company.service.ICompanyBillExtListener;
import com.fenbeitong.openapi.plugin.support.apply.dto.CompanyApplyDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author duhui
 * @Date 2021/8/5
 **/
@Service
@Slf4j
public class YiDianBillExtListener implements ICompanyBillExtListener {

    @Autowired
    private OpenApplyServiceImpl openApplyService;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    private static final String YIDIANTIANXIA_EMPLOYEE = "yidiantianxia_employee:{0}";

    @Override
    public void setBillExt(String companyId, Map<String, Object> srcData, Map<String, Object> resultData, FuncBillExtInfoTransformDTO transformDto) {
        log.info("易点天下导出账单处理前信息,resultData:{},transformDto:{}", JsonUtils.toJson(resultData), JsonUtils.toJson(transformDto));
        if (!ObjectUtils.isEmpty(transformDto)) {
            if (!ObjectUtils.isEmpty(resultData.get("passengerDeptId"))) {
                resultData.put("bookerDeptId", resultData.get("passengerDeptId"));
            }

            // 审批单号
            String applyId = transformDto.getApplyId();
            String orderApplyId = transformDto.getOrderApplyId();
            String type = transformDto.getType();
            // 使用人ID
            EmployeeContract employeeContract = getEmployee(companyId, StringUtils.isBlank(transformDto.getUserId()) ? transformDto.getEmployeeId() : transformDto.getUserId());
            if (!ObjectUtils.isEmpty(employeeContract)) {
                resultData.put("employeeMail", employeeContract.getEmail());
                List<Map<String, String>> listMap = JsonUtils.toObj(employeeContract.getExpand(), new TypeReference<List<Map<String, String>>>() {
                });
                if (!ObjectUtils.isEmpty(listMap) && listMap.size() > 0) {
                    resultData.put("employeeCode", listMap.get(0).get("code1"));
                }
            }
            if (!StringUtils.isBlank(applyId)) {
                packageApprove(applyId, companyId, resultData, type);
            }
            if (!StringUtils.isBlank(orderApplyId)) {
                packageOrderApprove(orderApplyId, companyId, resultData, type);
            }
        }
    }

    private void packageApprove(String applyId, String companyId, Map<String, Object> resultData, String type) {
        String token = userCenterService.getUcSuperAdminToken(companyId);
        try {
            Map<String, Object> companyApproveDetail = openApplyService.getCompanyApproveDetail(token, CompanyApplyDetailReqDTO.builder().applyId(applyId).build());
            ApplyOrderDetailDTO applyOrderDetailDto = JsonUtils.toObj(JsonUtils.toJson(companyApproveDetail), ApplyOrderDetailDTO.class);
            if (!ObjectUtils.isEmpty(applyOrderDetailDto)) {
                // 三方审批单号
                if (ObjectUtils.isEmpty(resultData.get("thirdApplyId"))) {
                    resultData.put("thirdApplyId", applyOrderDetailDto.getApply().getThird_id());
                }
                // 申请人ID
                String applyEmployee = applyOrderDetailDto.getApply().getEmployee_id();
                EmployeeContract ApplyEmployeeContract = getEmployee(companyId, applyEmployee);
                if (ObjectUtils.isEmpty(resultData.get("applyEmployeeMail"))) {
                    if (!ObjectUtils.isEmpty(ApplyEmployeeContract)) {
                        resultData.put("applyEmployeeMail", ApplyEmployeeContract.getEmail());
                    }
                }
                Map<String, ApplyTripInfoDTO> trapMap = applyOrderDetailDto.getTrip_list().stream().collect(Collectors.toMap(t -> t.getType().toString(), Function.identity(), (o, n) -> n));

                if (trapMap.containsKey(type)) {
                    if (ObjectUtils.isEmpty(resultData.get("applyStartTime"))) {
                        resultData.put("applyStartTime", trapMap.get(type).getStart_time());
                    }
                    if (ObjectUtils.isEmpty(resultData.get("applyEndTime"))) {
                        resultData.put("applyEndTime", trapMap.get(type).getEnd_time());
                    }
                }
                // 申请事由
                if (ObjectUtils.isEmpty(resultData.get("applyReason"))) {
                    resultData.put("applyReason", applyOrderDetailDto.getApply().getApply_reason());
                }
            }
        } catch (Exception e) {
            log.warn("易点天下定制账单获取审批失败", e);
        }
    }

    private void packageOrderApprove(String applyId, String companyId, Map<String, Object> resultData, String type) {
        String token = userCenterService.getUcSuperAdminToken(companyId);
        try {
            Map<String, Object> companyApproveDetail = openApplyService.getCompanyApproveDetail(token, CompanyApplyDetailReqDTO.builder().applyId(applyId).build());
            ApplyOrderDetailDTO applyOrderDetailDto = JsonUtils.toObj(JsonUtils.toJson(companyApproveDetail), ApplyOrderDetailDTO.class);
            if (!ObjectUtils.isEmpty(applyOrderDetailDto)) {
                // 申请人ID
                String applyEmployee = applyOrderDetailDto.getApply().getEmployee_id();
                EmployeeContract ApplyEmployeeContract = getEmployee(companyId, applyEmployee);
                if (ObjectUtils.isEmpty(resultData.get("applyEmployeeMail"))) {
                    if (!ObjectUtils.isEmpty(ApplyEmployeeContract)) {
                        resultData.put("applyEmployeeMail", ApplyEmployeeContract.getEmail());
                    }
                }
                // 申请事由
                if (ObjectUtils.isEmpty(resultData.get("applyReason"))) {
                    resultData.put("applyReason", applyOrderDetailDto.getApply().getApply_reason());
                }
            }
        } catch (Exception e) {
            log.warn("易点天下定制账单获取审批失败", e);
        }
    }

    private EmployeeContract getEmployee(String companyId, String employeeId) {
        final String beisenTokenKey = MessageFormat.format(YIDIANTIANXIA_EMPLOYEE, companyId + employeeId);
        String employeeStr = (String) redisTemplate.opsForValue().get(beisenTokenKey);
        if (!StringUtils.isBlank(employeeStr)) {
            return JsonUtils.toObj(employeeStr, EmployeeContract.class);
        } else {
            EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(employeeId, companyId);
            if (!ObjectUtils.isEmpty(employeeContract)) {
                redisTemplate.opsForValue().set(beisenTokenKey, JsonUtils.toJson(employeeContract), 1, TimeUnit.HOURS);
                return employeeContract;
            }
        }
        return null;
    }

}
