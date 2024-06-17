package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName OpenBillQueryResDTO
 * @Description 账单详情分页查询返回
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/9/6 下午2:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenBillPageInfoResDTO<T> extends BaseDTO {
    @JsonProperty("total_count")
    private Integer count;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("page_index")
    private Integer pageIndex;

    @JsonProperty("page_size")
    private Integer pageSize;

    @JsonProperty("details")
    private List<T> details;
}

