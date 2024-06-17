package com.fenbeitong.openapi.plugin.wechat.eia.enums;

/**
 * Created by dave.hansins on 19/12/16.
 */
public enum ProcessApprovolType {
    /**
     * 正常审批单
     */
    NONE("NONE"),
    /**
     * 撤销审批单
     */
    REVOKE("REVOKE"),
    /**
     * 修改审批单
     */
    MODIFY("MODIFY"),
    ;
    String value;

    ProcessApprovolType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
