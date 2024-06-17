package com.fenbeitong.openapi.plugin.feishu.isv.constant;

public enum PushMessageResultState {
    /**
     * 发送成功
     */
    SEND_SUCCESS(1),
    /**
     * 发送失败
     */
    SEND_FAIL(0);

    private int code;

    public int getCode() {
        return code;
    }

    PushMessageResultState(int code) {
        this.code = code;
    }
}
