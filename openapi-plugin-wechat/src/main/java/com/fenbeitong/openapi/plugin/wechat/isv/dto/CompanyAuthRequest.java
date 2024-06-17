package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 企业授权请求参数
 * Created by log.chang on 2020/3/12.
 */
@Data
public class CompanyAuthRequest {

    @JsonProperty("auth_code")
    private String authCode;

}
