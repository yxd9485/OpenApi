package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public enum ExpenseTypeEnum {

    TRIP_TYPE("费用报销", 0),
    EXPENSE_TYPE("差旅费报销", 1);

    private String desc;
    private int type;

    ExpenseTypeEnum(String desc, int type) {
        this.type = type;
        this.desc = desc;
    }

    public static ExpenseTypeEnum getType(String desc) {
        for (ExpenseTypeEnum source : values()) {
            if (desc.equals(source.getDesc())) {
                return source;
            }
        }
        return null;
    }
}
