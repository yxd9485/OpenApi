package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * create on 2020-09-25 17:1:55
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvOpenPayResponse {

    private Integer errcode;

    private String errmsg;

    private String token;

    private BigDecimal amount;


}