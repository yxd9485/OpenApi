package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 汽车票详情请求DTO
 * @author zhangpeng
 * @date 2022/3/30 3:04 下午
 */
@Data
public class BusOrderDetailReqDTO {

    private String companyId;

    @NotBlank(message = "订单id[order_id]不可为空")
    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("ticket_id")
    private String ticketId;

}
