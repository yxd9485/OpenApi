package com.fenbeitong.openapi.plugin.func.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName RemiInvoiceResDTO
 * @Description 报销单发票信息
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/9/20 上午8:37
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemiInvoiceResDTO {
    @JsonProperty("inv_id")
    private String invId;//发票id
    @JsonProperty("inv_type")
    private Integer invType;
    @JsonProperty("inv_type_name")
    private String invTypeName;
    @JsonProperty("inv_code")
    private String invCode;
    @JsonProperty("inv_number")
    private String invNumber;//发票号码
    @JsonProperty("issued_date")
    private String issuedDate;
    @JsonProperty("seller_name")
    private String sellerName;
    @JsonProperty("seller_tax_number")
    private String sellerTaxNumber;//销售方纳税人识别号
    @JsonProperty("buyer_name")
    private String buyerName;
    @JsonProperty("buyer_tax_number")
    private String buyerTaxNumber;//购买方纳税人识别号
    @JsonProperty("total_price_plus_tax")
    private BigDecimal totalPricePlusTax;
    @JsonProperty("inv_tax_amount")
    private BigDecimal invTaxAmount;
    @JsonProperty("tax_rate")
    private BigDecimal taxRate;
    @JsonProperty("exclude_tax_amount")
    private BigDecimal excludeTaxAmount;
    @JsonProperty("inv_pdf_url")
    private String invPdfUrl;
    @JsonProperty("inv_pic_url")
    private String invPicUrl;

    private List<DetailData> detail;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailData {
        /**
         * 发票税率
         */
        private BigDecimal rate;
        /**
         * 发票税额
         */
        private BigDecimal amount;
        /**
         * 名称
         */
        private String name;

    }
}
