package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FeiShuApprovalRespDTO {
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("data")
    private ApprovalData data;

    @Data
    public static class ApprovalData {
        //审批实例单号
        @JsonProperty("approval_code")
        private String approvalCode;
        //审批名称，为审批应用的名称
        @JsonProperty("approval_name")
        private String approvalName;
        //用户ID
        @JsonProperty("user_id")
        private String userId;
        //流水号
        @JsonProperty("serial_number")
        private String serialNumber;
        //部门ID
        @JsonProperty("department_id")
        private String departmentId;
        //form表单,list类型的jsonstring集合,包含不同组件的数据集合
        @JsonProperty("form")
        private String form;
        //open_id
        @JsonProperty("open_id")
        private String openId;

    }
}
