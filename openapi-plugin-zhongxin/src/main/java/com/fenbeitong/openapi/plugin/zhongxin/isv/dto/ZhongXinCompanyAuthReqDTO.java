package com.fenbeitong.openapi.plugin.zhongxin.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 企业授权参数
 *
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZhongXinCompanyAuthReqDTO {

    @JsonProperty("corpId")
    private String corpId;

    @JsonProperty("corpName")
    private String corpName;

    @JsonProperty("corpCode")
    private String corpCode;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("unauthed")
    private String unauthed;

    @JsonProperty("scCode")
    private String scCode;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("nonce")
    private String nonce;

}