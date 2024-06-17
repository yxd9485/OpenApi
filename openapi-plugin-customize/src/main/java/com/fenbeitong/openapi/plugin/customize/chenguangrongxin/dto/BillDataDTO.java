package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.func.order.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BillDataDTO
 * @Description 简道云账单
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/9/19 上午11:05
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDataDTO extends BaseDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entry<T> {
        private T value;
    }
    /**
     * 业务线
     */
    @JsonProperty( "jdy_order_category")
    private BillDataDTO.Entry<Integer> orderCategory;
    /**
     * 订单号/交易编号
     */
    @JsonProperty( "jdy_order_id")
    private BillDataDTO.Entry<String> orderId;
    /**
     * 主订单号/付款单号
     */
    @JsonProperty( "jdy_root_order_id")
    private BillDataDTO.Entry<String> rootOrderId;
    /**
     * 预订/退票/下单日期时间/付款时间
     */
    @JsonProperty( "jdy_order_create_time")
    private BillDataDTO.Entry<String> orderCreateTime;
    /**
     * 票号/取票号
     */
    @JsonProperty( "jdy_ticket_number")
    private BillDataDTO.Entry<String> ticketNumber;

    /**
     * 机票分类/用车服务商/其他订单业务类别
     */
    @JsonProperty( "jdy_order_category_type")
    private BillDataDTO.Entry<String> orderCategoryType;

    /**
     * 机票/订单/支付状态
     */
    @JsonProperty( "jdy_order_status")
    private BillDataDTO.Entry<String> orderStatus;

    /**
     * 预订/下单/用餐人/付款人姓名
     */
    @JsonProperty( "jdy_payer_name")
    private BillDataDTO.Entry<String> payerName;

    /**
     * 预订/下单/用餐人手机号/付款人手机号
     */
    @JsonProperty( "jdy_payer_phone")
    private BillDataDTO.Entry<String> payerPhone;

    /**
     * 预订/下单/用餐人直属部门/付款人部门
     */
    @JsonProperty( "jdy_payer_dept")
    private BillDataDTO.Entry<String> payerDept;

    /**
     * 预订/下单人层级部门/付款人层级部门
     */
    @JsonProperty( "jdy_payer_dept_all_path")
    private BillDataDTO.Entry<String> payerDeptAllPath;
    /**
     * 预订人/下单人/付款人法人主体
     */
    @JsonProperty( "jdy_payer_custom_field1")
    private BillDataDTO.Entry<String> payerCustomField1;
    /**
     * 预订人/下单人/付款人自定义字段2
     */
    @JsonProperty( "jdy_payer_custom_field2")
    private BillDataDTO.Entry<String> payerCustomField2;
    /**
     * 预订人/下单人/付款人自定义字段3
     */
    @JsonProperty( "jdy_payer_custom_field3")
    private BillDataDTO.Entry<String> payerCustomField3;
    /**
     * 旅客/乘车人/使用人/发件人/交易人姓名/付款单创建人
     */
    @JsonProperty( "jdy_user_name")
    private BillDataDTO.Entry<String> userName;
    /**
     * 旅客/乘车人/发件人手机号/使用人/交易人手机号/付款单创建人手机号
     */
    @JsonProperty( "jdy_user_phone")
    private BillDataDTO.Entry<String> userPhone;

    /**
     * 旅客直属部门/乘车人部门/使用人直属部门/付款单创建人部门
     */
    @JsonProperty( "jdy_user_dept")
    private BillDataDTO.Entry<String> userDept;
    /**
     * 旅客/乘车人层级部门/使用人层级部门/付款单创建人层级部门
     */
    @JsonProperty( "jdy_user_dept_all_path")
    private BillDataDTO.Entry<String> userDeptAllPath;
    /**
     * 旅客/乘车人/使用人/交易人/付款单创建人法人主体
     */
    @JsonProperty( "jdy_user_custom_field1")
    private BillDataDTO.Entry<String> userCustomField1;
    /**
     * 旅客/乘车人/使用人/交易人/付款单创建人自定义字段2
     */
    @JsonProperty( "jdy_user_custom_field2")
    private BillDataDTO.Entry<String> userCustomField2;
    /**
     * 旅客/乘车人/使用人/交易人/付款单创建人自定义字段3
     */
    @JsonProperty( "jdy_user_custom_field3")
    private BillDataDTO.Entry<String> userCustomField3;
    /**
     * 申请事由
     */
    @JsonProperty( "jdy_public_payment_reason")
    private BillDataDTO.Entry<String> publicPaymentReason;
    /**
     * 同住人
     */
    @JsonProperty( "jdy_inmate_name")
    private BillDataDTO.Entry<String> inmateName;
    /**
     * 同住人手机号
     */
    @JsonProperty( "jdy_inmate_phone")
    private BillDataDTO.Entry<String> inmatePhone;
    /**
     * 同住人直属部门
     */
    @JsonProperty( "jdy_inmate_dept")
    private BillDataDTO.Entry<String> inmateDept;
    /**
     * 同住人层级部门
     */
    @JsonProperty( "jdy_inmate_dept_all_path")
    private BillDataDTO.Entry<String> inmateDeptAllPath;
    /**
     * 同住人法人主体
     */
    @JsonProperty( "jdy_inmate_custom_field1")
    private BillDataDTO.Entry<String> inmateCustomField1;
    /**
     * 同住人自定义字段2
     */
    @JsonProperty( "jdy_inmate_custom_field2")
    private BillDataDTO.Entry<String> inmateCustomField2;
    /**
     * 同住人自定义字段3
     */
    @JsonProperty( "jdy_inmate_custom_field3")
    private BillDataDTO.Entry<String> inmateCustomField3;
    /**
     * 行程/用车类型/主门店/商家名称/供应商名称/业务名称
     */
    @JsonProperty( "jdy_trip_name")
    private BillDataDTO.Entry<String> tripName;
    /**
     * 火车/机票行程/分门店名/被保人/收件人姓名/收款方开户行
     */
    @JsonProperty( "jdy_trip_info")
    private BillDataDTO.Entry<String> tripInfo;
    /**
     * 航班号/车次/服务类型/门店地址/收件人手机号/收款方帐号
     */
    @JsonProperty( "jdy_trip_number")
    private BillDataDTO.Entry<String> tripNumber;
    /**
     * 出发城市/下单城市
     */
    @JsonProperty( "jdy_start_city_name")
    private BillDataDTO.Entry<String> startCityName;
    /**
     * 出发车站/出发地/发件地址
     */
    @JsonProperty( "jdy_start_adress_name")
    private BillDataDTO.Entry<String> startAdressName;
    /**
     * 到达城市/入住城市/目的城市/用餐城市
     */
    @JsonProperty( "jdy_end_city_name")
    private BillDataDTO.Entry<String> endCityName;
    /**
     * 到达车站/目的地/门店地址/收件地址
     */
    @JsonProperty( "jdy_end_adress_name")
    private BillDataDTO.Entry<String> endAdressName;
    /**
     * 起飞/入住/出发/行程开始时间
     */
    @JsonProperty( "jdy_start_time")
    private BillDataDTO.Entry<String> startTime;

    /**
     * 到达/离店/支付/完成时间
     */
    @JsonProperty( "jdy_end_time")
    private BillDataDTO.Entry<String> endTime;
    /**
     * 机票折扣
     */
    @JsonProperty( "jdy_air_discount")
    private BillDataDTO.Entry<String> airDiscount;
    /**
     * 舱位等级/坐席/产品类目/业务描述/付款账户
     */
    @JsonProperty( "jdy_seat_type")
    private BillDataDTO.Entry<String> seatType;
    /**
     * 座位编号/商品名称/付款账号
     */
    @JsonProperty( "jdy_goods_name")
    private BillDataDTO.Entry<String> goodsName;
    /**
     * 间夜数/商品数量/用餐人数
     */
    @JsonProperty( "jdy_goods_number")
    private BillDataDTO.Entry<String> goodsNumber;
    /**
     * 票销售价/平均客房单价/车票单价/商品单价
     */
    @JsonProperty( "jdy_goods_price")
    private BillDataDTO.Entry<String> goodsPrice;
    /**
     * 机建费(国内)
     */
    @JsonProperty( "jdy_airport_fee")
    private BillDataDTO.Entry<String> airportFee;
    /**
     * 燃油费(国内)
     */
    @JsonProperty( "jdy_fuel_fee")
    private BillDataDTO.Entry<String> fuelFee;
    /**
     * 用车金额/采购销售总价
     */
    @JsonProperty( "jdy_taxi_price")
    private BillDataDTO.Entry<String> taxiPrice;
    /**
     * 商品状态
     */
    @JsonProperty( "jdy_goods_type")
    private BillDataDTO.Entry<String> goodsType;
    /**
     * 保险费/运费
     */
    @JsonProperty( "jdy_insurance_fee")
    private BillDataDTO.Entry<String> insuranceFee;
    /**
     * 改签差价
     */
    @JsonProperty( "jdy_rebook_diff_price")
    private BillDataDTO.Entry<String> rebookDiffPrice;
    /**
     * 改签费/改签手续费
     */
    @JsonProperty( "jdy_rebook_fee")
    private BillDataDTO.Entry<String> rebookFee;
    /**
     * 升舱费/火车抢票费/用车调度费/汽车票服务费
     */
    @JsonProperty( "jdy_upgrade_fee")
    private BillDataDTO.Entry<String> upgradeFee;
    /**
     * 票价差额退款
     */
    @JsonProperty( "jdy_refund_diff_price")
    private BillDataDTO.Entry<String> refundDiffPrice;
    /**
     * 退票费
     */
    @JsonProperty( "jdy_refund_price")
    private BillDataDTO.Entry<String> refundPrice;
    /**
     * 其他退款
     */
    @JsonProperty( "jdy_bus_refund_price")
    private BillDataDTO.Entry<String> busRefundPrice;
    /**
     * 优惠券/折扣
     */
    @JsonProperty( "jdy_discount")
    private BillDataDTO.Entry<String> discount;
    /**
     * 采购商品总价
     */
    @JsonProperty( "jdy_total_price")
    private BillDataDTO.Entry<String> totalPrice;
    /**
     * 采购商品退款金额
     */
    @JsonProperty( "jdy_mall_refund_price")
    private BillDataDTO.Entry<String> mallRefundPrice;
    /**
     * 销售总价/订单实付/交易金额/对公付款支付金额
     */
    @JsonProperty( "jdy_actual_price")
    private BillDataDTO.Entry<String> actualPrice;
    /**
     * 减免金额
     */
    @JsonProperty( "jdy_reduction_price")
    private BillDataDTO.Entry<String> reductionPrice;
    /**
     * 红包券支付
     */
    @JsonProperty( "jdy_coupon_price")
    private BillDataDTO.Entry<String> couponPrice;
    /**
     * 第三方个人支付
     */
    @JsonProperty( "jdy_personal_price")
    private BillDataDTO.Entry<String> personalPrice;
    /**
     * 分贝币支付
     */
    @JsonProperty( "jdy_fbb_price")
    private BillDataDTO.Entry<String> fbbPrice;
    /**
     * 企业支付
     */
    @JsonProperty( "jdy_company_price")
    private BillDataDTO.Entry<String> companyPrice;
    /**
     * 服务费
     */
    @JsonProperty( "jdy_service_fee")
    private BillDataDTO.Entry<String> serviceFee;
    /**
     * 应还款总金额
     */
    @JsonProperty( "jdy_repayment_price")
    private BillDataDTO.Entry<String> repaymentPrice;
    /**
     * 企业支付总金额
     */
    @JsonProperty( "jdy_company_total_price")
    private BillDataDTO.Entry<String> companyTotalPrice;
    /**
     * 扣款账户
     */
    @JsonProperty( "jdy_payment_account")
    private BillDataDTO.Entry<String> paymentAccount;
    /**
     * 账户类型
     */
    @JsonProperty( "jdy_account_type")
    private BillDataDTO.Entry<String> accountType;
    /**
     * 事由
     */
    @JsonProperty( "jdy_reason")
    private BillDataDTO.Entry<String> reason;
    /**
     * 费用归属类型 部门和项目2选1
     */
    @JsonProperty( "jdy_cost_attribution_category1")
    private BillDataDTO.Entry<String> costAttributionCategory;

    /**
     * 费用归属名称
     */
    @JsonProperty( "jdy_cost_attribution_name1")
    private BillDataDTO.Entry<String> costAttributionName;
    /**
     * 费用类别
     */
    @JsonProperty( "jdy_cost_category")
    private BillDataDTO.Entry<String> costCategory;
    /**
     * 费用归属部门全路径
     */
    @JsonProperty( "jdy_cost_attribution_all_path")
    private BillDataDTO.Entry<String> jcostAttributionAllPath;
    /**
     * 费用归属项目编号
     */
    @JsonProperty( "jdy_project_code")
    private BillDataDTO.Entry<String> projectCode;
    /**
     * 是否需要行程审批
     */
    @JsonProperty( "jdy_trip_apply")
    private BillDataDTO.Entry<String> tripApply;
    /**
     * 行程审批单号
     */
    @JsonProperty( "jdy_trip_apply_id")
    private BillDataDTO.Entry<String> tripApplyId;
    /**
     * 是否超规
     */
    @JsonProperty( "jdy_exceed_buy_apply")
    private BillDataDTO.Entry<String> exceedBuyApply;
    /**
     * 超规理由
     */
    @JsonProperty( "jdy_exceed_buy_info")
    private BillDataDTO.Entry<String> exceedBuyInfo;
    /**
     * 是否需要订单审批
     */
    @JsonProperty( "jdy_order_apply")
    private BillDataDTO.Entry<String> orderApply;
    /**
     * 订单审批单号
     */
    @JsonProperty( "jdy_order_apply_id")
    private BillDataDTO.Entry<String> orderApplyId;
    /**
     * 是否需要退改控制
     */
    @JsonProperty( "jdy_re_apply")
    private BillDataDTO.Entry<String> jdyReApply;
    /**
     * 退改审批理由
     */
    @JsonProperty( "jdy_re_apply_reason")
    private BillDataDTO.Entry<String> reApplyReason;
    /**
     * 退改审批单号
     */
    @JsonProperty( "jdy_re_apply_id")
    private BillDataDTO.Entry<String> reApplyId;
    /**
     * 项目
     */
    @JsonProperty( "jdy_public_payment_project")
    private BillDataDTO.Entry<String> publicPaymentProject;
    /**
     * 用途
     */
    @JsonProperty( "jdy_public_payment_use")
    private BillDataDTO.Entry<String> publicPaymentUse;
    /**
     * 自定义字段1
     */
    @JsonProperty( "jdy_order_custom_field1")
    private BillDataDTO.Entry<String> orderCustomField1;
    /**
     * 自定义字段2
     */
    @JsonProperty( "jdy_order_custom_field2")
    private BillDataDTO.Entry<String> orderCustomField2;
    /**
     * 自定义字段3
     */
    @JsonProperty( "jdy_order_custom_field3")
    private BillDataDTO.Entry<String> orderCustomField3;
}
