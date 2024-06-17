package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: BusinessType</p>
 * <p>Description: 业务类型</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 6:02 PM
 */
public enum BusinessType {

    /**
     * 核销申请单
     */
    APPLY(1, "APPLY"),

    /**
     * 账单
     */
    BILL(2, "BILL"),

    /**
     * 虚拟卡凭证
     */
    PAYMENT(3, "PUBLIC_PAY"),

    /**
     * 凭证底表生成凭证
     */
    VOUCHER(100, "VOUCHER");

    private int type;

    private String value;

    BusinessType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static BusinessType getBusinessType(int type) {
        for (BusinessType businessType : values()) {
            if (businessType.getType() == type) {
                return businessType;
            }
        }
        return BusinessType.APPLY;
    }
}
