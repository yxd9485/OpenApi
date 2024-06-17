package com.fenbeitong.openapi.plugin.wechat.isv.enums;

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
    AUTH_EXPIRED(2),


    /**
     * 未生成合同
     */
    NO_CONTACT(3);

    private int code;

    public int getCode() {
        return code;
    }

    CompanyAuthState(int code) {
        this.code = code;
    }
}
