package com.fenbeitong.openapi.plugin.func.company.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: CoeCostItemsDTO</p>
 * <p>Description: 费用用途与会计科目</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/24 5:07 PM
 */
@Data
public class CoeCostItemsDTO {

    @JsonProperty("budget_type_id")
    private String budgetTypeId;

    @JsonProperty("target_type_code")
    private String targetTypeCode;

    @JsonProperty("cost_item_id")
    private String costItemId;

    @JsonProperty("cost_item_code")
    private String costItemCode;

    @JsonProperty("cost_type_code")
    private String costTypeCode;

    @JsonProperty("cost_attr_code")
    private String costAttrCode;

    @JsonProperty("cost_item_desc")
    private String costItemDesc;

    @JsonProperty("dept_modify")
    private String deptModify;

    @JsonProperty("gl_account_code")
    private String glAccountCode;

    @JsonProperty("reference_code")
    private String referenceCode;

    private String description;

    @JsonProperty("is_marketing")
    private String isMarketing;

    @JsonProperty("enabled_flag")
    private String enabledFlag;

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
}
