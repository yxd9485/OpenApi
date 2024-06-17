package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.reimburse;

import lombok.Getter;

@Getter
@SuppressWarnings("all")
public enum ExpenseLargeTypeEnum {

    OFFLINE("员工团建费/福利费/活动/培训费", 1),
    SHANGCHAO("员工补贴（随工资发放）", 2),
    APP("业务招待费", 3),
    PLATFORM("样品/试吃费用", 4),
    TYPE_5("内部会议费", 5),
    TYPE_6("加盟商培训/交流会议费", 6),
    TYPE_7("广告/营销/推广/活动/招商/补贴费用", 7),
    TYPE_8("房屋租赁/装修/水电费用", 8),
    TYPE_9("公车车辆费", 9),
    TYPE_10("第三方/平台服务费用", 10),
    TYPE_11("车辆费用（公车费用）", 11),
    TYPE_12("办公/耗材费用", 12),
    TYPE_13("其他", 13);

    private String desc;
    private int type;

    ExpenseLargeTypeEnum(String desc, int type) {
        this.type = type;
        this.desc = desc;
    }

    public static ExpenseLargeTypeEnum getType(String desc) {
        for (ExpenseLargeTypeEnum source : values()) {
            if (desc.equals(source.getDesc())) {
                return source;
            }
        }
        return null;
    }
}
