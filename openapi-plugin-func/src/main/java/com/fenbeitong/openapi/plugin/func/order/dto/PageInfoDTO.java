package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: PageInfoDTO</p>
 * <p>Description: 分页信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/1 8:17 PM
 */
@Data
public class PageInfoDTO {

    @JsonProperty("current_page")
    private Integer currentPage;

    @JsonProperty("page_size")
    private Integer pageSize;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("total_size")
    private Integer totalSize;
}
