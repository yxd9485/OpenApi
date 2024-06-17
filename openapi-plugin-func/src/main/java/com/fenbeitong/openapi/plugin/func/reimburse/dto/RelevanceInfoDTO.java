package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RelevanceInfoDTO
 * @Description 报销单信息
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/7 下午10:14
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelevanceInfoDTO {
    @JsonProperty("apply_id")
    private String applyId;
    @JsonProperty("type")
    private Integer type;
}
