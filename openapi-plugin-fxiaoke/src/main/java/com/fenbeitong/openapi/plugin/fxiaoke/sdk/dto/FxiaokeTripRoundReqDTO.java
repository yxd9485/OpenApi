package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by hanshuqi on 2020/07/06.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeTripRoundReqDTO {
    /**
     * 公司ID
     */
    @NotBlank(message = "公司ID[corp_id]不可为空")
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * 单程往返标识
     */
    @NotNull(message = "单程往返标识[round_trip]不可为空")
    @JsonProperty("round_trip")
    private Integer roundTrip;
    /**
     * 往返名称
     */
    @NotBlank(message = "往返名称[trip_name]不可为空")
    @JsonProperty("trip_name")
    private String tripName;
    /**
     * 是否可用
     */
    @NotBlank(message = "是否可用[state]不可为空")
    @JsonProperty("state")
    private String state;
    /**
     * 纷享销客ID
     */
    @NotBlank(message = "纷享销客ID[trip_id]不可为空")
    @JsonProperty("trip_id")
    private String tripId;
}
