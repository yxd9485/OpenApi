package com.fenbeitong.openapi.plugin.definition.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by xiaowei on 2020/05/19.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenEmployeePrivDTO {
    /**
     * 公司ID
     */
    @JsonProperty("company_id")
    private String companyId;
    /**
     * 标识不同场景
     */
    @JsonProperty("scene")
    private String scene;
    /**
     * 具体权限规则信息
     */
    @JsonProperty("priv_json_data")
    private String privJsonData;
    /**
     * 角色类型，不同角色对应不同的权限和规则信息
     */
    @JsonProperty("role_type")
    private Long roleType;
}
