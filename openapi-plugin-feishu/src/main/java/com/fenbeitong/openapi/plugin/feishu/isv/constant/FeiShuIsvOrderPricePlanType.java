package com.fenbeitong.openapi.plugin.feishu.isv.constant;

/**
 * 价格方案类型
 */
public enum FeiShuIsvOrderPricePlanType {

    /**
     * 已授权
     */
    PER_YEAR(1, "per_year");


    private int type;

    private String value;

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    FeiShuIsvOrderPricePlanType(int type, String value) {
        this.type = type;
        this.value = value;
    }
}
