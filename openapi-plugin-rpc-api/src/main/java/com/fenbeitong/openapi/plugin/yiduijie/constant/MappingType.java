package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: MappingType</p>
 * <p>Description: 映射类型</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 11:52 AM
 */
public enum MappingType {

    /**
     * 科目
     */
    account("科目", "account"),

    /**
     * 部门
     */
    department("部门", "department"),

    /**
     * 部门
     */
    employee("人员", "employee"),

    /**
     * 项目
     */
    project("项目", "project"),

    /**
     * 供应商
     */
    supplier("供应商", "supplier"),

    /**
     * 客户
     */
    customer("客户", "customer");

    private String name;

    private String value;

    MappingType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }


}
