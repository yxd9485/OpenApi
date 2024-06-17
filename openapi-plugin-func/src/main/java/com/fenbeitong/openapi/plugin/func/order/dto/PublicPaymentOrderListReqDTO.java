package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: PublicPaymentOrderListReqDTO</p>
 * <p>Description: 对公交易订单列表</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/8/31 10:59 AM
 */
@Data
public class PublicPaymentOrderListReqDTO {

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
     * // key=9开头不能再用，9开头用在退款中
     *     WAIT_PAY(1, "已下单", "已下单", "#FB4646"),
     *     PAY(2, "已支付", "交易中", "#FB4646"),
     *     DONE(80, "已完成", "交易成功","#26CE77"),
     *     HAD_FAIL(82,"支付取消","交易失败"),
     *     CANCEL_PAY(83, "冲正", "已取消", "#CCCCCC"),
     */
    @JsonProperty("order_status")
    private String orderStatus;

    /**
     * 人姓名：文本框输入，精确匹配
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * 人手机号：文本框输入，精确匹配
     */
    @JsonProperty("user_phone")
    private String userPhone;

    /**
     * 交易类型 1消费2退款3冲正4撤销冲正 （银行业务类型）
     */
    @JsonProperty("bank_trans_type")
    private String bankHupoTransType;

    /**
     * 卡号
     */
    @JsonProperty("bank_account_no")
    private String bankAccountNo;

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

}
