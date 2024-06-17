package com.fenbeitong.openapi.plugin.definition.service.auto;

import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoOrgEmpDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportBindEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeBindInfo;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

@ServiceAspect
@Service
public class AutoEmployeeService extends AbstractEmployeeService {

public void autoBindEmployee(String company, CorpAutoOrgEmpDTO corpAutoOrgEmpDTO){
    SupportEmployeeBindInfo build = SupportEmployeeBindInfo.builder()
            .phone(corpAutoOrgEmpDTO.getMobilePhone())
            .thirdEmployeeId(corpAutoOrgEmpDTO.getThirdEmpId())
            .build();
    List<SupportEmployeeBindInfo> list = Lists.newArrayList();
    list.add(build);
    SupportBindEmployeeReqDTO build1 = SupportBindEmployeeReqDTO.builder()
            .companyId(corpAutoOrgEmpDTO.getCompanyId())
            .bindList(list)
            .build();
    List<SupportBindEmployeeReqDTO> list1 = Lists.newArrayList();
    list1.add(build1);
    sync2Finhub(null,null,list1);
}

}
