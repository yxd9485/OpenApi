package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: FinanceVoucherContractDto</p>
 * <p>Description: 报销单数据</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/25 2:58 PM
 */
@Data
public class FinanceVoucherContractDto {

    /**
     * 批次id
     */
    private String batch_id;

    //摘要
    private String summary;

    //申请人名称
    private String username;

    //部门名称
    private String department;

    //部门层级名称
    private String dept_sub_name;

    //审批单编码
    private String apply_code;

    //费用类别id
    private Integer cost_category_id;

    //费用类别名称
    private String cost_category;

    //发票金额
    private BigDecimal total_amount;

    //发票税额
    private BigDecimal tax_amount;

    //价税合计
    private BigDecimal total_price_plus_tax;
}
