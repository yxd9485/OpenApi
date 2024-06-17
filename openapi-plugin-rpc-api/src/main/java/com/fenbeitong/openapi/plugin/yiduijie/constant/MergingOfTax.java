package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: MergingOfTax</p>
 * <p>Description: 合并进项税</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 5:51 PM
 */
public enum  MergingOfTax {

    /**
     * 合并：所有进项税合并到一行
     */
    merge(1, "合并"),

    /**
     * 业务线：按照业务线计算进项税；
     */
    detail(2, "业务线");

    private int type;

    private String value;

    MergingOfTax(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static MergingOfTax getMergingOfTax(int type) {
        for (MergingOfTax source : values()) {
            if (source.getType() == type) {
                return source;
            }
        }
        return null;
    }

    public static String getConfigName() {
        return "mergingOfTax";
    }
}
