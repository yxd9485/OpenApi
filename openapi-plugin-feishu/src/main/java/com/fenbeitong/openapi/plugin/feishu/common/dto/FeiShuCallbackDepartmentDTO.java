package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 通讯录部门相关变更事件，包括 dept_add, dept_update 和 dept_delete
 *
 * @author lizhen
 * @date 2020/6/3
 */
@Data
public class FeiShuCallbackDepartmentDTO {

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

        @JsonProperty("open_department_id")
        private String openDepartmentId;

    }
}
