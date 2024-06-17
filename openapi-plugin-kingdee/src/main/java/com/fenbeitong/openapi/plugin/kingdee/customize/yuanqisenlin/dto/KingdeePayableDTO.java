package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 元气森林金蝶其他应付单
 *
 * @author ctl
 * @date 2021/7/1
 */
@NoArgsConstructor
@Data
public class KingdeePayableDTO {

    /**
     * 表单id
     */
    @JsonProperty("formid")
    private String formId;

    /**
     * 数据包
     */
    @JsonProperty("data")
    private Resource data;

    @NoArgsConstructor
    @Data
    public static class Resource {
        /**
         * 表单数据包，JSON类型（必录）
         */
        @JsonProperty("Model")
        private ModelDTO model;

        @NoArgsConstructor
        @Data
        public static class ModelDTO {
            /**
             * 实体主键
             */
            @JsonProperty("FID")
            private Integer id;
            /**
             * 单据类型 必填 QTYFD01_SYS 其他应付单
             */
            @JsonProperty("FBillTypeID")
            private FBillTypeIDDTO billTypeID;
            /**
             * 业务日期 必填 根据账单日期倒推
             */
            @JsonProperty("FDATE")
            private String date;
            /**
             * 到期日 必填 根据账单日期倒推
             */
            @JsonProperty("FENDDATE_H")
            private String enddateH;
            /**
             * 是否期初单据
             */
            @JsonProperty("FISINIT")
            private Boolean isinit;
            /**
             * 往来单位类型 必填 固定为供应商
             */
            @JsonProperty("FCONTACTUNITTYPE")
            private String contactunittype;
            /**
             * 往来单位 必填 写分贝通在用户金蝶的编码
             */
            @JsonProperty("FCONTACTUNIT")
            private FCONTACTUNITDTO contactunit;
            /**
             * 币别 必填 PRE001 人民币
             */
            @JsonProperty("FCURRENCYID")
            private FCURRENCYIDDTO currencyid;
            /**
             * 总金额 根据账单金额算
             */
            @JsonProperty("FTOTALAMOUNTFOR_H")
            private BigDecimal totalamountforH;
            /**
             * 未借款金额 按总金额算
             */
            @JsonProperty("FNOTSETTLEAMOUNTFOR")
            private BigDecimal notsettleamountfor;
            /**
             * 申请部门
             */
            @JsonProperty("FDEPARTMENTID")
            private FDEPARTMENTIDDTO departmentid;
            /**
             * 结算组织 必填 按合同主体的部门编码算
             */
            @JsonProperty("FSETTLEORGID")
            private FSETTLEORGIDDTO settleorgid;
            /**
             * 采购组织 按合同主体的部门编码算
             */
            @JsonProperty("FPURCHASEORGID")
            private FPURCHASEORGIDDTO purchaseorgid;
            /**
             * 付款组织 必填 按合同主体的部门编码算
             */
            @JsonProperty("FPAYORGID")
            private FPAYORGIDDTO payorgid;
            /**
             * 本位币 必填 PRE001 人民币
             */
            @JsonProperty("FMAINBOOKSTDCURRID")
            private FMAINBOOKSTDCURRIDDTO mainbookstdcurrid;
            /**
             * 汇率类型 必填 HLTX01_SYS 固定汇率
             */
            @JsonProperty("FEXCHANGETYPE")
            private FEXCHANGETYPEDTO exchangetype;
            /**
             * 汇率 1.0
             */
            @JsonProperty("FExchangeRate")
            private BigDecimal exchangeRate;
            /**
             * 不含税金额本位币
             */
            @JsonProperty("FNOTAXAMOUNT")
            private BigDecimal notaxamount;
            /**
             * 税额本位币
             */
            @JsonProperty("FTAXAMOUNT")
            private BigDecimal taxamount;
            /**
             * 到期日计算日期 必填 账单日期倒推
             */
            @JsonProperty("FACCNTTIMEJUDGETIME")
            private String accnttimejudgetime;
            /**
             * 作废状态 必填 A
             */
            @JsonProperty("FCancelStatus")
            private String cancelStatus;
            /**
             * 业务类型 T
             */
            @JsonProperty("FBUSINESSTYPE")
            private String businesstype;
            /**
             * 适用范围 必填
             */
            @JsonProperty("F_ScopeOfApplication")
            private String scopeofapplication;
            /**
             * 单据体
             */
            @JsonProperty("FEntity")
            private List<FEntityDTO> entity;

            @NoArgsConstructor
            @Data
            public static class FBillTypeIDDTO {
                @JsonProperty("FNUMBER")
                private String number;
            }

            @NoArgsConstructor
            @Data
            public static class FCONTACTUNITDTO {
                @JsonProperty("FNumber")
                private String number;
            }

            @NoArgsConstructor
            @Data
            public static class FCURRENCYIDDTO {
                @JsonProperty("FNumber")
                private String number;
            }

            @NoArgsConstructor
            @Data
            public static class FDEPARTMENTIDDTO {
                @JsonProperty("FNumber")
                private String number;
            }

            @NoArgsConstructor
            @Data
            public static class FSETTLEORGIDDTO {
                @JsonProperty("FNumber")
                private String number;
            }

            @NoArgsConstructor
            @Data
            public static class FPURCHASEORGIDDTO {
                @JsonProperty("FNumber")
                private String number;
            }

            @NoArgsConstructor
            @Data
            public static class FPAYORGIDDTO {
                @JsonProperty("FNumber")
                private String number;
            }

            @NoArgsConstructor
            @Data
            public static class FMAINBOOKSTDCURRIDDTO {
                @JsonProperty("FNumber")
                private String number;
            }

            @NoArgsConstructor
            @Data
            public static class FEXCHANGETYPEDTO {
                @JsonProperty("FNumber")
                private String number;
            }

            @NoArgsConstructor
            @Data
            public static class FEntityDTO {
                /**
                 * 费用项目编码 跟事由挂钩
                 */
                @JsonProperty("FCOSTID")
                private FCOSTIDDTO costid;
                /**
                 * 费用承担部门 必填 费用归属部门
                 */
                @JsonProperty("FCOSTDEPARTMENTID")
                private FCOSTDEPARTMENTIDDTO costdepartmentid;
                /**
                 * 税率(%)    等公式
                 */
                @JsonProperty("FEntryTaxRate")
                private BigDecimal entryTaxRate;
                /**
                 * 不含税金额    等公式
                 */
                @JsonProperty("FNOTAXAMOUNTFOR")
                private BigDecimal notaxamountfor;
                /**
                 * 税额   等公式
                 */
                @JsonProperty("FTAXAMOUNTFOR")
                private BigDecimal taxamountfor;
                /**
                 * 总金额  等公式
                 */
                @JsonProperty("FTOTALAMOUNTFOR")
                private BigDecimal totalamountfor;
                /**
                 * 未借款金额    总金额
                 */
                @JsonProperty("FNOTSETTLEAMOUNTFOR_D")
                private BigDecimal notsettleamountforD;
                /**
                 * 不含税金额本位币 不含税金额
                 */
                @JsonProperty("FNOTAXAMOUNT_D")
                private BigDecimal notaxamountD;
                /**
                 * 税额本位币 税额
                 */
                @JsonProperty("FTAXAMOUNT_D")
                private BigDecimal taxamountD;
                /**
                 * 已生成发票
                 */
                @JsonProperty("FCREATEINVOICE")
                private Boolean createinvoice;
                /**
                 * 研发项目 扩展字段中取
                 */
                @JsonProperty("F_PFGC_Assistant")
                private FPFGCAssistantDTO pfgcAssistant;

                @JsonProperty("F_TaxRateName")
                private FTaxRateNameDTO taxRateName;

                @NoArgsConstructor
                @Data
                public static class FCOSTIDDTO {
                    @JsonProperty("FNumber")
                    private String number;
                }

                @NoArgsConstructor
                @Data
                public static class FCOSTDEPARTMENTIDDTO {
                    @JsonProperty("FNumber")
                    private String number;
                }

                @NoArgsConstructor
                @Data
                public static class FPFGCAssistantDTO {
                    @JsonProperty("FNumber")
                    private String number;
                }

                @NoArgsConstructor
                @Data
                public static class FTaxRateNameDTO {
                    @JsonProperty("FNumber")
                    private String number;
                }
            }
        }
    }

}
