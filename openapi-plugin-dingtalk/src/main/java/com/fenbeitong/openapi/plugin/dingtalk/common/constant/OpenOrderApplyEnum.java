package com.fenbeitong.openapi.plugin.dingtalk.common.constant;

/**
 * <p>Title: OpenOrderApplyEnum</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-10-21 17:06
 */
public enum OpenOrderApplyEnum {

    /**
     * 未知时间结果类型
     */
    REMARK_DETAIL("remarkDetail", "详细事由详情"),
    REMARK_REASON("remarkReason", "申请事由"),
    ORDER_PERSON("orderPerson", "申请人"),
    ORDER_TYPE("orderType", "场景类型"),
    GUEST_NAME("guestName", "使用人"),
    BEGIN_DATE("beginDate", "开始日期"),
    END_DATE("endDate", "结束日期"),
    DEPARTURE_NAME("departureName", "出发地"),
    DESTINATION_NAME("destinationName", "目的地"),
    ORDER_PRICE("orderPrice", "订单金额"),

    CHANGE_TYPE("changeType", "审批单类型"),
    REFUND_REASON("refundReason", "退订事由"),
    CHANGE_REASON("changeReason", "改签事由"),
    ORDER_CHANGE_FEE("orderChangeFee", "改签手续费"),
    OLD_THIRD_APPLY("oldThirdApply", "关联审批单"),
    OLD_ORDER_INFO("oldOrderInfo", "原订单信息"),
    ORDER_INFO("orderInfo", "改签订单信息");


    /**
     * Code
     */
    private String code;
    /**
     * Message
     */
    private String msg;

    OpenOrderApplyEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String code() {
        return this.code;
    }

    public String msg() {
        return this.msg;
    }


}
