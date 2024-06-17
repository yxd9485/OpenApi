package com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.enums;

public enum FxiaokeApprovalTriggerType {
    /**
     * 创建
     */
    APPROVAL_CREATE("Create", 1),
    /**
     * 删除
     */
    APPROVAL_DELETE("Delete", 2),
    /**
     * 修改
     */
    APPROVAL_UPDATE("fxiaoke_approval_trip", 3);


    private final String key;
    private final int value;

    FxiaokeApprovalTriggerType(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}
