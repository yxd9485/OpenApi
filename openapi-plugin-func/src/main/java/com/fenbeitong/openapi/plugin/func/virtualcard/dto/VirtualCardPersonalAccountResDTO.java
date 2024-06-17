package com.fenbeitong.openapi.plugin.func.virtualcard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ClassName VirtualCardPersonalAccountResDTo
 * @Description 虚拟卡个人账户信息查询返回
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/22 下午10:38
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualCardPersonalAccountResDTO {

    /**
     * 企业分贝通ID
     */
    @JsonProperty("company_id")
    private String companyId;
    /**
     * 员工分贝通ID
     */
    @JsonProperty("employee_id")
    private String employeeId;
    /**
     * 员工姓名
     */
    @JsonProperty("employee_name")
    private String employeeName;
    /**
     * 员工三方ID
     */
    @JsonProperty("third_employee_id")
    private String thirdEmployeeId;
    /**
     * 员工手机号
     */
    @JsonProperty("employee_phone")
    private String employeePhone;
    /**
     * 员工所属部门
     */
    @JsonProperty("org_unit_name")
    private String orgUnitName;
    /**
     * 银行卡号
     */
    @JsonProperty("bank_account_no")
    private String bankAccountNo;
    /**
     * 卡状态
     * @see com.fenbeitong.fenbeipay.api.constant.enums.bank.BankCardStatus
     */
    @JsonProperty("card_status")
    private Integer cardStatus;
    /**
     * 开户行
     */
    @JsonProperty("bank_name")
    private String bankName;
    /**
     * 虚拟卡可用额度
     */
    @JsonProperty("card_balance")
    private BigDecimal cardBalance;
    /**
     * 企业三方ID
     */
    @JsonProperty("third_company_id")
    private String thirdCompanyId;

}
