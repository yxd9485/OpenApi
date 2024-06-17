package com.fenbeitong.openapi.plugin.customize.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @ClassName OpenBudgetCostComparison
 * @Description 预算费用对照错误响应
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/6
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComparisonErrorRespDTO {

    /**
     * 费用id
     */
    @JsonProperty("cost_id")
    private String costId;

    /**
     * 错误信息
     */
    @JsonProperty("error_msg")
    private String errorMsg;

}
