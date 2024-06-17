package com.fenbeitong.openapi.plugin.func.organization.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName OpenQueryLegalEntityResDTO
 * @Description 查询法人主体信息返回
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/4/15 下午3:47
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenQueryLegalEntityResDTO {
    @JsonProperty("total_count")
    private Integer totalCount;
    @JsonProperty("total_pages")
    private Integer totalPages;
    @JsonProperty("page_index")
    private Integer pageIndex;
    @JsonProperty("page_size")
    private Integer pageSize;
    /**
     * 三方法人实体信息列表
     */
    private List<OpenCreateLegalEntityReqDTO> entities;
}
