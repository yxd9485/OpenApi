package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 通讯录用户相关变更事件，包括 user_add, user_update 和 user_leave 事件类型
 *
 * @author lizhen
 * @date 2020/6/3
 */
@Data
public class FeiShuCallbackEmployeeDTO {

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

        @JsonProperty("open_id")
        private String openId;

        @JsonProperty("employee_id")
        private String employeeId;

        @JsonProperty("union_id")
        private String unionId;

    }

}
