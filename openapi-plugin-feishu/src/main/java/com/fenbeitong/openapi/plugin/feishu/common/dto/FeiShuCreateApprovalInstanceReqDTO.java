package com.fenbeitong.openapi.plugin.feishu.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * create on 2020-12-01 17:40:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuCreateApprovalInstanceReqDTO {

    @JsonProperty("approval_code")
    private String approvalCode;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("department_id")
    private String departmentId;

    private String form;

}