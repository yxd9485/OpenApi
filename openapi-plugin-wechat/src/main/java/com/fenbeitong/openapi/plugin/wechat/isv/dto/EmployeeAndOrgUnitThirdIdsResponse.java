package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by lizhen on 2020/4/13.
 */
@Data
public class EmployeeAndOrgUnitThirdIdsResponse {

    @JsonProperty("org_unit_list")
    private List<CommonIdDTO> orgUnitList;
    
    @JsonProperty("employee_list")
    private List<CommonIdDTO>  employeeList;


}
