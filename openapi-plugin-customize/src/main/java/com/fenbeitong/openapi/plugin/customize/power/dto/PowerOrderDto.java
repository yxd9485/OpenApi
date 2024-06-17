package com.fenbeitong.openapi.plugin.customize.power.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author zhangjindong
 */
@Data
public class PowerOrderDto {

    @JsonProperty("order_info")
    Map<String,Object> orderInfo;

    @JsonProperty("third_info")
    Map<String,Object> thirdInfo;

    @JsonProperty("price_info")
    Map<String,Object> priceInfo;


}
