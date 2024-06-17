package com.fenbeitong.openapi.plugin.zhongxin.common.constant;

/**
 * ISV企业授权状态
 */
public enum CompanyAuthState {

    /**
     * 已授权
     */
    AUTH_SUCCESS("0"),
    /**
     * 未授权
     */
    AUTH_INIT("1"),

    /**
     * 已过期
     * */
    AUTH_EXPIRED("2");

    private String code;

    public String getCode() {
        return code;
    }

    CompanyAuthState(String code) {
        this.code = code;
    }
}
