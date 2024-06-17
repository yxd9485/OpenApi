package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderTypeValidAnnotation;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: ExpressDeliveryOrderListQueryReq</p>
 * <p>Description: 快递订单列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/3 7:59 PM
 */
@Data
public class ExpressDeliveryOrderListReqDTO {

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;


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
     * 发送人的人手机号：文本框输入，精确匹配
     */
    @JsonProperty("sender_phone")
    private String senderPhone;

    /**
     * 接收人的人手机号：文本框输入，精确匹配
     */
    @JsonProperty("receiver_phone")
    private String receiverPhone;

    /**
     * 订单状态：
     * WAIT_PAY(1, "待支付", "待支付", "#FB4646"),
     * PAID(2, "已支付", "已支付"),
     * PAID_NOTICE_ERROR(21, "订单发货失败", "发券失败"),
     * DONE(80, "已完成", "已完成","#26CE77"),
     * CLOSE(81, "已关闭", "已关闭"),
     * CANCEL_NO_PAY(82, "未支付取消", "已取消"),
     */
    @JsonProperty("status_list")
    private List<Integer> statusList;

    /**
     * 起始页
     */
    @JsonProperty("page_index")
    private Integer pageIndex;

    /**
     * 每页显示的条数
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * 订单开始时间 yyyy-mm-dd
     */
    @JsonProperty("create_time_begin")
    private String createTimeBegin;

    /**
     * 订单结束时间 yyyy-mm-dd
     */
    @JsonProperty("create_time_end")
    private String createTimeEnd;

    /**
     * 订单接收的开始时间 yyyy-mm-dd
     */
    @JsonProperty("receiver_time_begin")
    private String receiverTimeBegin;

    /**
     * 订单接收的结束时间 yyyy-mm-dd
     */
    @JsonProperty("receiver_time_end")
    private String receiverTimeEnd;

    /**
     * 订单类别：1：因公  2：因私
     */
    @JsonProperty("order_type")
    @FuncOrderTypeValidAnnotation(message = "订单类型【order_type】仅支持【1：因公，2：因私】，请检查参数!")
    private Integer orderType=1;
}
