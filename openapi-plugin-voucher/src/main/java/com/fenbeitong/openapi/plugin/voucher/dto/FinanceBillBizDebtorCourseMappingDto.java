package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceBillBizDebtorCourseMappingDto</p>
 * <p>Description: 账单业务线借方科目映射</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 4:41 PM
 */
@Data
public class FinanceBillBizDebtorCourseMappingDto {

    private String groupId;

    /**
     * 部门名称
     */
    private String orgUnitName;

    /**
     * 业务线
     */
    private String bizName;

    /**
     * 1:事由;2;财务项目编号
     */
    private Integer fieldInfoType;

    /**
     * 事由/财务项目编号
     */
    private String fieldInfoName;

    /**
     * 科目名称
     */
    private String courseName;

}
