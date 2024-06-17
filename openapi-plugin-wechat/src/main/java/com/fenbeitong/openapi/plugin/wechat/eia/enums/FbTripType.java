package com.fenbeitong.openapi.plugin.wechat.eia.enums;

/**
 * Created by dave.hansins on 19/12/16.
 */
public enum FbTripType {
    /**
     * 飞机
     */
    AIR(7),
    /**
     * 火车
     */
    TRAIN(15),
    /**
     * 酒店
     */
    HOTEL(11),
    /**
     * 国际机票
     */
    INTL_AIR(40),
    /**
     * 用车
     */
    CAR(3);

    private int code;

    public int getCode() {
        return code;
    }

    FbTripType(int code) {
        this.code = code;
    }
}
