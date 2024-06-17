package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RemiCostResDTO
 * @Description 报销单费用信息
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/20 上午8:32
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemiCostResDTO {
    /**
     * 费用类别编码
     */
    @JsonProperty("cost_category_custom_code")
    private String costCategoryCustomCode;
    @JsonProperty("cost_category_code")
    private String costCategoryCode;
    @JsonProperty("cost_category_name")
    private String costCategoryName;
    @JsonProperty("form_type")
    private Integer formType;
    @JsonProperty("cost_reason")
    private String costReason;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
//    @JsonProperty("appendix")
//    private List<String> appendix;
    @JsonProperty("cost_attribution_group")
    private List<CostAttributionGroupDTO> costAttributionGroup;
    @JsonProperty("reimb_invoice")
    private List<RemiInvoiceResDTO> reimbInvoice;
    @JsonProperty("cost_custom_fields")
    private List<KVEntity> costCustomFields;

    @JsonProperty("tax_amount")
    @ApiModelProperty(value = "费用税额")
    private String taxAmount;

    @JsonProperty("tax_rate")
    @ApiModelProperty(value = "费用税率")
    private String taxRate;
}
