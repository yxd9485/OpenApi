package com.fenbeitong.openapi.plugin.kingdee.customize.aerfa.bill.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 金蝶保存数据结构-费用报销单
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KingdeeSaveReimbursementDTO {

    @JsonProperty("formid")
    private String formId = "ER_ExpReimbursement";
    @JsonProperty("data")
    private Resource data;

    //模型数据
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Resource {
        @JsonProperty("NeedUpDateFields")
        private List<String> needUpDateFields = new ArrayList<>();
        @JsonProperty("NeedReturnFields")
        private List<String> needReturnFields = new ArrayList<>();
        @JsonProperty("IsDeleteEntry")
        private String isDeleteEntry = "true";
        @JsonProperty("SubSystemId")
        private String subSystemId = "";
        @JsonProperty("IsVerifyBaseDataField")
        private String isVerifyBaseDataField = "false";
        @JsonProperty("IsEntryBatchFill")
        private String isEntryBatchFill = "true";
        @JsonProperty("ValidateFlag")
        private String validateFlag = "true";
        @JsonProperty("NumberSearch")
        private String numberSearch = "true";
        @JsonProperty("IsAutoAdjustField")
        private Boolean isAutoAdjustField=false;
        @JsonProperty("InterationFlags")
        private String interationFlags = "";
        @JsonProperty("IgnoreInterationFlag")
        private  Boolean ignoreInterationFlag;
        @JsonProperty("IsControlPrecision")
        private Boolean isControlPrecision;
        @JsonProperty("Model")
        private Model model;
    }

    //模型数据
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Model {
        //数据来源
        @JsonProperty("FBillSource")
        private String billSource;
        //申请人Id  (必填项)
        @JsonProperty("FProposerID")
        private FProposerID proposeId;
        // 申请部门  (必填项)
        @JsonProperty("FRequestDeptID")
        private FRequestDeptID requestDeptId;
        // 费用承担部门  (必填项)
        @JsonProperty("FExpenseDeptID")
        private FExpenseDeptID expenseDepId;
        //币别  (必填项)  PRE001  人民币
        @JsonProperty("FCurrencyID")
        private FCurrencyID currencyId;
        // 汇率类型  (必填项)  固定汇率（固定值） "HLTX01_SYS"
        @JsonProperty("FExchangeTypeID")
        private FExchangeTypeID exchangeTypeId;
        //申请组织 (必填项) 100
        @JsonProperty("FOrgID")
        private FOrgID orgId;
        //申请日期  (必填项)
        @JsonProperty("FDate")
        private Date applyDate;
        // 费用承担组织  (必填项)
        @JsonProperty("FExpenseOrgId")
        private FExpenseOrgId expenseOrgId;
        // 本位币  (必填项) PRE001
        @JsonProperty("FLocCurrencyID")
        private FLocCurrencyID locCurrencyId;
        // 往来单位类型  (必填项) 员工 "BD_Empinfo"
        @JsonProperty("FCONTACTUNITTYPE")
        private String contactUnitType;
        // 往来单位  (必填项)
        @JsonProperty("FCONTACTUNIT")
        private FCONTACTUNIT contactUnit;
        // 单据类型 (必填项)  付款单："CLFBX001_SYS" 退款单 CLFBX001_SYS
        @JsonProperty("FBillTypeID")
        private FBillTypeID billTypeId;
        //事由  (必填项)
        @JsonProperty("FCausa")
        private String causa;
        //费用类别  默认取值“01 一般费用”
        @JsonProperty("F_PAEZ_Assistant")
        private FPAEZAssistant paezAssistant;
        //申请付款 涉及金额为正数时，默认传“false”
        @JsonProperty("FPayBox")
        private Boolean payBox;
        //申请退款 涉及金额为负数时，默认传“true”
        @JsonProperty("FrefundBox")
        private Boolean FrefundBox;
        // 付款组织：100 申请付款勾选“否”时，付款信息隐藏，无需获取；
        //申请退款勾选“是”时，付款信息带出，需获取付款组织，根据账单的自定义字段：开票主体进行获取，需要做映射
        @JsonProperty("FPayOrgId")
        private FPayOrgId payOrgId;
        // 结算方式：电汇 JSFS04_SYS 现金： JSFS01_SYS
        @JsonProperty("FPaySettlleTypeID")
        private FPaySettlleTypeID paySettlleTypeId;
        // 报销明细的实体
        @JsonProperty("FEntity")
        private List<FEntity> entity;

        @Data
        public static class FEntity {
            // 费用项目  (必填项) 6602003002
            @JsonProperty("FExpID")
            private FExpID expId;
            //  发票类型  0 "FInvoiceType": "1",
            @JsonProperty("FInvoiceType")
            private String invoiceType;
            // 费用金额：FTaxSubmitAmt
            @JsonProperty("FTaxSubmitAmt")
            private BigDecimal taxSubmitAmt;
            // 税率：FTaxRate
            @JsonProperty("FTaxRate")
            private BigDecimal taxRate;
            // 税额：FTaxAmt
            @JsonProperty("FTaxAmt")
            private BigDecimal taxAmt;
            // 费用承担部门：FExpenseDeptEntryID  (必填项)
            @JsonProperty("FExpenseDeptEntryID")
            private FExpenseDeptEntryID expenseDeptEntryId;
            // 研发项目
            @JsonProperty("F_CMY_Assistant ")
            private FCMYAssistant cmyAssistant;
            // 核定报销金额：FExpSubmitAmount
            @JsonProperty("FExpSubmitAmount")
            private BigDecimal expSubmitAmount;
            // 申请退/付款金额：FRequestAmount
            @JsonProperty("FRequestAmount")
            private BigDecimal requestAmount;
            // 备注
            @JsonProperty("FRemark")
            private String remark;
        }
    }

    @Data
    public static class FCurrencyID {
        @JsonProperty("FNUMBER")
        private String number;
    }

    @Data
    public static class FOrgID {
        @JsonProperty("FNumber")
        private String number;
    }

    @Data
    public static class FProposerID {
        @JsonProperty("FSTAFFNUMBER")
        private String stafNumber;
    }

    @Data
    public static class FRequestDeptID {
        @JsonProperty("FNUMBER")
        private String number;
    }

    @Data
    public static class FExpenseOrgId {
        @JsonProperty("FNumber")
        private String number;
    }

    @Data
    public static class FBillTypeID {
        @JsonProperty("FNUMBER")
        private String number;
    }

    @Data
    public static class FExpenseDeptID {
        @JsonProperty("FNUMBER")
        private String number;
    }

    @Data
    public static class FCONTACTUNIT {
        @JsonProperty("FNumber")
        private String number;
    }

    @Data
    public static class FPayOrgId {
        @JsonProperty("FNumber")
        private String number;
    }

    @Data
    public static class FPaySettlleTypeID {
        @JsonProperty("FNUMBER")
        private String number;
    }

    @Data
    public static class FLocCurrencyID {
        @JsonProperty("FNUMBER")
        private String number;
    }

    @Data
    public static class FRefundBankAccount {
        @JsonProperty("FNUMBER")
        private String number;
    }

    @Data
    public static class FExchangeTypeID {
        @JsonProperty("FNUMBER")
        private String number;
    }

    @Data
    public static class FCreatorId {
        @JsonProperty("FUserID")
        private String userId;
    }

    @Data
    public static class F_JH_Base1 {
        @JsonProperty("FSTAFFNUMBER")
        private String staffNumber;
    }

    @Data
    public static class F_JH_CWSP {
        @JsonProperty("FSTAFFNUMBER")
        private String staffNumber;
    }

    @Data
    public static class F_JH_Base {
        @JsonProperty("FSTAFFNUMBER")
        private String staffNumber;
    }

    @Data
    public static class FExpID {
        @JsonProperty("FNUMBER")
        private String number;
    }

    @Data
    public static class FExpenseDeptEntryID {
        @JsonProperty("FNUMBER")
        private String number;
    }
    @Data
    public static class FPAEZAssistant{
        @JsonProperty("FNUMBER")
        private String number;
    }
    @Data
    public static class FCMYAssistant{
        @JsonProperty("FNUMBER")
        private String number;
    }
}
