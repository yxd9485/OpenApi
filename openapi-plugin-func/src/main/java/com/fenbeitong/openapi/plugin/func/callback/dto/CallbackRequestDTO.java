package com.fenbeitong.openapi.plugin.func.callback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import lombok.Data;

/**
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
@Data
public class CallbackRequestDTO {

    @JsonProperty("apply_id")
    private String applyId;

    @JsonProperty("third_id")
    private String thirdId;
}
