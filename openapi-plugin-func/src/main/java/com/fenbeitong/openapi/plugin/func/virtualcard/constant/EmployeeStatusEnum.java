package com.fenbeitong.openapi.plugin.func.virtualcard.constant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @ClassName EmployeeStatusEnum
 * @Description 用户状态
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/18 下午1:42
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum EmployeeStatusEnum {
    UNKNOWN(0, "未知"),
    ACTIVE(1, "启用"),
    INACTIVE(2, "禁用"),
    PAUSE(3, "暂停"),
    SOFT_DELETE(4, "软删除");

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
