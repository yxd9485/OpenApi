package com.fenbeitong.openapi.plugin.func.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.noc.api.service.base.BasePageReqDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BankCheckQueryReqDTO extends BasePageReqDTO {
    /**
     * 申请人
     * 否
     */
    private String proposer;

    /**
     * 申请单号
     * 否
     */
    private String apply_order_id;


    /**
     * 提交开始时间
     * 否
     */
    private String start_time;


    /**
     * 提交结束时间
     * 否
     */
    private String end_time;

    /**
     * 审批状态 -1.全部 2.待审核 4.已同意 16.已拒绝 64.撤回
     * 是
     */
    private Integer state;

    /**
     * 部门名称
     * 否
     */
    private String department;

    /**
     * 凭证状态 -1.全部 1.已生成 2.未生成 3.生成失败 4.生成中
     * 是
     */
    private String voucherStatus;

    /**
     * 回票状态 -1:全部 0:未回票 1:已回票 2:部分回票
     * 是
     */
    private String returnTicket;

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

    private String companyId;
}
