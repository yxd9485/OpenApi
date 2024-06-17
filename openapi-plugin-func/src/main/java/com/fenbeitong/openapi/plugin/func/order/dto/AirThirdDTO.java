package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/9/16 20:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirThirdDTO {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("unit_id")
    private String unitId;

    @JsonProperty("cost_dept_id")
    private String costDeptId;

    @JsonProperty("cost_project_id")
    private String costProjectId;

    @JsonProperty("apply_id")
    private String applyId;

    @JsonProperty("during_apply_id")
    private String duringApplyId;

    @JsonProperty("passenger_user_id")
    private String passengerUserId;

    @JsonProperty("passenger_unit_id")
    private String passengerUnitId;

}
