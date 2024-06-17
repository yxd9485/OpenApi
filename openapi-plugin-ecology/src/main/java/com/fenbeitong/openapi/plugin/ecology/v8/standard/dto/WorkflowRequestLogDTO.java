package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: WorkflowRequestLogDTO</p>
 * <p>Description: 工作流审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/22 4:52 PM
 */
@Data
public class WorkflowRequestLogDTO {

    @JsonProperty("log_id")
    private String logId;

    @JsonProperty("node_id")
    private String nodeId;

    @JsonProperty("node_name")
    private String nodeName;

    @JsonProperty("operate_date")
    private String operateDate;

    @JsonProperty("operate_time")
    private String operateTime;

    @JsonProperty("operate_type")
    private String operateType;

    @JsonProperty("operator_dept")
    private String operatorDept;

    @JsonProperty("operator_id")
    private String operatorId;

    @JsonProperty("operator_name")
    private String operatorName;

    @JsonProperty("received_persons")
    private String receivedPersons;

    private String remark;
}
