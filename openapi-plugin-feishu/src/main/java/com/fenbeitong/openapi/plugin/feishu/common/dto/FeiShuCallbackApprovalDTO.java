package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 审批变更时间，包含创建，修改，撤销
 *
 * @author lizhen
 * @date 2020/6/3
 */
@Data
public class FeiShuCallbackApprovalDTO {

    private String uuid;

    private String token;

    private String ts;

    private String type;

    private Event event;

    @Data
    public static class Event {

        private String type;

        @JsonProperty("app_id")
        private String appId;

        @JsonProperty("tenant_key")
        private String tenantKey;

        @JsonProperty("approval_code")
        private String approvalCode;

        @JsonProperty("instance_code")
        private String instanceCode;

        @JsonProperty("operate_time")
        private String operateTime;

        @JsonProperty("status")
        private String status;

    }
}
