package com.fenbeitong.openapi.plugin.func.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单费用分摊DTO，订单侧resp
 * create on 2022-04-25 12:47:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCostDetailDTO {

    private Integer amount;

    private String costCategory;

    private String costCategoryId;

    private String id;

    private List<Costattributiongrouplist> costAttributionGroupList;

    /**
     * create on 2022-04-25 12:47:31
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Costattributionlist {

        private BigDecimal price;

        private String name;

        private BigDecimal weight;

        private String id;


    }

    /**
     * create on 2022-04-25 12:47:31
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Costattributiongrouplist {

        private List<Costattributionlist> costAttributionList;

        private Integer category;

        private Integer costAttributionRange;

        private String categoryName;

        private String recordId;

    }
}
