package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: VirtualCardDeductionTypeDto</p>
 * <p>Description: 虚拟卡抵扣类型配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 8:32 PM
 */
@Data
public class VirtualCardDeductionTypeDto {

    private String id;

    @JsonProperty("cost_category")
    private String costCategory;

    @JsonProperty("expense_category_id")
    private String expenseCategoryId;

    @JsonProperty("invoice_type")
    private String invoiceType;

    @JsonProperty("invoice_taxes_id")
    private String invoiceTaxesId;

    @JsonProperty("group_id")
    private String groupId;

    private int status;
}
