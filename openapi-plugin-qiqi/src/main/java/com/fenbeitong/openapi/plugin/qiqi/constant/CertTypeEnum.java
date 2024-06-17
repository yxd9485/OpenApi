package com.fenbeitong.openapi.plugin.qiqi.constant;

import java.util.Arrays;

/**
 *
 * @author helu
 * @date 2022/5/16 下午4:46
 * 证件类型
 */
public enum CertTypeEnum {
    IDCARD(1,"IdType.idcard"),
    PASSPORT(2,"IdType.passport"),
    OTHER(3,"IdType.other");

    private Integer type;
    private String desc;

    CertTypeEnum(Integer type, String desc) {
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

    public static CertTypeEnum getEnumByType(String  desc){
        return Arrays.stream(CertTypeEnum.values())
            .filter(certTypeEnum -> certTypeEnum.desc.equals(desc))
            .findFirst()
            .orElse(CertTypeEnum.OTHER);
    }
}
