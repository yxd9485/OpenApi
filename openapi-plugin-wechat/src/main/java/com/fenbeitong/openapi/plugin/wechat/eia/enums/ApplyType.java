package com.fenbeitong.openapi.plugin.wechat.eia.enums;

/**
 * Created by dave.hansins on 19/12/25.
 */
public enum ApplyType {

    /**
     * 差旅审批
     */
    TRIP(1),
    /**
     * 审批用车
     */
    APPLY_CAR(12),
    /**
     * 订单审批
     */
    ORDER_APPLY(3);


    private int code;

    public int getCode() {
        return code;
    }

    ApplyType(int code) {
        this.code = code;
    }
}
