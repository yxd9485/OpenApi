package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.constant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowTypeEnum
 * @Description
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/7/1 上午11:32
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum FlowTypeEnum {

    OPEN_YQ_TRAEL("出差",16),
    OPEN_YQ_GO_OUT("外出", 5);
    private String name;
    private int flowTypeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlowTypeId() {
        return flowTypeId;
    }

    public void setFlowTypeId(int flowTypeId) {
        this.flowTypeId = flowTypeId;
    }
}
