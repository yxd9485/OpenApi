package com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.enums;

public enum FxiaokeApprovalStatus {
    /**
     * 进行中
     */
    IN_PROGRESS("in_progress", "进行中"),
    /**
     * 通过
     */
    PASS("pass", "通过"),
    /**
     * 取消
     */
    CANCEL("cancel", "取消"),
    /**
     * 拒绝
     */
    REJECT("reject", "拒绝"),
    /**
     * 异常
     */
    ERROR("error", "异常");

    private final String key;
    private final String value;

    FxiaokeApprovalStatus(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
