package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.noc.api.service.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class BankOrderDetaiDTO extends BaseModel {

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 交易商户名
     */
    private String shopName;

    /**
     * II类账户银行卡号
     */
    private String bankAccountNo;

    /**
     * 订单总价
     */
    private BigDecimal totalPrice;

    /**
     * 订单采购价
     */
    private BigDecimal costPrice;

    /**
     * 公司支付总金额
     */
    private BigDecimal companyTotalPay;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 供应商ID
     */
    private Integer supplierId;

    /**
     * 用户是否标记 0 否  1是
     */
    private Integer userMark;

    /**
     * 退款时，存入正向订单号
     */
    private String fbOrderId;

    /**
     * com.fenbeitong.finhub.common.constant.TransactionTypeEnum
     * 交易类型 1消费  2退款
     */
    private Integer transactionType;

    /**
     * 订单来源
     */
    private Integer orderChannel;

    /**
     * @see com.fenbeitong.finhub.common.constant.CategoryTypeEnum
     * 场景类型(现有场景标识)
     */
    private Integer categoryType;

    /**
     * 订单类型1因公2因私
     */
    private Integer accountType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 预定时间
     */
    private Date reserveTime;

    /**
     * 订单分贝支付时间
     */
    private Date payTime;

    /**
     * 完成时间
     */
    private Date completeTime;

    /**
     * 员工(下单人)id
     */
    private String userId;

    /**
     * 员工(下单人)部门id
     */
    private String unitId;

    /**
     * 用户名(预定人)
     */
    private String userName;

    /**
     * 用户手机号(预定人)
     */
    private String userPhone;

    private String companyId;

    /**
     * 银行交易时间
     */
    private Date bankTransTime;

    /**
     * 银行消费流水号
     */
    private String bankTransNo;

    /**
     * 收银台流水号
     */
    private String fbCashierTxnId;

    /**
     * 分贝支付回调通知路由
     */
    private String fbPayNotifyUrl;

    /**
     * 支付账户类型
     */
    private Integer accountSubType;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * @see com.fenbeitong.finhub.common.constant.BankHuPoTxnType
     * 银行通知交易业务类型1消费2退款3冲正4撤销冲正6充值7提现
     */
    private Integer bankHupoTransType;

    /**
     * 原银行交易流水号
     */
    private String bankOriTransNo;

    /**
     * 月份标识
     */
    private String monthType;

    /**
     * 扩展字段1
     */
    private String sysExt1;

    /**
     *    1正常显示， 2不显示
     */
    private Integer showType;

    /**
     * 核销状态
     */
    private Integer checkStatus;

    /**
     * 核销状态变更时间
     */
    private Date checkTime;

    /**
     * 是否有替票
     */
    private Integer replaceTag;

    /**
     * 标记时间
     */
    private Date userMarkTime;

    /**
     * 正向单关联退款标识 0无退款，1部分退款，2全额退款
     */
    private Integer refundStatus;

    /**
     * 发票总金额
     */
    private BigDecimal totalInvoicePrice;

    /**
     * 付款单号
     */
    private String paymentId;

    /**
     * 电子回单状态
     */
    private Integer electronicStatus;

    /**
     * 交易失败原因
     */
    private String failDesc;

    /**
     * 收款方名称
     */
    private String receiverName;

    /**
     * 收款方开户行
     */
    private String receiverBank;

    /**
     * 收款方账户
     */
    private String receiverAccount;

    /**
     * 对公支付业务模式枚举
     */
    private Integer publicModel;

    /**
     * 发票地址
     */
    private String costImageUrl;

    /**
     * 扩展字段2
     */
    private String sysExt2;

    /**
     * 扩展字段3
     */
    private String sysExt3;

    /**
     * 扩展字段4
     */
    private String sysExt4;

    private Integer businessMode;

    /**
     * 主订单号
     */
    private String rootOrderId;

    /**
     * 还款状态
     */
    private Integer payBackStatus;

    /**
     * 因私支付总金额
     */
    private BigDecimal personalTotalPay;

    /**
     * 三方支付金额
     */
    private BigDecimal thirdPaymentPrice;

    /**
     * 三方支付方式
     */
    private String thirdPaymentChannel;

    /**
     * 超时时间
     */
    private Date expiredTime;
    /**
     * 备用金ID
     */
    private String pettyId;

    /**
     * 付款用途
     */
    private String paymentPurpose;

    /**
     * 费用归属信息
     */
    @JsonProperty("cost_info")
    private List<OrderSaasInfoDTO.CostAttribution> costInfo;
    /**
     * 三方信息
     */
    @JsonProperty("third_info")
    private OrderThirdInfoDTO thirdInfo;
}
