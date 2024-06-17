package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * <p>Title: FuncCustformApplyDetailReqDTO<p>
 * <p>Description: 查询自定义申请单详情请求参数<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/2/14 10:15 AM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FuncCustformApplyDetailReqDTO {
    /**
     * 申请单号
     */
    @JsonProperty("apply_id")
    @NotBlank(message = "分贝通申请单ID不能为空")
    private String applyId;
}
