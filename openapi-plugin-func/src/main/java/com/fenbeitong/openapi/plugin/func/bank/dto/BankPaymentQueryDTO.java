package com.fenbeitong.openapi.plugin.func.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ctl
 * @date 2021/11/10
 */
@Data
public class BankPaymentQueryDTO implements Serializable {

    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("payment_id")
    private String paymentId;

}
