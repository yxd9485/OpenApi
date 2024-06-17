package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-09-25 15:49:7
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvGetOrderRequest {

    private String orderid;

    @JsonProperty("nonce_str")
    private String nonceStr;

    private Long ts;

    private String sig;


}