package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName FuncRemiUpdStatusDTO
 * @Description 报销单状态批量更新为已修改
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/16 下午9:58
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemiUpdStatusDTO {
    /**
     * 报销单单号
     */
    @NotBlank(message = "报销单id[reimb_id]不可为空")
    @JsonProperty("reimb_id")
    private String reimbId;
}
