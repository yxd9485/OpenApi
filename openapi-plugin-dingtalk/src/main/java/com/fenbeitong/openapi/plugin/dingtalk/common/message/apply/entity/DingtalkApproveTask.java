package com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by xiaohai on 2021/08/04.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dingtalk_approve_task")
public class DingtalkApproveTask {

    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 审批单ID
     */
    @Column(name = "APPROVE_ID")
    private String approveId;

    /**
     * 任务ID
     */
    @Column(name = "TASK_ID")
    private Long taskId;

    /**
     * 接收消息的用户ID
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 审批单状态
     */
    @Column(name = "APPROVE_STAUTS")
    private Integer approveStauts;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
