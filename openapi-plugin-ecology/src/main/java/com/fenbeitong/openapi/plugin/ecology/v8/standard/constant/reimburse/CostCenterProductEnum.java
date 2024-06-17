package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public enum CostCenterProductEnum {

    COMMON_CHANNEL("共用产品(待分摊)", 0),
    OFFLINE("大有", 1),
    SHANGCHAO("憨憨", 2),
    HUO_GUO("火锅", 3),
    SHAOKAO("烧烤", 4),
    SHENGXIAN("生鲜", 5),
    LUWEI("卤味", 6),
    XICAN("西餐", 7);

    private String desc;
    private int type;

    CostCenterProductEnum(String desc, int type) {
        this.type = type;
        this.desc = desc;
    }

    public static CostCenterProductEnum getType(String desc) {
        for (CostCenterProductEnum source : values()) {
            if (desc.equals(source.getDesc())) {
                return source;
            }
        }
        return null;
    }
}
