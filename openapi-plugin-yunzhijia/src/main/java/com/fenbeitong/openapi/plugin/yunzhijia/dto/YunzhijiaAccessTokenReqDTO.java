package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class YunzhijiaAccessTokenReqDTO {

//    @NotBlank(message = "企业ID[eid]不能为空")
    @JsonProperty("eid")
    private String eid;
//    @NotBlank(message = "轻应用id[appId]不能为空")
    @JsonProperty("appId")
    private String appId;
//    @NotBlank(message = "轻应用[secret]不能为空")
    @JsonProperty("secret")
    private String secret;
    @NotBlank(message = "时间戳[timestamp]不能为空")
    @JsonProperty("timestamp")
    private long timestamp;
    @NotBlank(message = "授权级别[scope]不能为空")
    @JsonProperty("scope")
    private String scope;
    //刷新token令牌,刷新token时使用
    @JsonProperty("refreshToken")
    private String refreshToken;

}
