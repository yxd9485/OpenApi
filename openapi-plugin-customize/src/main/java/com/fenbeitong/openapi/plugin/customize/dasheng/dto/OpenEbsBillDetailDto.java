package com.fenbeitong.openapi.plugin.customize.dasheng.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: OpenEbsBillDetailDto</p>
 * <p>Description: 大生科技定制账单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/1/8 11:50 AM
 */
@Data
public class OpenEbsBillDetailDto {

    @JsonProperty( "id")
    private Long id;

    /**
     * EBS法人
     */
    @JsonProperty("coa_com")
    private String coaCom;

    /**
     * COA_BU
     */
    @JsonProperty("coa_bu")
    private String coaBu;

    /**
     * 成本中心
     */
    @JsonProperty("coa_cc")
    private String coaCc;

    /**
     * 会计科目
     */
    @JsonProperty("coa_acc")
    private String coaAcc;

    /**
     * COA_IC
     */
    @JsonProperty("coa_ic")
    private String coaIc;

    /**
     * COA_EC
     */
    @JsonProperty("coa_ec")
    private String coaEc;

    /**
     * COA_RE
     */
    @JsonProperty("coa_re")
    private String coaRe;

    /**
     * COA_RESERVE1
     */
    @JsonProperty("coa_reserve1")
    private String coaReserve1;

    /**
     * COA_RESERVE2
     */
    @JsonProperty("coa_reserve2")
    private String coaReserve2;

    /**
     * COA_RESERVE3
     */
    @JsonProperty("coa_reserve3")
    private String coaReserve3;

    /**
     * 借项
     */
    @JsonProperty("debit")
    private BigDecimal debit;

    /**
     * 贷项
     */
    @JsonProperty("credit")
    private BigDecimal credit;

    /**
     * 行说明
     */
    @JsonProperty("desp")
    private String desp;
}
