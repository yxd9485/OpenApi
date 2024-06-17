package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public enum CostCenterChannelEnum {

    COMMON_CHANNEL("共用渠道(待分摊)", 0),
    OFFLINE("线下门店", 1),
    SHANGCHAO("商超（物美）", 2),
    APP("APP/小程序", 3),
    PLATFORM("电商平台", 4),
    SHANGCHAO_OTHER("商超（其他）", 5),
    DEALER("经销商", 6);

    private String desc;
    private int type;

    CostCenterChannelEnum(String desc, int type) {
        this.type = type;
        this.desc = desc;
    }

    public static CostCenterChannelEnum getType(String desc) {
        for (CostCenterChannelEnum source : values()) {
            if (desc.equals(source.getDesc())) {
                return source;
            }
        }
        return null;
    }
}
