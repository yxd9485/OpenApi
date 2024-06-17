package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * module: 非行程审批单xiangf<br/>
 * <p>
 * description: 非行程审批列表响应实体<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/9/14 13:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "非行程审批列表响应实体")
public class ApplyNoTravelResDTO {

    @JsonProperty("totalCount")
    private Integer totalCount;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("page_index")
    private Integer pageIndex;

    @JsonProperty("page_size")
    private Integer pageSize;

    private List<ResultData> results;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResultData {

        @JsonProperty("apply_id")
        private String applyId;

        @JsonProperty("third_id")
        private String thirdId;

        private String phone;

        private String proposer;

        @JsonProperty("create_time")
        private String createTime;

        private Integer type;

        @JsonProperty("apply_order_type_name")
        private String applyOrderTypeName;

        private Integer state;

        private String budget;

        @JsonProperty("travel_day")
        private String travelDay;

    }


}
