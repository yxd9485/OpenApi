package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * <p>Title: MeiShiRefundDetailReqDTO</p>
 * <p>Description: 美食退款订单详情请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/2 4:57 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeiShiRefundDetailReqDTO {

    /**
     * 订单编号：文本框输入，精确匹配
     */
    @NotBlank(message = "退款订单id[refund_order_id]不可为空")
    @JsonProperty("refund_order_id")
    private String refundOrderId;

    /**
     * api版本号
     */
    private String apiVersion;
}
