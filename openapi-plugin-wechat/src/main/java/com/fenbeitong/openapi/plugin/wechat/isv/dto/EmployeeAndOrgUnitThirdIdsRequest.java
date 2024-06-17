package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by lizhen on 2020/4/13.
 */
@Data
public class EmployeeAndOrgUnitThirdIdsRequest {

    @NotNull(message = "公司id[company_id]不可为空")
    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("org_unit_list")
    private List<String> orgUnitList;

    @JsonProperty("employee_list")
    private List<String> employeeList;

}
