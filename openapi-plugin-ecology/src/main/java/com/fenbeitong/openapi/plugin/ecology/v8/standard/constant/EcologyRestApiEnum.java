package com.fenbeitong.openapi.plugin.ecology.v8.standard.constant;

/**
 * 泛微rest接口调用类型
 * Created by zhangpeng on 2021/12/31.
 */
public enum EcologyRestApiEnum {

    /**
     * 获取审批人
     */
    GET_APPROVE_USER("1", "获取审批人"),
    /**
     * 流程退回
     */
    REFUND("2", "流程退回"),
    /**
     * 流程删除
     */
    DELETE("3", "流程删除");

    private String type;
    private String desc;

    EcologyRestApiEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
