package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>Title: TrainOrderDetailReqDTO</p>
 * <p>Description: 火车订单详情请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/22 11:57 AM
 */
@Data
public class TrainOrderDetailReqDTO {

    /**
     * 订单编号：文本框输入，精确匹配
     */
    @NotBlank(message = "订单id[order_id]不可为空")
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 票号：文本框输入，精确匹配
     */
    @NotBlank(message = "车票id[ticket_id]不可为空")
    @JsonProperty("ticket_id")
    private String ticketId;

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;

}

