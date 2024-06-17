package com.fenbeitong.openapi.plugin.func.callback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.saasplus.api.model.dto.bill.ThirdReimburseQueryRes;
import lombok.Data;

@Data
public class ReimburseBillDTO extends ThirdReimburseQueryRes {
    @JsonProperty("company_id")
    private String companyId;
}
