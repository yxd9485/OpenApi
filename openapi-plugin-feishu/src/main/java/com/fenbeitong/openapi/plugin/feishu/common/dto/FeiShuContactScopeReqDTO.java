package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * @author lizhen
 * @date 2020/6/15
 */
@Data
public class FeiShuContactScopeReqDTO {

    private Integer code;

    private String msg;

    private ContactScope data;

    @Data
    public static class ContactScope {

        @JsonProperty("authed_departments")
        private List<String> authedDepartments;

        @JsonProperty("authed_employee_ids")
        private List<String> authedEmployeeIds;

        @JsonProperty("authed_open_ids")
        private List<String> authedOpenIds;

    }
}
