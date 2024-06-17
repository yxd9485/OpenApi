package com.fenbeitong.openapi.plugin.dingtalk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: DingtalkTripKitTravelTimeDTO<p>
 * <p>Description: 差旅审批套件出差时间控件DTO<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/5/16 20:11
 */
@Data
public class DingtalkTripKitTravelTimeDTO {
    /**
     * 开始时间时段类型：1：上午 2：下午
     */
    @JsonProperty("startDay")
    private Integer startDayType;

    /**
     * 开始时间时段类型：1：上午 2：下午
     */
    @JsonProperty("endDay")
    private Integer endDayType;
    /**
     * 开始时间 yyyy-MM-dd
     */
    @JsonProperty("startTime")
    private String startTime;
    /**
     * 结束时间 yyyy-MM-dd
     */
    @JsonProperty("endTime")
    private String endTime;

}
