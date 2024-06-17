package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import javax.validation.constraints.NotBlank;

/**
 * Created by hanshuqi on 2020/04/07.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaMsgSendReqDTO {
    /**
     * 企业ID
     */
    @NotBlank(message = "企业ID[corp_id]不可为空")
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 发送消息公众号
     */
    @NotBlank(message = "发送消息公众号[agent_id]不可为空")
    @JsonProperty("agent_id")
    private String agentId;
    /**
     * 公众号秘钥
     */
    @NotBlank(message = "公众号秘钥[agent_secret]不可为空")
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
