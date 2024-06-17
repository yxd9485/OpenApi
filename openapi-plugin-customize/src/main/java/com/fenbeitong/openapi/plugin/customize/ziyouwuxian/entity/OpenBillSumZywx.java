package com.fenbeitong.openapi.plugin.customize.ziyouwuxian.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wanghaoqiang on 2021/06/16.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_bill_sum_zywx")
public class OpenBillSumZywx {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 账单编号
     */
    @Column(name = "BILL_NO")
    private String billNo;

    /**
     * 项目编号
     */
    @Column(name = "ITEM_NO")
    private String itemNo;

    /**
     * 项目名称
     */
    @Column(name = "ITEM_NAME")
    private String itemName;

    /**
     * 客户经理
     */
    @Column(name = "CUSTOMER_MANAGER")
    private String customerManager;

    /**
     * 公司Id
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

    /**
     * 火车票退票改签保险费用
     */
    @Column(name = "TRAIN_REFUND_INSURANCE_CHANGE_SUM")
    private BigDecimal trainRefundInsuranceChangeSum;

    /**
     * 火车票服务费
     */
    @Column(name = "TRAIN_SERVICE_FEE")
    private BigDecimal trainServiceFee;

    /**
     * 火车票价
     */
    @Column(name = "TRAIN_TICKET_PRICE")
    private BigDecimal trainTicketPrice;

    /**
     * 酒店总费用
     */
    @Column(name = "HOTEL_TOTAL_PRICE")
    private BigDecimal hotelTotalPrice;

    /**
     * 机票票价
     */
    @Column(name = "AIR_TICKET_PRICE")
    private BigDecimal airTicketPrice;

    /**
     * 机票服务费
     */
    @Column(name = "AIR_SERVICE_FEE")
    private BigDecimal airServiceFee;

    /**
     * 机票退票改签保险费用
     */
    @Column(name = "AIR_REFUND_INSURANCE_CHANGE_SUM")
    private BigDecimal airRefundInsuranceChangeSum;

    /**
     * 用餐+外卖的总费用
     */
    @Column(name = "DINNER_TOTAL_PRICE")
    private BigDecimal dinnerTotalPrice;

    /**
     * 用车总费用
     */
    @Column(name = "CAR_TOTAL_PRICE")
    private BigDecimal carTotalPrice;

    /**
     * 闪送总费用
     */
    @Column(name = "SHANSONG_TOTAL_PRICE")
    private BigDecimal shansongTotalPrice;

    /**
     * 各场景总费用
     */
    @Column(name = "SUM_TOTAL_PRICE")
    private BigDecimal sumTotalPrice;

    /**
     * 万能订单总费用
     */
    @Column(name = "ALTMAN_TOTAL_PRICE")
    private BigDecimal altmanTotalPrice;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     *
     */
    @Column(name = "is_ding")
    private Integer isDing;

    /**
     * 采购总费用
     */
    @Column(name = "MALL_TOTAL_PRICE")
    private BigDecimal mallTotalPrice;

}
