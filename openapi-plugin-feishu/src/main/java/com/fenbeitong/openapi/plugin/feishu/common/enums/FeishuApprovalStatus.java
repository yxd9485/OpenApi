package com.fenbeitong.openapi.plugin.feishu.common.enums;

/**
 * Created by dave.hansins on 19/12/13.
 */
public enum FeishuApprovalStatus {
    /**
     * 创建
     */
    APPROVED("APPROVED"),
    /**
     * 审批中
     */
    PENDING("PENDING"),
    /**
     * 拒绝
     */
    REJECTED("REJECTED"),
    /**
     * 撤回
     */
    CANCELED("CANCELED"),
    /**
     * 取消
     */
    REVERTED("REVERTED"),
    /**
     * 删除
     */
    DELETED("DELETED");

    private String status;

    FeishuApprovalStatus(String msg) {
        this.status = msg;
    }

    public String getStatus() {
        return status;
    }
}
