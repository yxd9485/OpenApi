package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public enum PayTypeEnum {

    APPLY_PAY("申请付款", 0),
    BACKUP_MONEY("核销备用金", 1);

    private String desc;
    private int type;

    PayTypeEnum(String desc, int type) {
        this.type = type;
        this.desc = desc;
    }

    public static PayTypeEnum getPayType(String desc) {
        for (PayTypeEnum source : values()) {
            if (desc.equals(source.getDesc())) {
                return source;
            }
        }
        return null;
    }
}
