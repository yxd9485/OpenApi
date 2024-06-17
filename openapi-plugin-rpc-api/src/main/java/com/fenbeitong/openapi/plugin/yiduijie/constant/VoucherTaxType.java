package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: VoucherCreateType</p>
 * <p>Description: 凭证税金合并方式</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 7:07 PM
 */
public enum VoucherTaxType {

    /**
     * 税金合并
     */
    merge(1, "是"),

    /**
     * 税金拆分
     */
    split(2, "否");

    private int type;

    private String value;

    VoucherTaxType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static VoucherTaxType getVoucherTaxType(int type) {
        for (VoucherTaxType taxType : values()) {
            if (taxType.getType() == type) {
                return taxType;
            }
        }
        return VoucherTaxType.split;
    }

    public static String getConfigName() {
        return "taxMerged";
    }
}
