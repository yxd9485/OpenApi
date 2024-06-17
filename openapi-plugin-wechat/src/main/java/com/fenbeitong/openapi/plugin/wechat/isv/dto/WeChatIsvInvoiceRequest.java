package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * create on 2020-05-26 16:35:2
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvInvoiceRequest {

    @JsonProperty("card_id")
    private String cardId;

    @JsonProperty("encrypt_code")
    private String encryptCode;

    @JsonProperty("reimburse_status")
    private String reimburseStatus;

}