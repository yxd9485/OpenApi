package com.fenbeitong.openapi.plugin.func.virtualcard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ClassName VirtualCardPersonalDetailReqDTO
 * @Description 虚拟卡个人消费
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/21 下午10:02
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualCardPersonalDetailResDTO {

    @JsonProperty("order_info")
    private VirtualCardOrderInfoDTO orderInfo;
    //费用归属、
    @JsonProperty("cost_info")
    private List<VirtualCardCostDTO> costInfo;
}
