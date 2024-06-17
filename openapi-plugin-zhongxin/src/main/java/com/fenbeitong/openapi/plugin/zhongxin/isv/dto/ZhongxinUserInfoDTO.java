package com.fenbeitong.openapi.plugin.zhongxin.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZhongxinUserInfoDTO {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("corp_id")
    private String corpId;

    @JsonProperty("corp_name")
    private String corpName;

    @JsonProperty("hash")
    private String hash;

}
