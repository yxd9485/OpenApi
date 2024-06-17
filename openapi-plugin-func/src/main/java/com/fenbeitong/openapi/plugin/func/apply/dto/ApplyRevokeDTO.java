package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName ApplyRepulseDTO
 * @Description 三方新审批终审撤回
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/12 下午3:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyRevokeDTO {
    @JsonProperty("category")
    @NotNull(message = "审批场景类型[category]不可为空")
    private String category;
    @NotBlank(message = "三方审批单id[third_id]不可为空")
    @JsonProperty("third_id")
    private String thirdId;
    @NotBlank(message = "分贝通申请单id[apply_id]不可为空")
    @JsonProperty("apply_id")
    private String applyId;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("employee_id")
    private String employeeId;
    @JsonProperty("employee_type")
    private String employeeType;
}
