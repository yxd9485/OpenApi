package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;


/**
 * Created by xiaowei on 2020/05/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AltmanOrderRefundDetailDTO {

    @NotEmpty(message = "订单号不能为空")
    @JsonProperty("refund_order_id")
    private String refundOrderId;

    @JsonProperty("company_id")
    private String companyId;

}
