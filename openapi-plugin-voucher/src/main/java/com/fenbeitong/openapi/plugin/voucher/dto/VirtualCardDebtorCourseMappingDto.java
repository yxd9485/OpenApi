package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: VirtualCardDebtorCourseMappingDto</p>
 * <p>Description: 虚拟卡核销单借方科目映射</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 5:11 PM
 */
@Data
public class VirtualCardDebtorCourseMappingDto {

    /**
     *单据类别 1:虚拟卡核销单;2:对公付款单
     */
    private Integer billType;

    private String groupId;

    /**
     * 财务部门
     */
    private String orgUnitName;

    /**
     * 费用类别
     */
    private String costCategory;

    /**
     * 科目名称
     */
    private String courseName;
}
