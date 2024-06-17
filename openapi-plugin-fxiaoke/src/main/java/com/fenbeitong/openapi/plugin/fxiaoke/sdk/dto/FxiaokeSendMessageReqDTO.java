package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * create on 2020-12-22 15:28:22
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeSendMessageReqDTO {

    private String corpAccessToken;

    private String corpId;

    private List<String> toUser;

    private String msgType;

    private Composite composite;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Head {

        private String title;

    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class First {

        private String content;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Form {

        private String label;

        private String value;

    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Remark {

        private String content;

    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Link {

        private String title;

        private String url;

    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Composite {

        private Head head;

        private First first;

        private List<Form> form;

        private Remark remark;

        private Link link;

    }
}