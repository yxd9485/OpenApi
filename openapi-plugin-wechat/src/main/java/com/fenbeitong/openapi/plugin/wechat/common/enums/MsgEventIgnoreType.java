package com.fenbeitong.openapi.plugin.wechat.common.enums;

/**
 * 忽略事件
 * @author lizhen
 */
public enum MsgEventIgnoreType {

    NOT_IGNORE("0", "不忽略"),
    IGNORE_ALL("1", "忽略所有");

    private String key;
    private String value;

    MsgEventIgnoreType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
}
