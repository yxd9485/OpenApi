package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by hanshuqi on 2020/07/05.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeApplyTripDTO {
    /**
     * 公司ID
     */
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * trip_id,根据ID匹配交通工具类型
     */
    @JsonProperty("trip_id")
    private String tripId;
    /**
     * 交通工具类型
     */
    @JsonProperty("trip_type")
    private String tripType;
    /**
     * 交通工具名称
     */
    @JsonProperty("trip_name")
    private String tripName;
    /**
     * 状态是否可用,0:可用，1:不可用
     */
    @JsonProperty("trip_status")
    private String tripStatus;
}
