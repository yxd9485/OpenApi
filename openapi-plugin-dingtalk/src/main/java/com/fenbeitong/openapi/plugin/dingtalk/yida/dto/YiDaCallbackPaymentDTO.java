package com.fenbeitong.openapi.plugin.dingtalk.yida.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author ctl
 * @date 2022/3/3
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YiDaCallbackPaymentDTO extends YiDaCallbackDTO {

    /**
     * 请款编号
     * 三方申请单id
     */
    private String thirdId;

    /**
     * 请款日期
     * 对公付款日期
     */
    private String paymentTime;

    /**
     * 请款人部门id
     * 费用归属id
     */
    private String thirdDeptId;

    /**
     * 请款人部门名称
     * 费用归属name
     */
    private String thirdDeptName;

    /**
     * 请款人id
     * 三方申请人id
     */
    private String thirdEmployeeId;

    /**
     * 请款人name
     * 三方申请人name
     */
    private String thirdEmployeeName;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 是否涉及新签合同
     */
    private String isNewContract;

    /**
     * 收款户名
     */
    private String bankAccountName;

    /**
     * 收款账号
     */
    private String bankAccountCode;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 支行
     */
    private String subbranchName;

    /**
     * 附件
     */
    private String attachment;

    /**
     * 付款总金额
     * 对公付款总额 单位（元）
     */
    private BigDecimal paymentAmount;

    /**
     * 付款单名称
     */
    private String paymentName;

    /**
     * 付款明细中的 费用归属部门
     */
    private String listDeptStr;

    /**
     * 付款明细中的 客户
     */
    private String listClientStr;

    /**
     * 付款明细中的 付款内容
     */
    private String listContentStr;

    /**
     * 付款明细中的 付款金额
     */
    private String listAmountStr;

    /**
     * 付款明细中的 币种
     * （暂时不用）
     */
    private String listCurrencyStr;

    /**
     * 付款明细中的 付款方式
     * （暂时不用）
     */
    private String listWayStr;

    /**
     * 付款明细中的 关联请购单
     * （暂时不用）
     */
    private String listRequisitionStr;

    /**
     * 付款明细中的 项目名称
     */
    private String listProjectStr;
}
