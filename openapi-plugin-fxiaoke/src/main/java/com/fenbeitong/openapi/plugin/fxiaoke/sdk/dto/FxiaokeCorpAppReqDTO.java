package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import javax.validation.constraints.NotBlank;

/**
 * Created by hanshuqi on 2020/07/01.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeCorpAppReqDTO {
    /**
     * 企业ID
     */
    @NotBlank(message = "企业ID[corp_id]不可为空")
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 应用ID
     */
    @NotBlank(message = "应用ID[app_id]不可为空")
    @JsonProperty("app_id")
    private String appId;
    /**
     * 应用secret
     */
    @NotBlank(message = "应用secret[app_secret]不可为空")
    @JsonProperty("app_secret")
    private String appSecret;
    /**
     * APP名称
     */
    @NotBlank(message = "APP名称[app_name]不可为空")
    @JsonProperty("app_name")
    private String appName;
    /**
     * 永久授权码
     */
    @JsonProperty("permanent")
    private String permanent;
    /**
     * token
     */
    @JsonProperty("token")
    private String token;
    /**
     * aeskey
     */
    @JsonProperty("encoding_aes_key")
    private String encodingAesKey;
}
