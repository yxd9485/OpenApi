package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: VoucherCreateType</p>
 * <p>Description: 凭证生成方式</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 7:07 PM
 */
public enum VoucherCreateType {

    /**
     * 对接财务系统
     */
    direct(1, "direct"),

    /**
     * excel导出
     */
    excel(2, "excel");

    private int type;

    private String value;

    VoucherCreateType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static VoucherCreateType getVoucherCreateType(int type) {
        for (VoucherCreateType createType : values()) {
            if (createType.getType() == type) {
                return createType;
            }
        }
        return VoucherCreateType.excel;
    }

    public static String getConfigName() {
        return "mode";
    }
}
