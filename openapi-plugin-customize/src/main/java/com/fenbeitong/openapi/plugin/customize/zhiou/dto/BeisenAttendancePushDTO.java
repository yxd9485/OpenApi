package com.fenbeitong.openapi.plugin.customize.zhiou.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BeisenAttendanceDTO
 * @Description 北森考勤推送参数
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/4
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeisenAttendancePushDTO {
    /**
     * 员工姓名
     */
    @JsonProperty("StaffName")
    private String staffName;
    /**
     * 员工邮箱
     */
    @JsonProperty("StaffEmail")
    private String staffEmail;
    /**
     * 北森UserId
     */
    @JsonProperty("StaffId")
    private String staffId;
    /**
     * 开始时间
     */
    @JsonProperty("StartDateTime")
    private String startDateTime;
    /**
     * 结束时间
     */
    @JsonProperty("StopDateTime")
    private String stopDateTime;
    /**
     * 交通工具
     */
    @JsonProperty("BusinessVehicle")
    private String businessVehicle;
    /**
     * 出发地
     */
    @JsonProperty("DeparturePlace")
    private String departurePlace;
    /**
     * 目的地
     */
    @JsonProperty("Destination")
    private String destination;
    /**
     * 工作地址
     */
    @JsonProperty("Address")
    private String address;
    /**
     * 备注
     */
    @JsonProperty("Remark")
    private String remark;
    /**
     * 出差事由
     */
    @JsonProperty("Reason")
    private String reason;
    /**
     * 同行人三方id
     */
    @JsonProperty("TogetherStaffIds")
    private String[] togetherStaffIds;
    /**
     * 同行人邮箱
     */
    @JsonProperty("TogetherStaffEmails")
    private String[] togetherStaffEmails;
}
