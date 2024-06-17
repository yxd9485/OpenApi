package com.fenbeitong.openapi.plugin.func.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 供应商信息
 *
 * @author ctl
 * @date 2022/4/25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FuncSupplierResDTO implements Serializable {

    /**
     * 供应商id
     */
    @JsonProperty("id")
    private Integer id;
    /**
     * 供应商三方id
     */
    @JsonProperty("third_id")
    private String thirdId;
    /**
     * 供应商编码
     */
    @JsonProperty("code")
    private String code;
    /**
     * 供应商名称
     */
    @JsonProperty("name")
    private String name;
    /**
     * 收款账号
     */
    @JsonProperty("bank_account")
    private String bankAccount;
    /**
     * 收款账户名称
     */
    @JsonProperty("bank_account_name")
    private String bankAccountName;
    /**
     * 开户行名称
     */
    @JsonProperty("bank_name")
    private String bankName;
    /**
     * 开户行id
     */
    @JsonProperty("bank_id")
    private String bankId;
    /**
     * 支行名称
     */
    @JsonProperty("subbranch_name")
    private String subbranchName;
    /**
     * 支行id
     */
    @JsonProperty("subbranch_id")
    private String subbranchId;
}
