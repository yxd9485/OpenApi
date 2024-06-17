package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaPubTokenDTO {

    private String no;
    private String pubId;
    private String pubSecret;
    private String nonce;
    private String time;
    private String pubToken;
}
