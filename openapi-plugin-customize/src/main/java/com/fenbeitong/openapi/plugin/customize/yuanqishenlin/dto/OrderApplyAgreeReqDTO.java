package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>Title: OrderApplyAgreeReqDTO</p>
 * <p>Description: 订单审批同意请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 */
@Data
public class OrderApplyAgreeReqDTO {
    @NotBlank(message = "审批id[apply_id]不可为空")
    @JsonProperty("apply_id")
    private String applyId;
    @NotBlank(message = "任务id[task_id]不可为空")
    @JsonProperty("task_id")
    private String taskId;
    @NotBlank(message = "审批人id[approver_id]不可为空")
    @JsonProperty("approver_id")
    private String approverId;

    private BigDecimal price;
    //审批同意时才需要传递
    @JsonProperty("seat_item")
    private Map seatItem;
}
