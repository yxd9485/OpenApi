package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: EmployeeSource</p>
 * <p>Description: 人员选择</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 4:04 PM
 */
public enum EmployeeSource {

    /**
     * 预订人
     */
    bookUser(1, "预订人"),

    /**
     * 实际使用人
     */
    useUser(2, "实际使用人");

    private int type;

    private String value;

    EmployeeSource(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static EmployeeSource getEmployeeSource(int type) {
        for (EmployeeSource source : values()) {
            if (source.getType() == type) {
                return source;
            }
        }
        return null;
    }

    public static String getConfigName() {
        return "employeeSource";
    }
}
