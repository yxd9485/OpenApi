package com.fenbeitong.openapi.plugin.definition.dto.plugin.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 企业插件配置添加消息接收配置参数
 * Created by log.chang on 2019/12/25.
 */
@Data
public class CreateMsgRecipientDefinitionReqDTO {

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
