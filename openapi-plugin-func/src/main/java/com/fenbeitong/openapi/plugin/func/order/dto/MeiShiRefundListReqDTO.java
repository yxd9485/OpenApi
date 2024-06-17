package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>Title: MeiShiRefundListReqDTO</p>
 * <p>Description: 美食退款列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/1 7:59 PM
 */
@Data
public class MeiShiRefundListReqDTO {

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 退款单编号：文本框输入，精确匹配
     */
    @JsonProperty("refund_order_id")
    private String refundOrderId;

    /**
     * 订单编号：文本框输入，精确匹配
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 预订人姓名：文本框输入，精确匹配
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * 预订人手机号：文本框输入，精确匹配
     */
    @JsonProperty("user_phone")
    private String userPhone;

    /**
     * REFUNDING(90, "退款中", "退款中"),
     * SUCCEED(95, "退款完成", "已退款", "#FF8E22"),
     */
    @JsonProperty("refund_status_list")
    private List<Integer> refundStatusList;

    /**
     * 起始页
     */
    @NotNull(message = "起始页[page_index]不可为空")
    @JsonProperty("page_index")
    private Integer pageIndex;

    /**
     * 每页显示的条数
     */
    @NotNull(message = "每页显示的条数[page_size]不可为空")
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * 订单开始时间 yyyy-mm-dd
     */
    @NotBlank(message = "订单创建时间[create_time_begin]不可为空")
    @JsonProperty("create_time_begin")
    private String createTimeBegin;

    /**
     * 订单结束时间 yyyy-mm-dd
     */
    @NotBlank(message = "订单创建时间[create_time_end]不可为空")
    @JsonProperty("create_time_end")
    private String createTimeEnd;

    /**
     * api版本号
     */
    private String apiVersion;

    /**
     * 订单类型（具体场景）
     */
    @JsonProperty("order_type")
    @FuncOrderTypeValidAnnotation(message = "订单类型【order_type】仅支持【1：因公，2：因私】，请检查参数!")
    private Integer orderType=1;
}
