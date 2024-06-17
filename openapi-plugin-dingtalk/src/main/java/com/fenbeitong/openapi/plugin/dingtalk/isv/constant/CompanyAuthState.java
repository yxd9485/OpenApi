package com.fenbeitong.openapi.plugin.dingtalk.isv.constant;

/**
 * ISV企业授权状态
 */
public enum CompanyAuthState {

    /**
     * 取消授权
     */
    AUTH_CANCEL(0),
    /**
     * 已授权
     */
    AUTH_SUCCESS(1),

    /**
     * 授权失效
     */
    AUTH_EXPIRED(2);

    private int code;

    public int getCode() {
        return code;
    }

    CompanyAuthState(int code) {
        this.code = code;
    }
}
