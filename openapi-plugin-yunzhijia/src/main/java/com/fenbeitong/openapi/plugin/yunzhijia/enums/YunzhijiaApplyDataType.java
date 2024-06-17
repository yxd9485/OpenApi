package com.fenbeitong.openapi.plugin.yunzhijia.enums;

public enum YunzhijiaApplyDataType {
    /**
     * 测试数据
     */
    YUNZHIJIA_APPLY_DATA_TYPE_TEST("0", "测试数据"),
    /**
     * 正常数据
     */
    YUNZHIJIA_APPLY_DATA_TYPE_NORMAL("1", "正常数据");

    private final String key;
    private final String value;

    YunzhijiaApplyDataType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static YunzhijiaApplyDataType parse(String key) {
        if (key == null) {
            return null;
        }
        YunzhijiaApplyDataType[] itemAry = values();
        for (YunzhijiaApplyDataType item : itemAry) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

}
