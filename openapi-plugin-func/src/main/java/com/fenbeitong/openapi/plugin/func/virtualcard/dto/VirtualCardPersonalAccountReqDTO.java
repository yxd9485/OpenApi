package com.fenbeitong.openapi.plugin.func.virtualcard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName VirtualCardPersonalAccountReqDTo
 * @Description 虚拟卡个人账户信息查询
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/22 下午10:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualCardPersonalAccountReqDTO {
    /**
     * 三方员工ID
     */
    @NotBlank(message="[third_employee_id]不可为空")
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;
    /**
     * 银行简称代码 传code字段
     * @see com.fenbeitong.finhub.common.constant.BankNameEnum
     */
    @JsonProperty("bank_name")
    private String bankName;
    /**
     * 卡状态 传key字段
     * @see com.fenbeitong.fenbeipay.api.constant.enums.bank.BankCardStatus
     */
    @JsonProperty("card_status")
    private Integer cardStatus;
    /**
     * 企业状态 传key字段
     *
     * @see com.fenbeitong.usercenter.api.model.enums.company.CompanyStatus
     */
    @JsonProperty("company_status")
    private Integer companyStatus;
    /**
     * 用户状态 传key字段
     *
     * @see com.fenbeitong.usercenter.api.model.enums.employee.EmployeeStatus
     */
    @JsonProperty("employee_status")
    private Integer employeeStatus;
}
