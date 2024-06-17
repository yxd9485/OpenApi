package com.fenbeitong.openapi.plugin.kingdee.customize.xindi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 金蝶保存数据结构
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KingdeeSaveReimbursementTravelDTO {

    @JsonProperty("formid")
    private String formId = "ER_ExpReimbursement_Travel";
    @JsonProperty("data")
    private Resource data;

    //模型数据
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Resource {
        //数据模块给入默认值
        @JsonProperty("Creator")
        private String creator = "";
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
        @JsonProperty("InterationFlags")
        private String interationFlags = "";
        @JsonProperty("IsAutoSubmitAndAudit")
        private String isAutoSubmitAndAudit = "false";
        @JsonProperty("Model")
        private Model model;

    }


    //模型数据
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Model {
        //1.单据头信息
        //实体主键
        @JsonProperty("FID")
        private int id;
        //订单号 (必填项)
        @JsonProperty("F_QDQX_FBTOrderNo")
        private String orderId;
        //申请日期  (必填项)
        @JsonProperty("FDate")
        private Date applyDate;
        //币别  (必填项)  PRE001  人民币
        @JsonProperty("FCurrencyID")
        private FCurrencyID currencyId;
        //申请组织 (必填项) 100
        @JsonProperty("FOrgID")
        private FOrgID orgId;
        //事由  (必填项)
        @JsonProperty("FCausa")
        private String causa;
        //申请人Id  (必填项)
        @JsonProperty("FProposerID")
        private FProposerID proposeId;
        // 申请部门  (必填项)
        @JsonProperty("FRequestDeptID")
        private FRequestDeptID requestDeptId;
        // 费用承担组织  (必填项)
        @JsonProperty("FExpenseOrgId")
        private FExpenseOrgId expenseOrgId;
        // 联系电话 (非必填)
        @JsonProperty("FContactPhoneNo")
        private String contactPhoneNo;
        // 单据类型 (必填项)  付款单："CLFBX001_SYS" 退款单 CLFBX001_SYS
        @JsonProperty("FBillTypeID")
        private FBillTypeID billTypeId;
        // 往来单位类型  (必填项) 员工 "BD_Empinfo"
        @JsonProperty("FCONTACTUNITTYPE")
        private String contactUnitType;
        // 费用承担部门  (必填项)
        @JsonProperty("FExpenseDeptID")
        private FExpenseDeptID expenseDepId;
        // 外部往来单位类型：客户 BD_Customer 固定的？
        @JsonProperty("FOUTCONTACTUNITTYPE")
        private String outContactUnitType;
        // 往来单位  (必填项)
        @JsonProperty("FCONTACTUNIT")
        private FCONTACTUNIT contactUnit;
        // 付款组织：100
        @JsonProperty("FPayOrgId")
        private FPayOrgId payOrgId;
        // 结算方式：电汇 JSFS04_SYS 现金： JSFS01_SYS
        @JsonProperty("FPaySettlleTypeID")
        private FPaySettlleTypeID paySettlleTypeId;
        // 开户银行：(页面必填)
        @JsonProperty("FBankBranchT")
        private String bankBranch;
        // 账户名称：
        @JsonProperty("FBankAccountNameT")
        private String bankAccountName;
        @JsonProperty("FBankAccountT")
        // 银行账号：
        private String bankAccount;
        // 本位币  (必填项) PRE001
        @JsonProperty("FLocCurrencyID")
        private FLocCurrencyID locCurrencyId;
        // 汇率 1.0
        @JsonProperty("FExchangeRate")
        private BigDecimal exchangeRate;
        // 汇率类型  (必填项)  固定汇率（固定值） "HLTX01_SYS"
        @JsonProperty("FExchangeTypeID")
        private FExchangeTypeID exchangeTypeId;
        // 是否合并付款：付款 true 退款为false
        @JsonProperty("FCombinedPay")
        private boolean combinePay = true;
        // 拆分行：true
        @JsonProperty("FSplitEntry")
        private boolean splitEntry = false;
        // 报销金额本位币：
        @JsonProperty("FLocExpAmountSum")
        private BigDecimal locExpAmountSum;
        // 退/付款金额本位币
        @JsonProperty("FLocReqAmountSum")
        private BigDecimal locReqAmountSum;
        // 核定报销金额汇总：
        @JsonProperty("FExpAmountSum")
        private BigDecimal expAmountSum;
        // 核定退/付款金额汇总：
        @JsonProperty("FReqAmountSum")
        private BigDecimal reqAmountSum;
        // 创建人：
        @JsonProperty("FCreatorId")
        private FCreatorId creatorId;
        // 创建日期：
        @JsonProperty("FCreateDate")
        private Date createDate;
        // 退款/付款：FRequestType 付款 1 退款 2
        @JsonProperty("FRequestType")
        private String requestType;
        // 退款账户 退款时必传的
        @JsonProperty("FRefundBankAccount")
        private FRefundBankAccount refundBankAccount;
        // 申请报销金额汇总：
        @JsonProperty("FReqReimbAmountSum")
        private BigDecimal reqReimbAmountSum;
        // 申请退/付款金额汇总
        @JsonProperty("FReqPayReFoundAmountSum")
        private BigDecimal reqPayReFoundAmountSum;
        // 开户行地址：
        @JsonProperty("FBankAddress")
        private String bankAddress;
        // 联行号：
        @JsonProperty("FBANKCNAPS")
        private String bankCnaps;
        // 实报实付： true
        @JsonProperty("FRealPay")
        private boolean realPay = true;
        // 二级负责人 011404
        @JsonProperty("F_JH_Base1")
        private F_JH_Base1 jhBase;
        // 审单会计 (必填项) 010897
        @JsonProperty("F_JH_CWSP")
        private F_JH_CWSP jhCwsp;
        // 部门负责人：
        @JsonProperty("F_JH_Base")
        private F_JH_Base hBase;
        // 申请退款负数 true  付款  false
        @JsonProperty("F_JH_CheckBox1")
        private boolean checkbox1 = false;
        // 预计付款日期：2020-09-18 00:00:00
        @JsonProperty("F_PAEZ_YJFKSJ")
        private Date paezYjfksj;
        // 复选框：false
        @JsonProperty("F_JH_CheckBox")
        private boolean checkBox = false;
        // 解锁单据编号：false
        @JsonProperty("F_QX_JSDJH")
        private boolean qxJsdjh = false;
        // 报销明细的实体
        @JsonProperty("FEntity")
        private List<FEntity> entity;
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
    public static class FEntity {
        // 费用项目  (必填项) 6602003002
        @JsonProperty("FExpID")
        private FExpID expId;
        @JsonProperty("FTravelStartDate")
        // 开始日期：FTravelStartDate  (必填项)
        private Date travelStartDate;
        // 费用说明：F_JH_FYSM  (必填项)
        @JsonProperty("F_JH_FYSM")
        private String jhFysm;
        // 结束日期：FTravelEndDate  (必填项)
        @JsonProperty("FTravelEndDate")
        private Date travelEndDate;
        // 出发地：FTravelStartSite  (必填项)
        @JsonProperty("FTravelStartSite")
        private String travelStartSite;
        // 目的地：FTravelEndSite  (必填项)
        @JsonProperty("FTravelEndSite")
        private String travelEndSite;
        //天数：FDays
        @JsonProperty("FDays")
        private int days;
        //  发票类型  0 "FInvoiceType": "1",
//        @JsonProperty("FInvoiceType")
//        private String invoiceType = "0";
        // 费用金额：FOtherTraAmount  明细里面必传
        @JsonProperty("FOtherTraAmount")
        private BigDecimal otherTraAmount;
        // 申请报销金额：FExpenseAmount
        @JsonProperty("FExpenseAmount")
        private BigDecimal expenseAmount;
        // 费用承担部门：FExpenseDeptEntryID  (必填项)
        @JsonProperty("FExpenseDeptEntryID")
        private FExpenseDeptEntryID expenseDeptEntryId;
        // 申请退/付款金额：FRequestAmount
        @JsonProperty("FRequestAmount")
        private BigDecimal requestAmount;
        // 核定报销金额：FExpSubmitAmount
        @JsonProperty("FExpSubmitAmount")
        private BigDecimal expSubmitAmount;
        // 核定退/付款金额：FReqSubmitAmount
        @JsonProperty("FReqSubmitAmount")
        private BigDecimal reqSubmitAmount;
        // 费用金额：FTaxSubmitAmt
        @JsonProperty("FTaxSubmitAmt")
        private BigDecimal taxSubmitAmt;
        // 不含税金额本位币：FLOCNOTAXAMOUNT
        @JsonProperty("FLOCNOTAXAMOUNT")
        private BigDecimal locnoTaxAmount;
        // 核定金额本位币：FLocReqSubmitAmount
        @JsonProperty("FLocReqSubmitAmount")
        private BigDecimal locReqSubmitAmount;
        // 核定报销金额本位币：FLocExpSubmitAmount
        @JsonProperty("FLocExpSubmitAmount")
        private BigDecimal locExpSubmitAmount;
        // 通过网上银行支付：FOnlineBank false
        @JsonProperty("FOnlineBank")
        private boolean onlineBank = false;
    }


}
