package com.fenbeitong.openapi.plugin.qiqi.constant;

/**
 * @ClassName UseRangeEnum
 * @Description 可见范围枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/6/15
 **/
public enum UseRangeEnum {
    UNLIMITED(1, "不限"),
    LIMIT_PROJECT_MEMBER(2, "仅限项目成员");

    private Integer code;
    private String desc;

    UseRangeEnum(Integer code, String desc) {
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
