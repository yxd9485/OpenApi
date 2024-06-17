package com.fenbeitong.openapi.plugin.dingtalk.eia.constant;

/**
 * 差旅类型
 * @author zhaokechun
 * @date 2018/12/6 19:55
 */
public enum ApplyTripType {
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
    INTEL_AIR(40),
    /**
     * 用车
     */
    CAR(3);

    private int code;

    public int getCode() {
        return code;
    }

    ApplyTripType(int code) {
        this.code = code;
    }
}
