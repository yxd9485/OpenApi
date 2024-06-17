package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: WawjKqDTO</p>
 * <p>Description: 我爱我家考勤数据-测试用</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/7 3:09 PM
 */
@Data
public class WawjKqDTO {

    @JsonProperty("employee_id")
    private String employeeId;

    public String getmployeeId() {
        return employeeId;
    }

    public void setmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @JsonProperty("employee_name")
    private String employeeName;
    @JsonProperty("work_date")
    private String workDate;
    @JsonProperty("user_check_in_time")
    private String userCheckInTime;
    @JsonProperty("user_check_out_time")
    private String userCheckOutTime;
}
