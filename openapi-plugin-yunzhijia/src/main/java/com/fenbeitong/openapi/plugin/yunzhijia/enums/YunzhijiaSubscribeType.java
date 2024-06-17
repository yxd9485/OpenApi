package com.fenbeitong.openapi.plugin.yunzhijia.enums;

/**
 * 企业是否订阅公共号
 * @Auther zhang.peng
 * @Date 2021/7/30
 */
public enum YunzhijiaSubscribeType {

    YES("1","已订阅"),

    NO("0","未订阅");

    private String code;

    private String desc;

    YunzhijiaSubscribeType(String code, String desc) {
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
