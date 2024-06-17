package com.fenbeitong.openapi.plugin.func.company.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: CoeGlCostCenterRelDTO</p>
 * <p>Description: 部门与成本中心映射关系</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/24 5:03 PM
 */
@Data
public class CoeGlCostCenterRelDTO {

    @JsonProperty("relation_id")
    private String relationId;

    @JsonProperty("ps_set_id")
    private String psSetId;

    @JsonProperty("ps_dept_id")
    private String psDeptId;

    @JsonProperty("ps_dept_desc")
    private String psDeptDesc;

    @JsonProperty("ps_effective_date")
    private String psEffectiveDate;

    @JsonProperty("ebs_cc_code")
    private String ebsCcCode;

    @JsonProperty("ebs_cc_desc")
    private String ebsCcDesc;

    @JsonProperty("parent_dept_code")
    private String parentDeptCode;

    @JsonProperty("enabled_flag")
    private String enabledFlag;

    @JsonProperty("start_date_active")
    private String startDateActive;

    @JsonProperty("end_date_active")
    private String endDateActive;

    @JsonProperty("cc_enabled_flag")
    private String ccEnabledFlag;

    @JsonProperty("enable_start_date")
    private String enableStartDate;

    @JsonProperty("enable_end_date")
    private String enableEndDate;

    @JsonProperty("creation_date")
    private String creationDate;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("last_update_date")
    private String lastUpdateDate;

    @JsonProperty("last_updated_by")
    private String lastUpdatedBy;

    private String attribute1;

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private String attribute5;

    private String attribute6;

    private String attribute7;

    private String attribute8;

    private String attribute9;

    private String attribute10;

    @JsonProperty("interface_date")
    private String interfaceDate;

    @JsonProperty("curr_flag")
    private String currFlag;

    @JsonProperty("cost_attr_code")
    private String costAttrCode;
}
