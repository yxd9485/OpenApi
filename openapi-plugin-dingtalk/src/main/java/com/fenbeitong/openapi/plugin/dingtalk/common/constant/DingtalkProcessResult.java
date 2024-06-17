package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * @author zhaokechun
 * @date 2018/12/11 17:05
 */
public enum DingtalkProcessResult {

    /**
     * 审批通过
     */
    AGREE("agree"),
    /**
     * 审批不通过
     */
    REFUSE("refuse"),

    /**
     * 待审批
     */
    WAIT("wait"),

    /**
     * 审批状态
     */
    COMPLETETD("COMPLETED"),

    /**
     * 通过
     */
    PASS("pass");

    String value;

    DingtalkProcessResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
