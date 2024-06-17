package com.fenbeitong.openapi.plugin.func.bank.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ctl
 * @date 2021/11/11
 */
@Data
public class IBankPaymentDetailVO implements Serializable {

    /**
     * 付款单单号
     */
    private String paymentId;
    /**
     * 付款单名称
     */
    private String paymentName;
    /**
     * 付款金额(元)
     */
    private BigDecimal totalPrice;
    /**
     * 付款单创建人
     */
    private String userName;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 付款主体
     */
    private String payerName;
    /**
     * 付款主体ID
     */
    private String payerId;
    /**
     * 付款用途
     */
    private String paymentPurpose;
    /**
     * 付款账户名称
     */
    private String bankAccountAcctName;
    /**
     * 供应商
     */
    private String receiverName;
    /**
     * 收款方id
     */
    private Integer receiverId;
    /**
     * 收款方开户行
     */
    private String receiverBank;
    /**
     * 收款方账号
     */
    private String receiverAccount;
    /**
     * 收款方唯一号
     */
    private String unionPayAccount;
    /**
     * 开户名
     */
    private String receiverAccountName;
    /**
     * 合同号
     */
    private String contractCode;
    /**
     * 合同id
     */
    private Integer contractId;
    /**
     * 合同名称
     */
    private String contractName;
    /**
     * 凭证ID
     */
    private Integer proofId;
    /**
     * 凭证名称
     */
    private String proofName;
    /**
     * 付款申请单单号
     */
    private String duringApplyId;
    /**
     * 付款申请人
     */
    private String duringApplyUserName;
    /**
     * 付款账户开户id
     */
    private String companyAccountId;
    /**
     * 申请事由
     */
    private String applyReason;
    /**
     * 申请事由描述
     */
    private String applyReasonDesc;
    /**
     * 费用ID
     */
    private String costId;
}
