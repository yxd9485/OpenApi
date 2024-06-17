package com.fenbeitong.openapi.plugin.customize.huizhuan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 回传规则
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HuiZhuanCallbackRuleDTO {

    @JsonProperty("prefectural_level_city")
    private Boolean prefecturalLevelCity;

    /**
     * 使用人在以下部门列表
     */
    @JsonProperty("passenger_in_department")
    private List<String> passengerInDepartment;



}
