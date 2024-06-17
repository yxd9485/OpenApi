package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeUpdateDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportUpdateEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lizhen
 * @date 2020/9/5
 */
@ServiceAspect
@Service
public class WeChatIsvSyncThirdEmployeeService extends OpenEmployeeServiceImpl {

    @Override
    protected boolean isNeedUpdate(OpenThirdEmployee employee, Map<String, OpenThirdEmployeeDTO> destEmployeeMap) {
        OpenThirdEmployeeDTO destEmployee = destEmployeeMap.get(employee.getThirdEmployeeId());
        boolean update = false;
        if (destEmployee != null && !StringUtils.isBlank(destEmployee.getThirdDepartmentId()) && !ObjectUtils.nullSafeEquals(destEmployee.getThirdDepartmentId(), employee.getThirdDepartmentId())) {
            update = true;
        }
        if (destEmployee != null && destEmployee.getThirdEmployeeRoleTye() != null && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeeRoleTye(), employee.getThirdEmployeeRoleTye())) {
            update = true;
        }
        if (destEmployee != null && !StringUtils.isBlank(destEmployee.getThirdEmployeeIdCard()) && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeeIdCard(), employee.getThirdEmployeeIdCard())) {
            update = true;
        }
        if (destEmployee != null && destEmployee.getStatus() != null && !ObjectUtils.nullSafeEquals(destEmployee.getStatus(), employee.getStatus())) {
            update = true;
        }
        if (destEmployee != null && destEmployee.getThirdEmployeeGender() != null && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeeGender(), employee.getThirdEmployeeGender())) {
            update = true;
        }
        if (destEmployee != null && destEmployee.getThirdEmployeeEmail() != null && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeeEmail(), employee.getThirdEmployeeEmail())) {
            update = true;
        }
        if (destEmployee != null && destEmployee.getExtAttr() != null && isExtAtrrChange(employee.getExtAttr(), destEmployee.getExtAttr())) {
            update = true;
        }
        return update;
    }

    /**
     * update的人员清除名称，名称不更新防止覆盖
     * @param companyId
     * @param supportUpdateEmployeeReqList
     */
    @Override
    public void updateEmployee(String companyId, List<SupportUpdateEmployeeReqDTO> supportUpdateEmployeeReqList) {
        if (!ObjectUtils.isEmpty(supportUpdateEmployeeReqList)) {
            supportUpdateEmployeeReqList.forEach(req -> {
                List<SupportEmployeeUpdateDTO> employeeList = req.getEmployeeList();
                for (SupportEmployeeUpdateDTO supportEmployeeUpdateDTO: employeeList) {
                    supportEmployeeUpdateDTO.setName(null);
                }
                updateUser(req);
                updateOpenThirdEmployee(companyId, req);
            });
        }
    }

}
