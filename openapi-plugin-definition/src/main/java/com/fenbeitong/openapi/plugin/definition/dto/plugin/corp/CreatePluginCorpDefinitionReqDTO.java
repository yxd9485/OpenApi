package com.fenbeitong.openapi.plugin.definition.dto.plugin.corp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 添加插件集成企业配置信息
 * Created by log.chang on 2019/12/23.
 */
@Data
@Builder
public class CreatePluginCorpDefinitionReqDTO {

    @NotBlank
    @JsonProperty("app_id")
    private String appId; // 分贝通企业ID

    @JsonProperty("admin_id")
    private String adminId;//管理员ID-分贝通

    @JsonProperty("third_admin_id")
    private String thirdAdminId;// 三方管理员id（钉钉/企业微信）

    @JsonProperty("third_corp_id")
    private String thirdCorpId;//钉钉/企业微信 平台企业ID

    @JsonProperty("third_app_key")
    private String thirdAppKey; // 三方平台appKey

    @JsonProperty("third_app_secret")
    private String thirdAppSecret; // 三方密钥

    @JsonProperty("third_app_name")
    private String thirdAppName; // 三方应用名

    @JsonProperty("third_agent_id")
    private Long thirdAgentId; // 三方

    @JsonProperty("proxy_url")
    private String proxyUrl; // 代理url

    @JsonProperty("open_type")
    private Integer openType; //开放类型

}
