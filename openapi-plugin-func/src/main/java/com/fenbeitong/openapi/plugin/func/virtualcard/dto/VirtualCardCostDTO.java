package com.fenbeitong.openapi.plugin.func.virtualcard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName VirtualCardCostDTO
 * @Description 虚拟卡消费费用归属
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/22 上午10:18
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualCardCostDTO {
    /**
     * 费用归属id
     */
    @JsonProperty("cost_attribution_id")
    private String costAttributionId;

    /**
     * 归属类型
     */
    @JsonProperty("cost_attribution_type")
    private Integer costAttributionType;

    /**
     * 费用归属名称
     */
    @JsonProperty("cost_attribution_name")
    private String costAttributionName;

    /**
     * 父级费用归属{"pids":"","pnames":""}
     */
    @JsonProperty("p_cost_attribution")
    private String pCostAttribution;

    /**
     * 自定义字段（json）
     */
    @JsonProperty("custom_ext")
    private String customExt;

}
