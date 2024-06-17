package com.fenbeitong.openapi.plugin.func.deprecated.dto.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/7 11:56
 * @since 2.0
 */
@Data
public class OpenEmployeeIDetailRespDTO {

    private String companyId;

    @JsonProperty("employee_id")
    private String employeeId;

    private Integer type;

    private Integer userType;



}
