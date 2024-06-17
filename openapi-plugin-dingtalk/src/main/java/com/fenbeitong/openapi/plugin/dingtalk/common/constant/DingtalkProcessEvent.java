package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * @author zhaokechun
 * @date 2018/12/5 15:41
 */
public enum DingtalkProcessEvent {

    /**
     * 审批创建
     */
    START("start"),
    /**
     * 审批结束
     */
    FINISH("finish");

    String value;

    DingtalkProcessEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
