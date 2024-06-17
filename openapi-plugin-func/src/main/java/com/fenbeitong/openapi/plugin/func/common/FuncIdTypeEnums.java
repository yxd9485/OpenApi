package com.fenbeitong.openapi.plugin.func.common;

/**
 * func id type的枚举
 * 和uc的不一样...
 * @author lizhen
 * @date 2021/1/15
 */
public enum FuncIdTypeEnums {

    FB_ID(0, "分贝ID"),
    THIRD_ID(1, "第三方ID");

    int key;
    String value;

    FuncIdTypeEnums(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static Enum getEnumByKey(Integer key) {
        if (key == null) {
            return null;
        }
        for (FuncIdTypeEnums item : values()) {
            if (item.getKey() == key) {
                return item;
            }
        }
        return null;
    }
}
