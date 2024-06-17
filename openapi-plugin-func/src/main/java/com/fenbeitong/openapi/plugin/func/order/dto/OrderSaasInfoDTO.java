package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenCostAttributionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: OrderSaasInfoDTO</p>
 * <p>Description: 订单管控信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/2 5:04 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSaasInfoDTO {

    private List<CostAttribution> costAttributionList;

    private String remark;

    @JsonProperty("during_apply_id")
    private String duringApplyId;

    @JsonProperty("apply_id")
    private String applyId;

    @JsonProperty("is_exceed")
    private Boolean isExceed;

    @JsonProperty("exceed_item")
    private String exceedItem;

    @JsonProperty("exceed_reason")
    private String exceedReason;

    @JsonProperty("cost_category")
    private String costCategory;

    @JsonProperty("cost_category_code")
    private String costCategoryCode;

    @JsonProperty("cost_attributions")
    private List<OpenCostAttributionDTO> costAttributionDTOList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CostAttribution {

        @JsonProperty("cost_attribution_category")
        private Integer costAttributionCategory;

        @JsonProperty("cost_attribution_id")
        private String costAttributionId;

        @JsonProperty("cost_attribution_name")
        private String costAttributionName;

        @JsonProperty("cost_attribution_custom_ext")
        private Object costAttributionCustomExt;
    }

}
