package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取企业授权信息请求参数
 * Created by lizhen on 2020/3/19.
 */
@Data
public class AuthInfoRequest {

    @JsonProperty("auth_corpid")
    private String authCorpid;

    @JsonProperty("permanent_code")
    private String permanentCode;

}
