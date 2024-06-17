package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.seeyon.helper.JacksonHelper;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeeyonOrgNameReq {

    private static final long serialVersionUID = -7775217544105326853L;

    @JsonProperty("orgName")
    private String orgName;

    @JsonProperty("compareDaysGap")
    private Long compareDaysGap;

    /**
     * 三方部门id取值字段名
     */
    private String thirdUnitIdFieldName;

    public static SeeyonOrgNameReq fromJson(String requestJson) {
        return JacksonHelper.readValue(requestJson, SeeyonOrgNameReq.class);
    }
}
