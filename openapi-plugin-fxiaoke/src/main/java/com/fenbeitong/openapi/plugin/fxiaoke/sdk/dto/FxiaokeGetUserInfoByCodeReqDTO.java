package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * create on 2020-12-23 14:42:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeGetUserInfoByCodeReqDTO {

    private String corpId;

    private String corpAccessToken;

    private String code;


}