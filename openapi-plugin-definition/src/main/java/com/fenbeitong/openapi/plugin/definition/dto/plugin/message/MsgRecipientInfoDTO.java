package com.fenbeitong.openapi.plugin.definition.dto.plugin.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业插件集成三方消息接收配置信息
 * Created by log.chang on 2019/12/25.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgRecipientInfoDTO {

    /**
     * 三方应用id（钉钉/企业微信）
     */
    @JsonProperty("third_corp_id")
    String thirdCorpId;
    /**
     * 三方应用id
     */
    @JsonProperty("third_agent_id")
    String thirdAgentId;
    /**
     * 三方用户id
     */
    @JsonProperty("third_user_id")
    String thirdUserId;
    @JsonProperty("third_user_name")
    String thirdUserName;

}
