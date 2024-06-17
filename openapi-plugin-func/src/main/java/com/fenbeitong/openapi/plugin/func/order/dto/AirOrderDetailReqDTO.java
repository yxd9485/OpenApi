package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>Title: AirOrderDetailReqDTO</p>
 * <p>Description: 机票订单详情请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/15 4:57 PM
 */
@Data
public class AirOrderDetailReqDTO {

    /**
     * 公司id
     */
    private String companyId;

    /**
     * 订单编号：文本框输入，精确匹配
     */
    @NotBlank(message = "订单id[order_id]不可为空")
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 票id：文本框输入，精确匹配
     */
    @NotBlank(message = "票id[ticket_id]不可为空")
    @JsonProperty("ticket_id")
    private String ticketId;

    /**
     * 是否国际机票：文本框输入，精确匹配
     */
    @NotNull(message = "是否国际机票[is_intl]不可为空")
    @JsonProperty("is_intl")
    private Boolean isIntl;
}
