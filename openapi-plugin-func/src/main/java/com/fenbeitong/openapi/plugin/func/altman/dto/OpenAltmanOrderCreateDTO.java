package com.fenbeitong.openapi.plugin.func.altman.dto;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;


/**
 * Created by xiaowei on 2020/05/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenAltmanOrderCreateDTO {

    @NotEmpty(message = "供应商的订单号不能为空")
    private String supplier_order_id;

    private String origin_order_id;

    //1为固定值 表示已完成状态的订单，后面可以添加
    @NotEmpty(message = "订单的状态不能为空")
    private String order_status;

    //订单的类型0为正常订单1为改价订单
    @NotEmpty(message = "订单的类型不能为空")
    private String order_type;

    @NotNull(message = "订单的总价格不能为空")
    private BigDecimal total_price;

    private BigDecimal pay_price;
    private BigDecimal cost_price;
    private BigDecimal total_discount;
    private BigDecimal company_total_pay;
    private BigDecimal personal_total_pay;

    private String book_user_phone;
    private String book_user_name;
    private String book_department;

    @Valid
    @NotEmpty(message = "消费者的数据不能为空")
    private List<Consumer> consumers;

    @NotEmpty(message = "下单时间不能为空")
    private String operator_order_time;

    @NotEmpty(message = "开始时间不能为空")
    private String start_time;

    @NotEmpty(message = "结束时间不能为空")
    private String end_time;

    @NotEmpty(message = "出发地不能为空")
    private String start_destination;

    @NotEmpty(message = "到达地不能为空")
    private String end_destination;

    @NotEmpty(message = "订单支付时间不能为空")
    private String order_pay_time;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Consumer {

        @NotEmpty(message = "消费者的名字不能为空")
        private String consumer_name;
        @NotEmpty(message = "消费者的手机号不能为空")
        private String consumer_phone;
        private String consumer_department;
    }

}
