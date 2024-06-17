package com.fenbeitong.openapi.plugin.ecology.v8.kailin.constant;

public enum KaiLinTripType {
    /**
     * 飞机
     */
    PLANE("0", "飞机"),
    /**
     * 火车
     */
    TRAIN("1", "火车"),

    /**
     * 自驾
     */
    SELF_DRIVING("2", "自驾/班车"),
    ;

    private final String key;
    private final String value;

    KaiLinTripType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
