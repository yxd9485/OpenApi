package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

/**
 * <p>Title: FinanceProjectMappingDto</p>
 * <p>Description: 项目映射</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 11:48 AM
 */
@Data
public class FinanceProjectMappingDto {

    private String id;

    /**
     * 分贝通项目id
     */
    private String costCenterId;

    /**
     * 分贝通项目编码
     */
    private String costCenterCode;

    /**
     * 分贝通项目名称
     */
    private String costCenterName;

    /**
     * 财务项目编码
     */
    private String financeCostCenterCode;

    /**
     * 财务项目名称
     */
    private String financeCostCenterName;

    private Integer status;

    private String createTime;
}
