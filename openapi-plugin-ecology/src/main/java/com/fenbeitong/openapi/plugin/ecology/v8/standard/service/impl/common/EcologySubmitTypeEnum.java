package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

/**
 * 泛微提交审批类型枚举
 * @Auther zhang.peng
 * @Date 2021/11/23
 */
public enum EcologySubmitTypeEnum {

    /**
     * 提交类型：submit 提交 subnoback 提交不需回复  subback 提交需要回复 reject 退回
     */
    SUBMIT("submit", 1),
    SUB_NO_BACK("subnoback", 2),
    SUB_BACK("subback", 3),
    REJECT("reject", 4);

    private final String key;
    private final int value;

    EcologySubmitTypeEnum(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}
