package com.fenbeitong.openapi.plugin.customize.zhiou.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @ClassName ApplyTripDTO
 * @Description 行程列表
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/6
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyTripDTO {

    /**
     * 业务类型
     */
    @JsonProperty("type")
    private Integer type;

    /**
     * 出发时间
     */
    @JsonProperty("start_time")
    private String startTime;
    /**
     * 结束时间
     */
    @JsonProperty("end_time")
    private String endTime;
    /**
     * 城市id
     */
    @JsonProperty("start_city_id")
    private String startCityId;
    /**
     * 城市名称
     */
    @JsonProperty("start_city_name")
    private String startCityName;
    /**
     * 目的城市id
     */
    @JsonProperty("arrival_city_id")
    private String arrivalCityId;
    /**
     * 目的城市名称
     */
    @JsonProperty("arrival_city_name")
    private String arrivalCityName;

    /**
     * 业务类型
     */
    @JsonProperty("estimated_amount")
    private Integer estimatedAmount;
}
