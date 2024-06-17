package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by hanshuqi on 2020/03/27.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaApplyReqDTO {
    /**
     * 企业ID
     */
    @NotBlank(message = "企业ID[corp_id]不可为空")
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 审批应用ID
     */
    @NotBlank(message = "审批应用ID[agent_id]不可为空")
    @JsonProperty("agent_id")
    private String agentId;
    /**
     * 审批应用开发者key
     */
    @NotBlank(message = "审批应用开发者key[agent_key]不可为空")
    @JsonProperty("agent_key")
    private String agentKey;
    /**
     * 审批应用开发者secret
     */
    @NotBlank(message = "审批应用开发者secret[agent_secret]不可为空")
    @JsonProperty("agent_secret")
    private String agentSecret;
    /**
     * 审批应用状态，0：可用，1：不可用
     */
    @NotNull(message = "审批应用状态，0：可用，1：不可用[state]不可为空")
    @JsonProperty("state")
    private Integer state;
    /**
     * 审批回调地址
     */
    @JsonProperty("call_back_url")
    private String callBackUrl;
}
