package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TravelTimeDetailDTo
 * @Description 审批详情时间明细
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/11/19 下午4:50
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelTimeDetailDTO {
    @JsonProperty("travel_type")
    private Integer travelType;
    @JsonProperty("travel_time")
    private Integer travelTime;
}
