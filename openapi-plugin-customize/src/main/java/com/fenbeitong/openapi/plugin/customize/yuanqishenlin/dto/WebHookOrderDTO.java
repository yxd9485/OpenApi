package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.event.core.BaseEvent;
import com.fenbeitong.openapi.plugin.event.saas.dto.ApplyOrderDetailCommonDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName WebHookOrderDTO
 * @Description webhook回调
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/13 下午5:23
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebHookOrderDTO implements BaseEvent{
    /**
     * 申请单id
     */
    @JsonProperty("apply_order_id")
    private String applyOrderId;
    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;
    /**
     * 审批定义id
     */
    @JsonProperty("process_def_id")
    private String processDefId;
    /**
     * 审批定义名称
     */
    @JsonProperty("process_def_name")
    private String processDefName;
    /**
     * 审批实例id
     */
    @JsonProperty("process_instance_id")
    private String processInstanceId;
    /**
     * 审批实例状态
     */
    @JsonProperty("process_instance_status")
    private String processInstanceStatus;
    /**
     * 审批实例发起人id
     */
    @JsonProperty("starter_id")
    private String starterId;
    /**
     * 审批实例发起人名称
     */
    @JsonProperty("starter_name")
    private String starterName;
    /**
     * 审批发起时间
     */
    @JsonProperty("process_start_time")
    private Long processStartTime;
    /**
     * 审批实例最近更新时间
     */
    @JsonProperty("process_update_time")
    private Long processUpdateTime;
    /**
     * 预留扩展信息
     */
    @JsonProperty("ext_info")
    private String extInfo;
    /**
     * 公司名称
     */
    @JsonProperty("company_name")
    private String companyName;
    /**
     * pc端发起链接
     */
    @JsonProperty("pc_sponsor_link")
    private String pcSponsorLink;
    /**
     * pc端实例跳转链接
     */
    @JsonProperty("pc_jump_link")
    private String pcJumpLink;
    /**
     * 移动端实例跳转链接
     */
    @JsonProperty("app_jump_link")
    private String appJumpLink;
    /**
     * 节点id
     */
    @JsonProperty("node_id")
    private String nodeId;
    /**
     * 节点名称
     */
    @JsonProperty("node_name")
    private String nodeName;
    /**
     * 抄送id
     */
    @JsonProperty("notifier_task_id")
    private String notifierTaskId;
    /**
     * 抄送人id
     */
    @JsonProperty("notifier_id")
    private String notifierId;
    /**
     * 抄送人姓名
     */
    @JsonProperty("notifier_name")
    private String notifierName;
    /**
     * 抄送人手机号
     */
    @JsonProperty("notifier_phone")
    private String notifierPhone;
    /**
     * 抄送发起时间
     */
    @JsonProperty("notifier_task_create_time")
    private Long notifierTaskCreateTime;
    /**
     * 抄送最近更新时间
     */
    @JsonProperty("notifier_task_update_time")
    private Long notifierTaskUpdateTime;

    /**
     * 审批结束时间
     */
    @JsonProperty("process_end_time")
    private Long processEndTime;

    /**
     * 任务id
     */
    @JsonProperty("task_id")
    private String taskId;
    /**
     * 审批人id
     */
    @JsonProperty("approver_id")
    private String approverId;
    /**
     * 审批人名字
     */
    @JsonProperty("approver_name")
    private String approverName;
    /**
     * 审批人手机号
     */
    @JsonProperty("approver_phone")
    private String approverPhone;
    /**
     * 任务状态
     */
    @JsonProperty("task_status")
    private String taskStatus;
    /**
     * 任务创建时间
     */
    @JsonProperty("task_create_time")
    private Long taskCreateTime;
    /**
     * 任务最近更新时间
     */
    @JsonProperty("task_update_time")
    private Long taskUpdateTime;
    /**
     * 审批实例发起人电话
     */
    @JsonProperty("starter_phone")
    private String starterPhone;
    /**
     * 申请单类型（一级分类）
     */
    @JsonProperty("apply_order_type")
    private Integer applyOrderType;
    /**
     * 审批单类型id
     */
    @JsonProperty("apply_type")
    private Integer applyType;//二级分类

    @JsonProperty("view_type")
    private Integer viewType;
    /**
     * 节点状态
     */
    @JsonProperty("node_status")
    private String nodeStatus;

    @JsonProperty("apply_order_detail")
    private ApplyOrderDetailCommonDTO applyOrderDetail;//审批单详情信息公共

    /**
     * 订单类型
     */
    @JsonProperty("order_type")
    private Integer orderType;
}

