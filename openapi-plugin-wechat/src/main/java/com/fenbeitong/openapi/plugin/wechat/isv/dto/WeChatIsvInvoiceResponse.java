package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * create on 2020-05-26 16:36:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvInvoiceResponse {

    private Integer errcode;

    private String errmsg;

    @JsonProperty("card_id")
    private String cardId;

    @JsonProperty("begin_time")
    private Integer beginTime;

    @JsonProperty("end_time")
    private Integer endTime;

    private String openid;

    private String type;

    private String payee;

    private String detail;

    @JsonProperty("user_info")
    private UserInfo userInfo;

    @Data
    public static class UserInfo {

        private Integer fee;

        private String title;

        @JsonProperty("billing_time")
        private Integer billingTime;

        @JsonProperty("billing_no")
        private String billingNo;

        @JsonProperty("billing_code")
        private String billingCode;

        private List<Info> info;

        @JsonProperty("fee_without_tax")
        private Integer feeWithoutTax;

        private Integer tax;

        private String detail;

        @JsonProperty("pdf_url")
        private String pdfUrl;

        @JsonProperty("reimburse_status")
        private String reimburseStatus;

        @JsonProperty("check_code")
        private String checkCode;


    }

    @Data
    public static class Info {

        private String name;

        private Integer num;

        private String unit;

        private Integer fee;

        private Integer price;

    }
}