package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName EmployeeInfoDTO
 * @Description 人员信息
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/20 上午8:25
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfoDTO {
    /**
     * 1:提单人 2:报销人
     */
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("employee_no")
    private String employeeNo;
    // 三方id
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;
    @JsonProperty("employee_phone")
    private String employeePhone;
    @JsonProperty("employee_department_id")
    private String employeeDepartmentId;
    @JsonProperty("employee_department_name")
    private String employeeDepartmentName;
    @JsonProperty("custom_fields")
    private List<KVEntity> customFields;
    @JsonProperty("third_department_id")
    private String thirdDepartmentId;
}
