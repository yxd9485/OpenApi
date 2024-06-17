package com.fenbeitong.openapi.plugin.definition.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * Created by xiaowei on 2020/05/19.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttrSpecReqDTO {
    /**
     * 
     */
    @JsonProperty("attr_cd")
    private String attrCd;
    /**
     * 
     */
    @JsonProperty("value")
    private String value;
    /**
     * 
     */
    @JsonProperty("value_name")
    private String valueName;
    /**
     * 
     */
    @JsonProperty("spec_type")
    private String specType;
    /**
     * 
     */
    @JsonProperty("list_flag")
    private String listFlag;
    /**
     * 
     */
    @JsonProperty("default_flag")
    private String defaultFlag;
    /**
     * 
     */
    @JsonProperty("remark")
    private String remark;
    /**
     * 
     */
    @JsonProperty("attr_value_name")
    private String attrValueName;
}
