package com.fenbeitong.openapi.plugin.kingdee.common.enums;

public enum KingDeeK3CloudEnum {
    FNUMBER("FNUMBER", "编号"),
    FSTAFFID("FSTAFFID", "内码ID"),
    FISFIRSTPOST("FIsFirstPost", "第一部门"),
    F_QDQX_CWAudit("F_QDQX_CWAudit", "会计内码");


    private final String key;
    private final String value;

    KingDeeK3CloudEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
