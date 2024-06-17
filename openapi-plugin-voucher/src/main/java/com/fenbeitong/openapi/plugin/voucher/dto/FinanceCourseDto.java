package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceCourseDto</p>
 * <p>Description: 科目清单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 4:18 PM
 */
@Data
public class FinanceCourseDto {

    private String id;

    private String companyId;

    /**
     * 科目id
     */
    private String courseId;

    /**
     * 科目名称
     */
    private String courseName;

    /**
     * 科目编码
     */
    private String courseCode;

    /**
     * 人员核算
     */
    private Boolean employeeAccounting;

    /**
     * 部门核算
     */
    private Boolean departmentAccounting;

    /**
     * 项目核算
     */
    private Boolean projectAccounting;

    /**
     * 供应商核算
     */
    private Boolean supplierAccounting;

    /**
     * 科目类型
     */
    private Integer courseType;

    private Integer state;

    private Integer used;
}
