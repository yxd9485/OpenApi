package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 发票信息
 */
@Data
public class InvoiceInfoRequest {

    /**
     * 2:微信内部应用  3：微信三方应用
     */
    @JsonProperty("source_type")
    private Integer sourceType;

    @NotEmpty(message = "选中发票信息[choose_invoice_info]不可为空")
    @JsonProperty("choose_invoice_info")
    private List<ChooseInvoiceInfo> chooseInvoiceInfoList;

    @Data
    public static class ChooseInvoiceInfo {
        @NotBlank(message = "发票id[card_id]不可为空")
        @JsonProperty("card_id")
        private String cardId;

        @NotBlank(message = "加密code[encrypt_code]不可为空")
        @JsonProperty("encrypt_code")
        private String encryptCode;

        @JsonProperty("app_id")
        private String appId;
    }


}
