package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lizhen
 * @date 2020/6/8
 */
@Data
public class FeiShuIsvSendMessageReqDTO {

    private String email;

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("msg_type")
    private String msgType;

    private MsgContent content;

    @Data
    public static class PostContent {

        private String tag;

        @JsonProperty("un_escape")
        private boolean unEscape;

        private String text;

        private String href;

    }

    @Data
    public static class ZhCn {

        private String title;

        private List<List<PostContent>> content;

    }

    @Data
    public static class Post {

        @JsonProperty("zh_cn")
        private ZhCn zhCn;

    }

    @Data
    public static class MsgContent {

        private Post post;

    }
}