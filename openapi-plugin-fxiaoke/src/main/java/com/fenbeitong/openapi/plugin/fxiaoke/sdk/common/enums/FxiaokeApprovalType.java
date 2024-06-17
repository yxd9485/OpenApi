package com.fenbeitong.openapi.plugin.fxiaoke.sdk.common.enums;

public enum FxiaokeApprovalType {
    /**
     * 进行中
     */
    CAR_APPROVAL("fxiaoke_approval_car", "用车审批"),
    /**
     * 通过
     */
    TRIP_APPROVAL("fxiaoke_approval_trip", "差旅审批");


    private final String key;
    private final String value;

    FxiaokeApprovalType(String key, String value) {
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
