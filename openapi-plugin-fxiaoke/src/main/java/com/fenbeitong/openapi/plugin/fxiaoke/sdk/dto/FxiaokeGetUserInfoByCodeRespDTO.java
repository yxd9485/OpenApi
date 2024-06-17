package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * create on 2020-12-23 14:48:3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeGetUserInfoByCodeRespDTO {

    private Integer errorCode;

    private String errorMessage;

    private String data;


}