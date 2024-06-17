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
 * Created by hanshuqi on 2020/07/05.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeApplyTripReqDTO {
    /**
     * 公司ID
     */
    @NotBlank(message = "公司ID[corp_id]不可为空")
    @JsonProperty("corp_id")
    private String corpId;
    /**
     * trip_id,根据ID匹配交通工具类型
     */
    @NotBlank(message = "trip_id,根据ID匹配交通工具类型[trip_id]不可为空")
    @JsonProperty("trip_id")
    private String tripId;
    /**
     * 交通工具类型
     */
    @NotBlank(message = "交通工具类型[trip_type]不可为空")
    @JsonProperty("trip_type")
    private String tripType;
    /**
     * 交通工具名称
     */
    @NotBlank(message = "交通工具名称[trip_name]不可为空")
    @JsonProperty("trip_name")
    private String tripName;
    /**
     * 状态是否可用,0:可用，1:不可用
     */
    @NotBlank(message = "状态是否可用,0:可用，1:不可用[trip_status]不可为空")
    @JsonProperty("trip_status")
    private String tripStatus;
}
