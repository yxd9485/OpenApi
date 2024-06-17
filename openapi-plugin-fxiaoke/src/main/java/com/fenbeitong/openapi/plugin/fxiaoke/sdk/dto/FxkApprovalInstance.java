package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.Data;

/**
 * <p>Title: FxkApprovalInstance</p>
 * <p>Description: 纷享销客审批实例</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/25 7:41 PM
 */
@Data
public class FxkApprovalInstance {

    /**
     * 流程实例id
     */
    private String instanceId;

    /**
     * 流程实例名称
     */
    private String instanceName;

    /**
     * 流程实例关联的数据id
     */
    private String dataId;

    /**
     * 操作类型 Create新建，Update编辑，Invalid作废，Delete删除
     */
    private String triggerType;

    /**
     * 流程实例状态 in_progress 进行中,pass 通过,error 异常,cancel 取消,reject 拒绝
     */
    private String state;

    /**
     * 流程实例创建时间
     */
    private Long createTime;

    /**
     * 流程实例最后更新时间
     */
    private Long lastModifyTime;

    /**
     * 流程实例结束时间
     */
    private Long endTime;

    /**
     * 审批流程 apiName
     */
    private String flowApiName;

    /**
     * 流程实例发起人
     */
    private String applicantOpenUserId;

    /**
     * 流程实例的取消时间
     */
    private Long cancelTime;

    /**
     * 数据对象的 apiName
     */
    private String objectApiName;
}
