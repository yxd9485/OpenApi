package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * <p>Title: OrderApplyAgreeReqDTO</p>
 * <p>Description: 订单审批同意请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/27 5:13 PM
 */
@Data
public class OrderApplyRepluseReqDTO {
    @NotBlank(message = "审批id[apply_id]不可为空")
    @JsonProperty("apply_id")
    private String applyId;
    @NotBlank(message = "审批id[task_id]不可为空")
    @JsonProperty("task_id")
    private String taskId;
    @NotBlank(message = "审批人id[approver_id]不可为空")
    @JsonProperty("approver_id")
    private String approverId;

    private BigDecimal price;
    //驳回原因
    @NotBlank(message = "驳回理由[comment]不可为空")
    private String comment;
}
