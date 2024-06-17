package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-09-22 17:0:49
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvGetTranslateResultResponse {

    private Integer errcode;

    private String errmsg;

    private Integer status;

    private String type;

    private Result result;


    @Data
    public static class ContactIdTranslate {
        private String url;
    }

    @Data
    public static class Result {
        @JsonProperty("contact_id_translate")
        private ContactIdTranslate contactIdTranslate;
    }
}