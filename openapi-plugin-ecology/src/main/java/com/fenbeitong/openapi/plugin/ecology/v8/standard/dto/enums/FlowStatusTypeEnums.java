package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.enums;

/**
 * 泛微流程处理状态枚举
 * @Auther zhang.peng
 * @Date 2021/12/13
 */
public enum FlowStatusTypeEnums {

    /**
     * 待办
     */
    TODO(0,"待办"),

    /**
     * 已办
     */
    DONE(2,"已办"),

    /**
     * 办结
     */
    FINISH(4,"办结"),

    /**
     * 抄送
     */
    CARBON_COPY(8,"抄送");

    private int code;
    private String desc;

    FlowStatusTypeEnums(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
