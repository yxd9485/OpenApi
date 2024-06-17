package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-09-25 15:53:4
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvGetOrderResponse {

    private Integer errcode;

    private String errmsg;

    @JsonProperty("buyer_corpid")
    private String buyerCorpid;

    @JsonProperty("buyer_userid")
    private String buyerUserid;

    @JsonProperty("order_status")
    private Integer orderStatus;

    @JsonProperty("order_time")
    private Long orderTime;

    @JsonProperty("pay_time")
    private Long payTime;


}