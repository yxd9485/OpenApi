package com.fenbeitong.openapi.plugin.dingtalk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 钉钉jsapi签名
 *
 * @author lizhen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DingtalkJsapiSignRespDTO {


    @JsonProperty("corp_id")
    private String corpId;

    @JsonProperty("agent_id")
    private Long agentId;

    @JsonProperty("time_stamp")
    private Long timeStamp;

    @JsonProperty("nonce_str")
    private String nonceStr;

    private String signature;

    private String url;
}
