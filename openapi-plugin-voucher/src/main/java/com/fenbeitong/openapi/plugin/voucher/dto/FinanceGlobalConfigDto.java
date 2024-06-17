package com.fenbeitong.openapi.plugin.voucher.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: FinanceGlobalConfigDto</p>
 * <p>Description: 财务全局配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/29 3:03 PM
 */
@SuppressWarnings("all")
@Data
public class FinanceGlobalConfigDto {

    private String operatorId;

    private String operatorName;

    private FinanceConfigDto config_1;

    private List<FinanceProjectMappingDto> config_2;

    private List<FinanceDeptMappingDto> config_3;

    private List<FinanceCourseDto> config_4;

    private List<FinanceBillBizDebtorCourseMappingDto> config_5;

    private List<VirtualCardDebtorCourseMappingDto> config_6;

    private Map<String, FinanceCourseDto> config_7;

    private List<FinanceBillTaxRuleDto> config_8;

    private FinanceBillExcludeTaxDto config_9;

    private List<VirtualCardTaxRateDto> config_10;

    private List<VirtualCardDeductionTypeDto> config_11;

    private FinanceVoucherManageDto config_12;

    private Map extConfig;
}
