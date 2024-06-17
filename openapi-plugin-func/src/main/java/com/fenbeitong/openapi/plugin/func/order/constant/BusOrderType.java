package com.fenbeitong.openapi.plugin.func.order.constant;

/**
 * 汽车票订单类型
 * @author zhangpeng
 * @date 2022/4/29 10:16 上午
 */
public enum BusOrderType {

    /**
     * 汽车票原票
     */
    ORIGINAL(0, "原票"),
    /**
     * 汽车票退票
     */
    REFUND(1,"退票");

    private int type;
    private String desc;

    BusOrderType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
