package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.enums;

/**
 * 泛微流程查看状态枚举
 * @Auther zhang.peng
 * @Date 2021/12/13
 */
public enum ViewTypeEnums {

    /**
     * 未读
     */
    UN_READ("0","未读"),

    /**
     * 已读
     */
    READ("1","已读");

    private String code;
    private String desc;

    ViewTypeEnums(String code, String desc) {
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
