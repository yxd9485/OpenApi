package com.fenbeitong.openapi.plugin.wechat.eia.enums;

/**
 * Created by dave.hansins on 19/12/16.
 */
public enum WeChatApplyContentControl {

    SELECTOR("Selector"),
    TEXT("Text"),
    DATE("Date"),
    MONEY("Money"),
    NUMBER("Number"),
    CONTACT("Contact"),
    TABLE("Table"),
    TEXTAREA("Textarea");


    String value;

    WeChatApplyContentControl(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
