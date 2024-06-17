package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: DepartmentSource</p>
 * <p>Description: 部门选择</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 4:04 PM
 */
public enum DepartmentSource {

    /**
     * 部门选择
     */
    feeBelongDept(1, "费用归属部门");

    private int type;

    private String value;

    DepartmentSource(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static DepartmentSource getDepartmentSource(int type) {
        for (DepartmentSource source : values()) {
            if (source.getType() == type) {
                return source;
            }
        }
        return null;
    }

    public static String getConfigName() {
        return "departmentSource";
    }
}
