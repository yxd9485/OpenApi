package com.fenbeitong.openapi.plugin.dingtalk.isv.constant;

public enum DingtalkTripType {
    /**
     * 火车
     */
    TRAIN("火车", "Train"),
    /**
     * 飞机
     */
    PLANE("飞机", "Plane"),
    /**
     * 其他
     */
    OTHER("其他", "Other"),
    /**
     * 汽车
     */
    CAR("汽车", "Car"),
    ;

    private final String key;
    private final String value;

    DingtalkTripType(String key, String value) {
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
