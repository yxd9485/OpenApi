package com.fenbeitong.openapi.plugin.ecology.v8.common;

/**
 * @author lizhen
 */
public enum WorkFlowState {
    /**
     * 初始化
     */
    INIT(0, "初始化"),
    /**
     * 成功
     */
    SUCESS(1, "成功"),
    /**
     * 关闭
     */
    CLOSEED(2, "关闭"),
    ;

    private final int key;
    private final String value;

    WorkFlowState(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
