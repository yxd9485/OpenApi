package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName RemiResultDTO
 * @Description 报销单详情查询返回结果
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/8 下午8:30
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemiResultDTO {

    @JsonProperty("reimb_form")
    private List reimbForm;

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
