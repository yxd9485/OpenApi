package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * <p>Title: MeiShiRefundDTO</p>
 * <p>Description: 美食退款订单信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/1 8:21 PM
 */
@Data
public class MeiShiRefundDTO {

    @JsonProperty("refund_order_info")
    private MeiShiRefundInfoDTO refundOrderInfo;

    @JsonProperty("order_info")
    private MeiShiRefundInfoDTO orderInfo;

    @JsonProperty("user_info")
    private OrderUserInfo userInfo;

    @JsonProperty("price_info")
    private OrderPriceInfoDTO priceInfo;

    @JsonProperty("saas_info")
    private OrderSaasInfoDTO saasInfo;

    @JsonProperty("third_info")
    private Map thirdInfo;

}
