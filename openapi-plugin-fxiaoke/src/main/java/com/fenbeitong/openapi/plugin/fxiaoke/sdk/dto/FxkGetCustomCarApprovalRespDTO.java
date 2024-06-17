package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxkGetCustomCarApprovalRespDTO {

    /**
     * 返回码
     */
    @JsonProperty("errorCode")
    private Integer errorCode;

    /**
     * 对返回码的文本描述内容
     */
    @JsonProperty("errorMessage")
    private String errorMessage;


    @JsonProperty("data")
    private FxkCustomCarApprovalDetail fxkCustomCarApprovalDetail;

}
