package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: TreatmentOfFee</p>
 * <p>Description: 服务单独核算</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 4:04 PM
 */
public enum MergingOfExpense {

    /**
     * 根据辅助核算自动合并
     */
    merge(1, "是"),

    /**
     * 生成明细
     */
    detail(2, "否");

    private int type;

    private String value;

    MergingOfExpense(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static MergingOfExpense getMergingOfExpense(int type) {
        for (MergingOfExpense source : values()) {
            if (source.getType() == type) {
                return source;
            }
        }
        return null;
    }

    public static String getConfigName() {
        return "mergingOfExpense";
    }
}
