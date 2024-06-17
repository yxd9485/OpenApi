package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: WorkflowDTO</p>
 * <p>Description: 工作流DTO</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/22 4:48 PM
 */
@Data
public class WorkflowDTO {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("request_name")
    private String requestName;

    @JsonProperty("request_level")
    private String requestLevel;

    @JsonProperty("creator_id")
    private String creatorId;

    @JsonProperty("creator_name")
    private String creatorName;

    @JsonProperty("current_node_id")
    private String currentNodeId;

    @JsonProperty("current_node_name")
    private String currentNodeName;

    private String status;

    @JsonProperty("workflow_id")
    private String workflowId;

    @JsonProperty("workflow_name")
    private String workflowName;

    @JsonProperty("workflow_type_id")
    private String workflowTypeId;

    @JsonProperty("workflow_type_name")
    private String workflowTypeName;

    @JsonProperty("form_data")
    private List<WorkflowFormDataDTO> formData;

    @JsonProperty("detail_form_1")
    private List<List<WorkflowFormDataDTO>> detailForm1;

    @JsonProperty("detail_form_2")
    private List<List<WorkflowFormDataDTO>> detailForm2;

    @JsonProperty("detail_form_3")
    private List<List<WorkflowFormDataDTO>> detailForm3;

    @JsonProperty("detail_form_4")
    private List<List<WorkflowFormDataDTO>> detailForm4;

    @JsonProperty("detail_form_5")
    private List<List<WorkflowFormDataDTO>> detailForm5;

    @JsonProperty("request_log")
    private List<WorkflowRequestLogDTO> requestLog;
}
