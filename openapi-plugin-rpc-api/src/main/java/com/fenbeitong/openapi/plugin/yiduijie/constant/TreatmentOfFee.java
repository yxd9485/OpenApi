package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: TreatmentOfFee</p>
 * <p>Description: 服务单独核算</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 4:04 PM
 */
public enum TreatmentOfFee {

    /**
     * 服务不单独核算
     */
    no(0, "否"),

    /**
     * 服务单独核算
     */
    yes(1, "是");

    private int type;

    private String value;

    TreatmentOfFee(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static TreatmentOfFee getTreatmentOfFee(int type) {
        for (TreatmentOfFee source : values()) {
            if (source.getType() == type) {
                return source;
            }
        }
        return null;
    }

    public static String getConfigName() {
        return "treatmentOfFee";
    }
}
