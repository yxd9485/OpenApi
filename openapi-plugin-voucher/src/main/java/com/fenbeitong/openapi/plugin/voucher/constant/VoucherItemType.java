package com.fenbeitong.openapi.plugin.voucher.constant;

/**
 * <p>Title: VoucherItemType</p>
 * <p>Description: 凭证分录类型</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/26 5:20 PM
 */
public enum VoucherItemType {
    /**
     * 1:业务线借方;
     */
    DEBIT(1, "业务线借方"),

    /**
     * 2:业务线进项税;
     */
    TAX_DEBIT(2, "业务线进项税"),

    /**
     * 3:服务费借方;
     */
    SERVICE_DEBIT(3, "服务费借方"),

    /**
     * 4:服务费进项税;
     */
    SERVICE_TAX_DEBIT(4, "服务费进项税"),

    /**
     * 5:贷方科目
     */
    CREDIT(5, "贷方科目");

    private int type;

    private String typeName;

    VoucherItemType(int type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public int getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public static VoucherItemType getVoucherItemType(int type) {
        for (VoucherItemType voucherItemType : values()) {
            if (voucherItemType.getType() == type) {
                return voucherItemType;
            }
        }
        return VoucherItemType.DEBIT;
    }
}
