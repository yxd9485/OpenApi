package com.fenbeitong.openapi.plugin.yunzhijia.enums;

/**
 * @Auther zhang.peng
 * @Date 2021/4/30
 */
public enum FbtTripType {
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
    INTL_AIR(40,"国际机票"),
    /**
     * 用车
     */
    CAR(3,"用车");

    private int code;

    private String value;

    public int getCode() {
        return code;
    }

    FbtTripType(int code , String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static FbtTripType parse(Integer key) {
        if (key == null) {
            return null;
        }
        FbtTripType[] itemAry = values();
        for (FbtTripType item : itemAry) {
            if (item.getCode() == key) {
                return item;
            }
        }
        return null;
    }
}
