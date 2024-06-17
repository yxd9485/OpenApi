package com.fenbeitong.openapi.plugin.qiqi.constant;
/**
 * @ClassName UseRangeEnum
 * @Description 员工状态枚举
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/6/15
 **/
public enum EmployeeStatusEnum {
    INCUMBENT(1, "在职"),
    RESIGNATION(2, "离职");

    private Integer code;
    private String desc;

    EmployeeStatusEnum(Integer code, String desc) {
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
