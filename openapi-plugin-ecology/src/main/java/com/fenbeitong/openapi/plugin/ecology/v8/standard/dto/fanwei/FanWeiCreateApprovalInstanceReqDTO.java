package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 主动向泛微推送工作流
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FanWeiCreateApprovalInstanceReqDTO {

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