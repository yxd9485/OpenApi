package com.fenbeitong.openapi.plugin.func.takeaway.service;

import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.dto.TakeawayAddressDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTakeawayService;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class FuncTakeawayService extends AbstractTakeawayService {
    @Autowired
    FuncEmployeeService funcEmployeeService;

   public  List<TakeawayAddressDTO> getFbTakeawayAddressList(String companyId,String employeeId,String employeeType, String orderType) {
        String employeeFbToken = funcEmployeeService.getEmployeeFbToken(companyId, employeeId,employeeType);
       if (StringUtils.isBlank(employeeFbToken)) {
           throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.THIRD_USER_NOT_FOUNT));
       }
        List<TakeawayAddressDTO> takeawayAddressList = getTakeawayAddressList(employeeFbToken, orderType);
        return takeawayAddressList;
    }

}
