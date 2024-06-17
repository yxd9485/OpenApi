package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiOrgRequest
 *
 * <p>OpenApi 组织机构数据
 *
 * @author Create by dave.hansins on 18:47 2019/3/21
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonApiOrgRequest {

    /**
     * company_id : 07ab0579u252456ya805bd567 org_unit_name : 审计部 third_parent_id : 第三方直属部门ID
     * third_org_id : 第三方机构部门ID operator_id : 57ab054c2528226a805bd523
     */
    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("company_id")
    private String companyId;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("org_unit_name")
    private String orgUnitName;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("third_parent_id")
    private String thirdParentId;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("third_org_id")
    private String thirdOrgId;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("operator_id")
    private String operatorId;
}
