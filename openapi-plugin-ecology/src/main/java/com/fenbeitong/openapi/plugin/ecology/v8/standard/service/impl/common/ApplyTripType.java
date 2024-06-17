package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.common;

import java.util.Objects;

/**
 * 差旅类型
 * @author zhangpeng
 * @date 2021/5/27 19:55
 */
public enum ApplyTripType {
    /**
     * 飞机
     */
    AIR(7,"飞机"),
    /**
     * 火车
     */
    TRAIN(15,"火车"),
    /**
     * 酒店
     */
    HOTEL(11,"酒店"),
    /**
     * 国际机票
     */
    INTEL_AIR(40,"国际机票"),
    /**
     * 用车
     */
    CAR(3,"用车");

    private int code;

    private String value;

    public int getCode() {
        return code;
    }

    ApplyTripType(int code,String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getValueByKey(Integer key){
        if (Objects.isNull(key)) {
            return null;
        }
        ApplyTripType type = getByKey(key);
        if (Objects.isNull(type)){
            return null;
        }
        return type.getValue();
    }

    public static ApplyTripType getByKey(Integer key) {
        if (key == null) {
            return null;
        }
        for (ApplyTripType type : values()) {
            if (type.getCode() == key) {
                return type;
            }
        }
        return null;
    }
}
