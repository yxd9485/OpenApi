package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ClassName CostAttributionDTO
 * @Description 费用归属信息
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/7 下午10:29
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CostAttributionDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("weight")
    private BigDecimal weight;
    @JsonProperty("amount")
    private BigDecimal amount;
    //费用归属三方id
    @JsonProperty("third_id")
    private String thirdId;
    //费用归属项目code
    @JsonProperty("cost_attribution_code")
    private String costAttributionCode;

}
