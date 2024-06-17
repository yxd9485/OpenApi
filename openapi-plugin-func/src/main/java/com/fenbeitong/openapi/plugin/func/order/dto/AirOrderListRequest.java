package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: AirOrderListRequest</p>
 * <p>Description: 机票订单参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/18 11:18 AM
 */
@Data
public class AirOrderListRequest {

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
     * 审批单id
     */
    private String applyId;

    /**
     * 预定人ID
     */
    private String bookingPersonId;

}
