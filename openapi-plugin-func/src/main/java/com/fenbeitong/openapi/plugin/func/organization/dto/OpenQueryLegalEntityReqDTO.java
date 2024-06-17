package com.fenbeitong.openapi.plugin.func.organization.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName OpenQueryLegalEntityReqDTO
 * @Description 批量查询法人详情信息
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/4/13 下午7:48
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenQueryLegalEntityReqDTO {

    @JsonProperty("page_index")
    private Integer pageIndex;

    @JsonProperty("page_size")
    private Integer pageSize;
}
