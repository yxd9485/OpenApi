package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 消息推送
 * Created by lizhen on 2020/3/28.
 */
@Data
public class WeChatIsvSendMessageRequest {

    private String touser;

    private String toparty;

    private String totag;

    @JsonProperty("msgtype")
    private String msgType;

    @JsonProperty("agentid")
    private Integer agentId;

    @JsonProperty("textcard")
    private Textcard textCard;

    @JsonProperty("enable_id_trans")
    private Integer enableIdTrans;

    @JsonProperty("enable_duplicate_check")
    private Integer enableDuplicateCheck;

    private Text text;

    @Data
    public static class Textcard {

        private String title;

        private String description;

        private String url;

        private String btntxt;
    }

    @Data
    public static class Text {
        private String content;
    }
}
