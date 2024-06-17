package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by hanshuqi on 2020/07/06.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeTripRoundDTO {
    /**
     * 公司ID
     */
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 单程往返标识
     */
    @JsonProperty("round_trip")
    private Integer roundTrip;
    /**
     * 往返名称
     */
    @JsonProperty("trip_name")
    private String tripName;
    /**
     * 是否可用
     */
    @JsonProperty("state")
    private String state;
    /**
     * 纷享销客ID
     */
    @JsonProperty("trip_id")
    private String tripId;
}
