package com.fenbeitong.openapi.plugin.func.deprecated.dto.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/13 13:43
 * @since 2.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryThirdEmployeeRespDTO {

    @JsonProperty("employee_id")
    private String employeeId;

    private String token;
}
