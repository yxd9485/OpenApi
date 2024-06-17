package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-09-17 22:15:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatIsvGetRegisterCodeRequest {

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("corp_name")
    private String corpName;

    @JsonProperty("admin_name")
    private String adminName;

    @JsonProperty("admin_mobile")
    private String adminMobile;

    private String state;

    @JsonProperty("follow_user")
    private String followUser;


}