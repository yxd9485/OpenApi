package com.fenbeitong.openapi.plugin.qiqi.constant;

/**
 * @ClassName StateEnum
 * @Description 启用状态枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/18
 **/
public enum StateEnum {
    STATE_DISABBLE(0, "停用"),
    STATE_ENABLE(1, "启用");

    private Integer code;
    private String desc;

    StateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
