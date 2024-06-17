package com.fenbeitong.openapi.plugin.definition.service.auto;

import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoOrgEmpDTO;
import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.support.organization.dto.SupportBindOrgUnitReqDTO;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

@ServiceAspect
@Service
public class AutoOriganizationService  extends AbstractOrganizationService {

    @Autowired
    AutoEmployeeService autoEmployeeService;


    /**
     * 绑定部门和人员ID
     * @param corpAutoOrgEmpDTO
     */
    public void autoBindDepartmentAndEmployee(CorpAutoOrgEmpDTO corpAutoOrgEmpDTO){
        if(StringUtils.isNotBlank(corpAutoOrgEmpDTO.getMobilePhone())){//人员
            autoEmployeeService.autoBindEmployee(corpAutoOrgEmpDTO.getCompanyId(),corpAutoOrgEmpDTO);

        }else{
            SupportBindOrgUnitReqDTO supportBindOrgUnitReqDTO = SupportBindOrgUnitReqDTO.builder()
                    .companyId(corpAutoOrgEmpDTO.getCompanyId())
                    .orgId(corpAutoOrgEmpDTO.getFbtOrgId())
                    .thirdOrgId(corpAutoOrgEmpDTO.getThirdOrgId())
                    .type(corpAutoOrgEmpDTO.getType())
                    .operatorId(corpAutoOrgEmpDTO.getThirdEmpId())
                    .build();
            autoBindDepartment(corpAutoOrgEmpDTO.getCompanyId(),supportBindOrgUnitReqDTO);
            List<SupportBindOrgUnitReqDTO> list = Lists.newArrayList();
            list.add(supportBindOrgUnitReqDTO);
            sync2FbDepartment(corpAutoOrgEmpDTO.getCompanyId(),null,null,list);
        }
    }


    private void autoBindDepartment(String companyId,SupportBindOrgUnitReqDTO bindOrgUnitReqDTO){
        List<SupportBindOrgUnitReqDTO> list = Lists.newArrayList();
        list.add(bindOrgUnitReqDTO);
        sync2FbDepartment(companyId,null,null,list);
    }



}
