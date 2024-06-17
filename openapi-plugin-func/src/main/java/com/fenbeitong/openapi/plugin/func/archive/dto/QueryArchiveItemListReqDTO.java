package com.fenbeitong.openapi.plugin.func.archive.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QueryArchiveItemListReqDTO
 * @Description 查询自定义档案项目列表
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/11/2 上午11:50
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryArchiveItemListReqDTO {
    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("state")
    private Integer state;
    @JsonProperty("archive_id")
    private String archiveId;
}
