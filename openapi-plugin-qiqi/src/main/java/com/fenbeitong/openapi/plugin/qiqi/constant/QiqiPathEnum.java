package com.fenbeitong.openapi.plugin.qiqi.constant;

/**
 * @ClassName QiqiPathEnum
 * @Description 企企接口路径枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/19
 **/
public enum QiqiPathEnum {
    LIST_PATH("/v1/list", "查询路径"),
    ADD_PATH("/v1/add", "新增路径");

    private String code;
    private String desc;

    QiqiPathEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
