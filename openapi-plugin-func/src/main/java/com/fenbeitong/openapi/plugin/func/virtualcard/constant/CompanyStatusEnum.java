package com.fenbeitong.openapi.plugin.func.virtualcard.constant;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @ClassName EmployeeStatusEnum
 * @Description 企业状态
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/18 下午1:42
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum CompanyStatusEnum {
    ACTIVE(1, "正常合作"),
    PAUSE(2, "停止合作"),
    EXPERIENCE(3, "体验"),
    INACTIVE(4, "禁止"),
    TRIAL(5, "试用");

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
