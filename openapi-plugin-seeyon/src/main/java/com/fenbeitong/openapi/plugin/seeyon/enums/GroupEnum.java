package com.fenbeitong.openapi.plugin.seeyon.enums;

/**
 * @Auther zhang.peng
 * @Date 2021/7/27
 */
public enum GroupEnum {

    IS_GROUP("0", "集团类型"),
    NOT_GROUP("1", "非集团");

    private String code;

    private String desc;

    GroupEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
