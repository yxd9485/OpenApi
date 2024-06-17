package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: FinanceDeptMappingDto</p>
 * <p>Description: 部门映射信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 4:00 PM
 */
@Data
public class FinanceDeptMappingDto {

    @JsonProperty("fbt_id")
    private String fbtId;

    /**
     * 分贝通部门id
     */
    @JsonProperty("fbt_org_unit_id")
    private String fbtOrgUnitId;

    /**
     * 分贝通部门	名称
     */
    @JsonProperty("fbt_org_unit_name")
    private String fbtOrgUnitName;

    @JsonProperty("fbt_level")
    private Integer fbtLevel;

    @JsonProperty("third_id")
    private String thirdId;

    /**
     * 财务部门id
     */
    @JsonProperty("third_org_unit_id")
    private String thirdOrgUnitId;

    /**
     * 财务系统部门名称
     */
    @JsonProperty("third_org_unit_name")
    private String thirdOrgUnitName;

    /**
     * 财务部门编码
     */
    @JsonProperty("third_org_unit_code")
    private String thirdOrgUnitCode;

    @JsonProperty("third_level")
    private Integer thirdLevel;

    @JsonProperty("org_state")
    private Integer orgState;

    @JsonProperty("org_state_name")
    private String orgStateName;
}
