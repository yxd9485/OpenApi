package com.fenbeitong.openapi.plugin.func.altman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by xiaowei on 2020/05/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenAltmanOrderConfigReqDTO {
    /**
     * 公司ID
     */
    @NotBlank(message = "公司ID[company_id]不可为空")
    @JsonProperty("company_id")
    private String companyId;
    /**
     * 场景类型air/car/hotel/train/mall/takeaway
     */
    @NotBlank(message = "场景类型air/car/hotel/train/mall/takeaway[scence_type]不可为空")
    @JsonProperty("scence_type")
    private String scenceType;
    /**
     * 分贝业务类型1 pop 2 托管 3 采销
     */
    @NotNull(message = "分贝业务类型1 pop 2 托管 3 采销[business_type]不可为空")
    @JsonProperty("business_type")
    private Integer businessType;
    /**
     * 发票提供的状态1、提供 2、不提供
     */
    @JsonProperty("invoic_provide_status")
    private Integer invoicProvideStatus;
    /**
     * 开票的类型 1、专票 2、普票/电子票 27、企业配置
     */
    @JsonProperty("scene_invoice_type")
    private Integer sceneInvoiceType;
    /**
     * 开票方 1、遵循开票规则 2、回填所选供应商名称
     */
    @JsonProperty("invoice_provide_type")
    private Integer invoiceProvideType;
    /**
     * 开票方名称 / 遵循开票规则 / 回填所选供应商名称
     */
    @JsonProperty("invoice_provide_name")
    private String invoiceProvideName;
    /**
     * 发票提供者
     */
    @JsonProperty("invoice_provider")
    private String invoiceProvider;
}
