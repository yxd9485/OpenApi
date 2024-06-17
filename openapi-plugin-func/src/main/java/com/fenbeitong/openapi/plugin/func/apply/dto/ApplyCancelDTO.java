package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Description  三方审批单作废
 * @Author duhui
 * @Date  2021/11/10
**/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyCancelDTO {
    @NotBlank(message = "三方审批单id[third_id]不可为空")
    @JsonProperty("third_id")
    private String thirdId;
    @JsonProperty("cancel_reason")
    private String cancelReason;
    @JsonProperty("cancel_reason_desc")
    private String cancelReasonDesc;
}
