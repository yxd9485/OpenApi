package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostAttributionGroupDTO {
    @JsonProperty("record_id")
    private String recordId;
    @JsonProperty("category_name")
    private String categoryName;
    @JsonProperty("cost_attribution_list")
    private List<CostAttributionDTO> costAttributionList;
    //1.部门 2.项目 3.自定义
    @JsonProperty("category")
    private Integer category;
    @JsonProperty("range")
    private Integer range;
}
