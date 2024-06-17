package com.fenbeitong.openapi.plugin.func.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BindEmployeeResDTO
 * @Description 绑定人员返回
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/11/18 下午8:30
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BindEmployeeResDTO {

    private String reason;
    private String code;
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;
    @JsonProperty("employee_id")
    private String employeeId;
    private String phone;

}
