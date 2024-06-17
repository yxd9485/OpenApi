package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-09-22 16:12:34
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvUploadFileResponse {

    private Integer errcode;

    private String errmsg;

    private String type;

    @JsonProperty("media_id")
    private String mediaId;

    @JsonProperty("created_at")
    private String createdAt;


}