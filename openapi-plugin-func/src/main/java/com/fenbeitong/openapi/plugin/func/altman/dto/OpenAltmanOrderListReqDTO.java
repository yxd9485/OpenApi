package com.fenbeitong.openapi.plugin.func.altman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created by xiaowei on 2020/05/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenAltmanOrderListReqDTO {

    //@NotEmpty(message = "公司Id不能为空")
    //private String company_id;

    //@NotNull(message = "起始页码不能为空")
    private int page_index;

    //订单的类型0为正常订单1为改价订单
    //@NotNull(message = "每页条数不能为空")
    private int page_size;

    private String consumer_phone;

    private String consumer_name;

    //orderId 分贝通自己的订单Id
    private String order_id;

    private String create_time_begin;

    private String create_time_end;

    private String company_id;

    /**
     * 订单类型（具体场景）
     */
    @JsonProperty("order_type")
    @FuncOrderTypeValidAnnotation(message = "订单类型【order_type】仅支持【1：因公，2：因私】，请检查参数!")
    private Integer orderType=1;
}
