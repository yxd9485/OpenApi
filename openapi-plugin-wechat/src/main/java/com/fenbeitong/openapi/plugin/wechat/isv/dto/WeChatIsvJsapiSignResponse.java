package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取jsapi签名
 * Created by lizhen on 2020/3/27
 */
@Data
public class WeChatIsvJsapiSignResponse {

    @JsonProperty("corp_id")
    private String corpId;

    @JsonProperty("agent_id")
    private Integer agentId;

    private String timestamp;

    @JsonProperty("nonce_str")
    private String nonceStr;

    private String signature;

}
