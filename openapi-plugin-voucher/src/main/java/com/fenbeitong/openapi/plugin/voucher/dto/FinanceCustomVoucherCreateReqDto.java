package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceCustomVoucherCreateReqDto</p>
 * <p>Description: 自定义账单生成</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/24 10:27 AM
 */
@Data
public class FinanceCustomVoucherCreateReqDto {

    /**
     * 公司id
     */
    private String companyId;

    /**
     * 账单编号
     */
    private String billNo;

    /**
     * 年
     */
    private int year;

    /**
     * 月
     */
    private int month;

    /**
     * 人员类型 0:预订人 1:实际使用人
     */
    private int employeeType;

    /**
     * 部门类型 0:下单人部门;1:使用人部门;2:费用归属部门
     */
    private int departmentType;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * excel导出配置id
     */
    private Long excelConfigId;

    /**
     * 是否删除账单数据
     */
    private Boolean deleteBill;
}
