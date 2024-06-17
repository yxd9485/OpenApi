package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: FinanceCreateVoucherReqDto</p>
 * <p>Description: 生成凭证参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/29 5:33 PM
 */
@Data
public class FinanceCreateVoucherReqDto {

    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("operator")
    private String operator;

    @JsonProperty("operator_id")
    private String operatorId;

    @JsonProperty("voucher_type")
    private Integer voucherType;

    @JsonProperty("batch_id")
    private String batchId;

    @JsonProperty("src_list")
    private List<Map<String, Object>> srcList;

    @JsonProperty("callback_url")
    private String callBackUrl;
}
