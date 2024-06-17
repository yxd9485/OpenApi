package com.fenbeitong.openapi.plugin.yiduijie.model.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>Title: BillConfigTaxCalcDTO</p>
 * <p>Description: 企业账单进项税规则配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/10 5:20 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillConfigTaxCalcDTO implements Serializable {

    /**
     * 业务线名称
     */
    @NotBlank(message = "业务线名称[businessType]不可为空")
    private String businessType;

    /**
     * 是否计算进项税
     */
    @NotNull(message = "是否计算进项税[calcTax]不可为空")
    private Boolean calcTax;

    /**
     * 税率
     */
    private Integer taxRate;

    /**
     * 分贝通供应商编码
     */
    private String fbtSupplierCode;
}
