package com.fenbeitong.openapi.plugin.qiqi.constant;

import java.util.Arrays;

/**
 *
 * @author helu
 * @date 2022/5/16 下午4:46
 * 性别
 */
public enum GenderEnum {
    MALE(1,"Gender.male"),
    FEMALE(2,"Gender.female"),
    UNKNOW(3,"Gender.unknow");

    private Integer type;
    private String desc;

    GenderEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static GenderEnum getEnumByType(String desc){
        return Arrays.stream(GenderEnum.values())
            .filter(genderEnum -> genderEnum.desc.equals(desc))
            .findFirst()
            .orElse(GenderEnum.UNKNOW);
    }
}
