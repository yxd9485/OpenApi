package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ApplyTripDetailReqDTO {
    @Valid
    @NotNull(message = "审批单id[apply_id]不可为空")
    @JsonProperty("apply_id")
    private String applyId; //审批单id

    @Valid
    @NotNull(message = "审批单类型[apply_type]不可为空")
    @JsonProperty("apply_type")
    private Integer applyType; //审批单类型 0为分贝ID 1为三方ID
}