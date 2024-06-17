package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * <p>Title: BaseOrderListRespDTO</p>
 * <p>Description: 列表响应对象</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/1 8:18 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseOrderListRespDTO {

    @JsonProperty("results")
    private List results;

    /**
     * 总记录数
     */
    @JsonProperty("total_count")
    private Integer totalCount;

    /**
     * 总页数
     */
    @JsonProperty("total_pages")
    private Integer totalPages;

    /**
     * 起始页
     */
    @JsonProperty("page_index")
    private Integer pageIndex;

    /**
     * 每页显示的条数
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    public Integer getTotalPages() {
        return (totalCount + pageSize -1) / pageSize;
    }
}
