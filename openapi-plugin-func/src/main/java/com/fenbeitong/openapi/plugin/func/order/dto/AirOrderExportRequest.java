package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 机票订单参数（导出）
 */
@Data
public class AirOrderExportRequest {

    /**
     * 订单id
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * 票状态
     */
    @JsonProperty("ticket_status")
    private Integer ticketStatus;

    /**
     * 供应商id
     */
    @JsonProperty("remote_order_id")
    private String remoteOrderId;

    /**
     * 票号
     */
    @JsonProperty("ticket_no")
    private String ticketNo;

    /**
     * 下单时间从
     */
    @JsonProperty("create_date_from")
    private String createDateFrom;
    /**
     * 下单时间到
     */
    @JsonProperty("create_date_to")
    private String createDateTo;
    /**
     * 出发日期从
     */
    @JsonProperty("starting_date_from")
    private String startingDateFrom;
    /**
     * 出发日期到
     */
    @JsonProperty("starting_date_to")
    private String startingDateTo;
    /**
     * 航班号
     */
    @JsonProperty("flight_no")
    private String flightNo;
    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 费用归属id
     */
    @JsonProperty("cost_attribution_id")
    private String costAttributionId;

    /**
     * 乘机人姓名
     */
    @JsonProperty("passenger_name")
    private String passengerName;
    /**
     * 预订人名称
     */
    @JsonProperty("booking_person_name")
    private String bookingPersonName;
    /**
     * 预订人电话
     */
    @JsonProperty("booking_person_phone")
    private String bookingPersonPhone;
    /**
     * 0：全部；1：国内；2：国际
     */
    @JsonProperty("intl_flag")
    private Integer intlFlag;
    /**
     * 供应商
     */
    @JsonProperty("supplier_id")
    private Integer supplierId;
    /**
     * 页码（不传默认第一页）
     */
    @JsonProperty("page_index")
    private Integer pageIndex;
    /**
     * 每页数据条数（不传默认30条）
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * 预定渠道
     */
    @JsonProperty("channel_id")
    private Integer channelId;

    /**
     * 是否进入过寻价系统
     */
    @JsonProperty("is_track_issued")
    private Integer isTrackIssued;

    /**
     * 费用归属名
     */
    @JsonProperty("cost_attribution")
    private Integer costAttribution;

    /**
     * 订单类别：1：因公  2：因私
     */
    @JsonProperty("order_type")
    private Integer orderType;

    /**
     * 订单状态
     */
    @JsonProperty("order_status")
    private Integer orderStatus;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 业务模式
     */
    private String businessMode;

    /**
     * 审批单id
     */
    private String applyId;

    /**
     * 预定人ID
     */
    private String bookingPersonId;

}
