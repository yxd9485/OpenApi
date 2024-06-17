package com.fenbeitong.openapi.plugin.dingtalk.eia.enums;

/**
 * 查询部门信息枚举
 * @Auther zhang.peng
 * @Date 2021/8/30
 */
public enum QueryDepartmentTypeEnum {

    DEPARTMENT_TYPE_NAME(1,"部门名称"),
    DEPARTMENT_TYPE_FULL_NAME(2,"部门全名称（父级到子部门）");

    private int type;

    private String desc;

    QueryDepartmentTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}

