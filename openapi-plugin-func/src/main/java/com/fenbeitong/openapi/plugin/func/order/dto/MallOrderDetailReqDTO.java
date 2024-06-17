package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>Title: MallOrderDetailReqDTO</p>
 * <p>Description: 采购订单详情请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/15 4:57 PM
 */
@Data
public class MallOrderDetailReqDTO {

    /**
     * 订单编号：文本框输入，精确匹配
     */
    @NotBlank(message = "订单id[order_id]不可为空")
    @JsonProperty("order_id")
    private String orderId;
}
