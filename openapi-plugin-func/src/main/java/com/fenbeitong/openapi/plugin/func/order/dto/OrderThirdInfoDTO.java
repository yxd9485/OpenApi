package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName OrderThirdInfoDTO
 * @Description 对公付款三方信息
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/1/25 下午3:04
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderThirdInfoDTO {

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("unit_id")
    private String unitId;
    @JsonProperty("cost_dept_id")
    private String costDeptId;
    @JsonProperty("cost_project_id")
    private String costProjectId;
}
