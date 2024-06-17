package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;


/**
 * Created by hanshuqi on 2020/05/12.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonExtInfoDTO {
    /**
     * 公司ID
     */
    @JsonProperty("company_id")
    private String companyId;

    @Column(name = "target_colum")
    private String targetColum;
    /**
     * 属性取值字段，根据指定字段获取值，可以配置不同字段获取不同值
     */
    @JsonProperty("map_key")
    private String mapKey;
    /**
     * 分贝通人员权限类型
     */
    @JsonProperty("map_value")
    private String mapValue;
    /**
     * 分贝人员权限类型
     */
    @JsonProperty("role_type")
    private Integer roleType;

    /**
     * 操作类型
     */
    @JsonProperty("type")
    private Integer type;
    /**
     * 状态属性：0:可用，1:不可用
     */
    @JsonProperty("state")
    private String state;
    /**
     * 脚本属性
     */
    @JsonProperty("scrip")
    private String scrip;
    /**
     * 扩展字段
     */
    @JsonProperty("ext_info")
    private String extInfo;
}
