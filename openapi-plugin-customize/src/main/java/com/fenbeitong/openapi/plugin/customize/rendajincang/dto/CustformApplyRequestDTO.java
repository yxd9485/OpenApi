package com.fenbeitong.openapi.plugin.customize.rendajincang.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName CustformApplyRequestDTO
 * @Description 自定义申请单参数
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/10/12
 **/
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class CustformApplyRequestDTO {
    /**
     * 申请单状态
     */
    @JsonProperty("apply_state")
    private Integer applyState;
    /**
     * 申请单ID
     */
    @JsonProperty("apply_id")
    private String applyId;
}
