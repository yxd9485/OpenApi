package com.fenbeitong.openapi.plugin.func.order.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PersonalConsumeFlowQuery {

    private Integer pageIndex = 1;
    //条数 默认20
    private Integer pageSize = 20;

    private String companyId;

    @NotNull(message = "账单编号[billNo]不可为空")
    private String billNo;

    private Boolean isQueryGrantVoucherTask = false;

    private String billId;

    public PersonalConsumeFlowQuery() {
    }

    public PersonalConsumeFlowQuery(String companyId, String billNo, Boolean isQueryGrantVoucherTask, String billId) {
        this.companyId = companyId;
        this.billNo = billNo;
        this.isQueryGrantVoucherTask = isQueryGrantVoucherTask;
        this.billId = billId;
    }

}
