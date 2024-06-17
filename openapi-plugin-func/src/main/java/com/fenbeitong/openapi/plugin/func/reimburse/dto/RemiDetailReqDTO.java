package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName RemiDetailDTO
 * @Description 报销单详情
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/16 下午9:26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemiDetailReqDTO {
    /**
     * 付款状态
     */
    @JsonProperty("payment_status")
    private Integer paymentStatus;
    /**
     * 开始时间
     */
    @JsonProperty("start_time")
    private String startTime;

    /**
     * 结束时间
     */
    @JsonProperty("end_time")
    private String endTime;
    /**
     * 页码
     */
    @JsonProperty("page_index")
    private Integer pageIndex;
    /**
     * 条数
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 支付开始时间
     */
    @JsonProperty("payment_start_time")
    private String paymentStartTime;

    /**
     * 支付结束时间
     */
    @JsonProperty("payment_end_time")
    private String paymentEndTime;


    /**
     * 审批结束开始时间
     */
    @JsonProperty("final_approve_start_time")
    private String finalApproveStartTime;
    /**
     * 审批结束结束时间
     */
    @JsonProperty("final_approve_end_time")
    private String finalApproveEndTime;
}
