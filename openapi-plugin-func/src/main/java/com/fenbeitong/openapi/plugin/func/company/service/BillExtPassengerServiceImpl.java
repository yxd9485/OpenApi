package com.fenbeitong.openapi.plugin.func.company.service;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.frequent.FrequentInfoContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.fenbeitong.usercenter.api.service.frequent.IFrequentService;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: BillExtPassengerServiceImpl</p>
 * <p>Description: 乘机人乘车人服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/16 5:00 PM
 */
@ServiceAspect
@Service
public class BillExtPassengerServiceImpl {

    @DubboReference(check = false)
    private IFrequentService frequentService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @SuppressWarnings("unchecked")
    public void setPassengerInfo(Map data) {
        String passengerId = (String) data.get("passengerId");
        String userDeptId = (String) data.get("userDeptId");
        String employeeId = null;
        String deptId = null;
        if (!ObjectUtils.isEmpty(passengerId)) {
            FrequentInfoContract frequentInfoContract = frequentService.queryFrequentOrgInfo(passengerId);
            if (frequentInfoContract != null) {
                Boolean employee = frequentInfoContract.getFrequentContact() != null && frequentInfoContract.getFrequentContact().getIsEmployee();
                if (employee) {
                    employeeId = frequentInfoContract.getFrequentContact().getSelectedEmployeeId();
                    if (ObjectUtils.isEmpty(userDeptId)) {
                        deptId = frequentInfoContract.getOrgUnit() == null ? null : frequentInfoContract.getOrgUnit().getId();
                    }
                }
            }
        }
        if (ObjectUtils.isEmpty(employeeId) || ObjectUtils.isEmpty(deptId)) {
            String passengerPhone = (String) data.get("passengerPhone");
            if (!ObjectUtils.isEmpty(passengerPhone)) {
                List<EmployeeContract> employeeContractList = employeeExtService.queryByPhone(passengerPhone);
                employeeId = ObjectUtils.isEmpty(employeeContractList) ? null : employeeContractList.get(0).getEmployee_id();
                if (ObjectUtils.isEmpty(userDeptId)) {
                    deptId = ObjectUtils.isEmpty(employeeContractList) ? null : employeeContractList.get(0).getOrg_id();
                }
            }
        }
        data.put("userId", ObjectUtils.isEmpty(employeeId) ? null : employeeId);
        data.put("userDeptId", ObjectUtils.isEmpty(userDeptId) ? deptId : userDeptId);
    }
}
