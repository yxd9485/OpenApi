package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.constant;

/**
 * @ClassName PayableTypeEnum
 * @Description 其他应付单类型
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/15 上午10:17
 **/
public enum PayableTypeEnum {

    BUSINESS_TYPE(1,"商务消费"),
    PERSONAL_TYPE(2,"个人消费");


    private Integer spendingType;
    private String  typeName;

    public Integer getSpendingType() {
        return spendingType;
    }

    public void setSpendingType(Integer spendingType) {
        this.spendingType = spendingType;
    }

    @Override
    public String toString() {
        return "PayableTypeEnum{" +
                "spendingType=" + spendingType +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    PayableTypeEnum(Integer spendingType, String typeName) {
        this.spendingType = spendingType;
        this.typeName = typeName;
    }
}
