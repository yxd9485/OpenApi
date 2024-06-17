package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by hanshuqi on 2020/03/24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaOrgUnitDTO {
    /**
     * 企业ID
     */
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 云之家部门ID
     */
    @JsonProperty("yunzhijia_org_id")
    private String yunzhijiaOrgId;
    /**
     * 云之家父部门ID
     */
    @JsonProperty("yunzhijia_parent_org_id")
    private String yunzhijiaParentOrgId;
    /**
     * 云之家部门名称
     */
    @JsonProperty("yunzhijia_org_name")
    private String yunzhijiaOrgName;
    /**
     * 部门状态 0：可用，1：不可用
     */
    @JsonProperty("state")
    private Long state;
}
