package com.fenbeitong.openapi.plugin.yiduijie.constant;

/**
 * <p>Title: ExternalPersonTaxReduce</p>
 * <p>Description: 外部人员是否抵扣</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 5:35 PM
 */
public enum ExternalPersonTaxReduce {

    /**
     * 1外部人员不抵扣
     */
    no_reduce(1, "是"),
    /**
     * 参与抵扣
     */
    reduce(2, "否");

    private int type;

    private String value;

    ExternalPersonTaxReduce(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static ExternalPersonTaxReduce getExternalPersonTaxReduce(int type) {
        for (ExternalPersonTaxReduce personTaxReduce : values()) {
            if (personTaxReduce.getType() == type) {
                return personTaxReduce;
            }
        }
        return null;
    }

    public static String getConfigName() {
        return "taxReduceExclude;externalPerson";
    }
}
