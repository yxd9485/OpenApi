package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * create on 2020-09-25 11:1:21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvOpenPayRequest {

    private String orderid;

    private Integer appid;

    @JsonProperty("buyer_corpid")
    private String buyerCorpid;

    @JsonProperty("buyer_userid")
    private String buyerUserid;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_detail")
    private String productDetail;

    @JsonProperty("unit_name")
    private String unitName;

    @JsonProperty("unit_price")
    private Long unitPrice;

    private Integer num;

    @JsonProperty("appointed_channel")
    private Integer appointedChannel;

    @JsonProperty("nonce_str")
    private String nonceStr;

    private Long ts;

    private String sig;


}