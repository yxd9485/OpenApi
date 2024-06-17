package com.fenbeitong.openapi.plugin.wechat.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatApprovalTemplateDetailRespDTO {
    private int errcode;
    private String errmsg;
    @JsonProperty("template_names")
    private List<Text> templateName;
    @JsonProperty("template_content")
    private TemplateContent templateContent;

    @Data
    public static class TemplateContent {
        @JsonProperty("controls")
        private List<Control> controls;

    }

    @Data
    public static class Control {
        @JsonProperty("property")
        private Property property;
    }

    @Data
    public static class Property {
        @JsonProperty("control")
        private String control;
        @JsonProperty("id")
        private String id;
        @JsonProperty("title")
        private List<Text> title;
        @JsonProperty("placeholder")
        private List<Text> placeholder;
        @JsonProperty("require")
        private int require;
        @JsonProperty("un_print")
        private int unPrint;
    }

    @Data
    public static class Text {
        @JsonProperty("text")
        private String text;
        @JsonProperty("lang")
        private String lang;
    }


}
