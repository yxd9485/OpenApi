package com.fenbeitong.openapi.plugin.wechat.eia.enums;

/**
 * Created by dave.hansins on 19/12/9.
 */
public enum WeChatProcessEvent {
    /**
     * 审批创建
     */
    START("start"),
    /**
     * 审批结束
     */
    FINISH("finish");

    String value;

    WeChatProcessEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
