package com.fenbeitong.openapi.plugin.customize.dasheng.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by huangsiyuan on 2020/10/19.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_ebs_bill_detail")
public class OpenEbsBillDetail {

    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * EBS法人
     */
    @Column(name = "COA_COM")
    private String coaCom;

    /**
     * COA_BU
     */
    @Column(name = "COA_BU")
    private String coaBu;

    /**
     * 成本中心
     */
    @Column(name = "COA_CC")
    private String coaCc;

    /**
     * 会计科目
     */
    @Column(name = "COA_ACC")
    private String coaAcc;

    /**
     * COA_IC
     */
    @Column(name = "COA_IC")
    private String coaIc;

    /**
     * COA_EC
     */
    @Column(name = "COA_EC")
    private String coaEc;

    /**
     * COA_RE
     */
    @Column(name = "COA_RE")
    private String coaRe;

    /**
     * COA_RESERVE1
     */
    @Column(name = "COA_RESERVE1")
    private String coaReserve1;

    /**
     * COA_RESERVE2
     */
    @Column(name = "COA_RESERVE2")
    private String coaReserve2;

    /**
     * COA_RESERVE3
     */
    @Column(name = "COA_RESERVE3")
    private String coaReserve3;

    /**
     * 借项
     */
    @Column(name = "DEBIT")
    private BigDecimal debit;

    /**
     * 贷项
     */
    @Column(name = "CREDIT")
    private BigDecimal credit;

    /**
     * 行说明
     */
    @Column(name = "DESP")
    private String desp;

    /**
     * 账单编号
     */
    @Column(name = "BILL_NO")
    private String billNo;

    /**
     * 账单id
     */
    @Column(name = "BILL_ID")
    private String billId;

    /**
     * 账单详情id
     */
    @Column(name = "BILL_DETAIL_ID")
    private String billDetailId;

}
