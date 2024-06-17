package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public enum VehicleEnum {

    TRAIN("火车", 0),
    AIR("飞机", 1),
    TAXI("出租车", 2),
    BUS("长途汽车", 3),
    SUBWAY("公交/地铁", 4);

    private String desc;
    private int type;

    VehicleEnum(String desc, int type) {
        this.type = type;
        this.desc = desc;
    }

    public static VehicleEnum getType(String desc) {
        for (VehicleEnum source : values()) {
            if (desc.equals(source.getDesc())) {
                return source;
            }
        }
        return null;
    }
}
