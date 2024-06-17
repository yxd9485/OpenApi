package com.fenbeitong.openapi.plugin.ecology.v8.sipai.constant;

/**
 * <p>Title: SipaiWorkFlowName</p>
 * <p>Description: 思派工作流名称</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/3 5:45 PM
 */
public enum SipaiWorkFlowName {

    /**
     * 加班申请
     */
    OVER_TIME_APPLY("员工加班申请", "OVER_TIME_APPLY"),

    /**
     * 出差申请
     */
    TRIP_APPLY("员工差旅申请", "TRIP_APPLY");

    private String type;

    private String value;

    SipaiWorkFlowName(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

}
