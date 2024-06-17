package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取suiteToken请求
 * Created by lizhen on 2020/3/19.
 */
@Data
public class SuiteTokenRequest {

    @JsonProperty("suite_id")
    private String suiteId;

    @JsonProperty("suite_secret")
    private String suiteSecret;

    @JsonProperty("suite_ticket")
    private String suiteTicket;
}
