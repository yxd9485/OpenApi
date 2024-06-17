package com.fenbeitong.openapi.plugin.func.virtualcard.constant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @ClassName CardStatusEnum
 * @Description 卡状态
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/18 下午1:42
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum CardStatusEnum {
    OPEN(1,"开卡"),
    NORMAL(2,"启用"),
    DISABLE(3,"禁用"),
    UNBIND(4,"解绑"),
    LOGOUT(5,"注销"),
    STOP_USE(6,"已停用");

    private int status;
    private String statusName;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
