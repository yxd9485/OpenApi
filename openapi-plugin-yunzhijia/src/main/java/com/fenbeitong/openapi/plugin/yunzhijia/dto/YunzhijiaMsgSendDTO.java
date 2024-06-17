package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by hanshuqi on 2020/04/07.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaMsgSendDTO {
    /**
     * 企业ID
     */
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 发送消息公众号
     */
    @JsonProperty("agent_id")
    private String agentId;
    /**
     * 公众号秘钥
     */
    @JsonProperty("agent_secret")
    private String agentSecret;
    /**
     * 公众号状态，默认0，0：开启，1：关闭
     */
    @JsonProperty("state")
    private Integer state;
    /**
     * 应用类型，可能会存在多个应用类型，可以用type来进行区分
     */
    @JsonProperty("type")
    private Integer type;
}
