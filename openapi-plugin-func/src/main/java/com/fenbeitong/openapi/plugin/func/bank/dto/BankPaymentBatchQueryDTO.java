package com.fenbeitong.openapi.plugin.func.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ctl
 * @date 2021/11/10
 */
@Data
public class BankPaymentBatchQueryDTO implements Serializable {

    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("payment_ids")
    private List<String> paymentIds;
}
