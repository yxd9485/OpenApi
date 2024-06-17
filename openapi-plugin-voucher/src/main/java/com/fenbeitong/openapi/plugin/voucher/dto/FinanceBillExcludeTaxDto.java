package com.fenbeitong.openapi.plugin.voucher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: FinanceBillExcludeTaxDto</p>
 * <p>Description: 不计税部门及项目</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 8:07 PM
 */
@Data
public class FinanceBillExcludeTaxDto {

    /**
     * 不计税部门
     */
    @JsonProperty("dept_list")
    private List<FinanceBillExcludeTaxDetailDto> deptList;

    /**
     * 不计税项目
     */
    @JsonProperty("cost_center_list")
    private List<FinanceBillExcludeTaxDetailDto> costCenterList;
}
