package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by hanshuqi on 2020/03/27.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaApplyDTO {
    /**
     * 企业ID
     */
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 审批应用ID
     */
    @JsonProperty("agent_id")
    private String agentId;
    /**
     * 审批应用开发者key
     */
    @JsonProperty("agent_key")
    private String agentKey;
    /**
     * 审批应用开发者secret
     */
    @JsonProperty("agent_secret")
    private String agentSecret;
    /**
     * 审批应用状态，0：可用，1：不可用
     */
    @JsonProperty("state")
    private Integer state;
    /**
     * 审批回调地址
     */
    @JsonProperty("call_back_url")
    private String callBackUrl;
}
