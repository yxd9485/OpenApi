package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 审批定义DTO
 * create on 2020-12-01 14:14:40
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuApprovalDefineRespDTO {

    private Integer code;

    private String msg;

    private ApprovalDefine data;


    @Data
    public static class ApprovalDefine {

        @JsonProperty("approval_name")
        private String approvalName;

        private String form;

        @JsonProperty("node_list")
        private List<NodeList> nodeList;

    }

    @Data
    public static class NodeList {

        private String name;

        @JsonProperty("need_approver")
        private boolean needApprover;

        @JsonProperty("node_id")
        private String nodeId;

        @JsonProperty("node_type")
        private String nodeType;


    }
}