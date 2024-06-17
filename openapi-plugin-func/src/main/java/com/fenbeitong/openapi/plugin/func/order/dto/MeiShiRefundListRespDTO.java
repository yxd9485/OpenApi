package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: MeiShiRefundListRespDTO</p>
 * <p>Description: 美食退款列表响应对象</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/1 8:18 PM
 */
@Data
public class MeiShiRefundListRespDTO {

    @JsonProperty("results")
    private List refundList;

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
}
