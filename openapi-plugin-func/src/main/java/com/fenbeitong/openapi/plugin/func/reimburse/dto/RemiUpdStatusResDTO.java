package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RemiUpdStatusResDTO
 * @Description 批量更新报销单状态返回
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/8 下午9:54
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemiUpdStatusResDTO {
    @JsonProperty("reimb_id")
    private String  reimbId;
    @JsonProperty("error_msg")
    private String errorMsg;
}
