package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Title: FinanceConfigDto</p>
 * <p>Description: 财务配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 5:19 PM
 */
@Data
public class FinanceConfigDto {

    @JsonProperty("dept_first")
    private Integer deptFirst;

    private Integer personnel;

    /**
     * 服务费单独核算
     */
    @JsonProperty("service_fee")
    private Integer serviceFee;

    /**
     * 外部人员不抵扣 0 关闭; 1:开启
     */
    @JsonProperty("outsider_deduction")
    private Integer outsiderDeduction;

    /**
     * 借方科目高级映射配置开关：0关，1开
     */
    @JsonProperty("advanced_mapping_config")
    private Integer advancedMappingConfig;

    /**
     * 优先匹配字段：1事由，2项目
     */
    @JsonProperty("priority_match")
    private Integer priorityMatch;
}
