package com.fenbeitong.openapi.plugin.wechat.eia.enums;

/**
 * Created by dave.hansins on 20/1/8.
 */
public enum WeChatApplyType {


    /**
     * 飞机
     */
    AIR("机票"),
    /**
     * 火车
     */
    TRAIN("火车"),
    /**
     * 酒店
     */
    HOTEL("酒店"),
    /**
     * 国际机票
     */
    INTL_AIR("国际机票"),
    /**
     * 用车
     */
    CAR("用车");

    private String msg;

    public String getMsg() {
        return msg;
    }

    WeChatApplyType(String msg) {
        this.msg = msg;
    }

}
