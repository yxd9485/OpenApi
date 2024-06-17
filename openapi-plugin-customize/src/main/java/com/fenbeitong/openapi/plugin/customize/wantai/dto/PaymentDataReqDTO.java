package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author machao
 * @date 2022/9/27
 */
@Data
public class PaymentDataReqDTO {

    /**
     * 分贝通付款单单号
     */
    private String varFbtno;

    /**
     * 核算年
     */
    private Integer fiscalYear;

    /**
     * 核算月
     */
    private Integer fiscalPeriod;

    /**
     * 录入人三方id
     */
    private String idRecorder;

    /**
     * 供应商代码三方id
     */
    private String idCorr;

    /**
     * 操作时间 2022-09-25 08:09:10
     */
    private String dateOpr;

    /**
     * 付款时间 2022-09-25 08:09:10
     */
    private String datePay;

    /**
     * 结算方式代码
     */
    private String idSetmth;

    /**
     * 付款类别代码
     */
    private String idTrsactype;

    /**
     * 本次付款金额(本币) 分
     */
    private BigDecimal decSamt;

    /**
     * 币种代码
     */
    private String idCurr;

    /**
     * 供应商开户行
     */
    private String varBank;

    /**
     * 供应商银行账号
     */
    private String varBankacct;

    /**
     * 付款申请单单号
     */
    private String oriNo;

}
