package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 审批详情
 *
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FuncCompanyApplyDetailReqDTO {
    /**
     * 申请单号
     */
    @JsonProperty("apply_id")
    private String applyId;
    /**
     * 三方申请单号
     */
    @JsonProperty("third_apply_id")
    private String thirdApplyId;
}
