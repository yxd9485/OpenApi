package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 元气森林金蝶费用报销单
 *
 * @author lizhen
 * @date 2021/11/18
 */
@NoArgsConstructor
@Data
public class KingdeeExpReimbursementDTO {

    /**
     * 表单id
     */
    @JsonProperty("formid")
    private String formId = Constant.REIMBURSEMENT_FORM_ID;

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
        /**
         * 自动提交审核
         */
        @JsonProperty("IsAutoSubmitAndAudit")
        private String isAutoSubmitAndAudit = "true";
        @NoArgsConstructor
        @Data
        public static class ModelDTO {
            /**
             * 实体主键
             */
            @JsonProperty("FID")
            private Integer id;

            /**
             * 单据ID
             */
            @JsonProperty("FBillNo")
            private String billNo;
            /**
             * 业务日期 必填 根据账单日期倒推
             */
            @JsonProperty("FDate")
            private String date;
            /**
             * 币别 必填 PRE001 人民币
             */
            @JsonProperty("FCurrencyID")
            private FNUMBERDTO currencyid;
            /**
             * 申请组织（员工合同主体公司编码）
             */
            @JsonProperty("FOrgID")
            private FNumberTDTO orgId;
            /**
             * 事由
             */
            @JsonProperty("FCausa")
            private String causa;
            /**
             * 申请人（工号）
             */
            @JsonProperty("FProposerID")
            private FSTAFFNUMBERDTO proposerId;
            /**
             * 申请部门（金蝶部门编码）
             */
            @JsonProperty("FRequestDeptID")
            private FNUMBERDTO requestDeptId;
            /**
             * 单据类型 必填 FYBXD001_SYS 费用报销
             */
            @JsonProperty("FBillTypeID")
            private FNUMBERDTO billTypeID;
            /**
             * 费用承担组织（员工合同主体公司编码）
             */
            @JsonProperty("FExpenseOrgId")
            private FNumberTDTO expenseOrgId;
            /**
             * 费用承担部门
             */
            @JsonProperty("FExpenseDeptID")
            private FNUMBERDTO expenseDeptId;
            /**
             * 往来单位类型 必填 固定员工BD_Empinfo
             */
            @JsonProperty("FCONTACTUNITTYPE")
            private String contactunittype;

            /**
             * 往来单位 必填 员工工号
             */
            @JsonProperty("FCONTACTUNIT")
            private FNumberTDTO contactunit;
            /**
             * 付款组织 必填 员工合同主体公司编码
             */
            @JsonProperty("FPayOrgId")
            private FNumberTDTO payorgid;

            /**
             * 结算方式 必填 电汇 JSFS04_SYS
             */
            @JsonProperty("FPaySettlleTypeID")
            private FNUMBERDTO paySettlleTypeId;

            /**
             * 开户行
             */
            @JsonProperty("FBankBranchT")
            private String bankBranch;

            /**
             * 账户名称 必填 姓名
             */
            @JsonProperty("FBankAccountNameT")
            private String bankAccountName;

            /**
             * 银行账号
             */
            @JsonProperty("FBankAccountT")
            private String bankAccount;
            /**
             * 本位币 必填 PRE001 人民币
             */
            @JsonProperty("FLocCurrencyID")
            private FNUMBERDTO locCurrenyId;

            /**
             * 汇率类型 必填 HLTX01_SYS 固定汇率
             */
            @JsonProperty("FExchangeTypeID")
            private FNUMBERDTO exchangetype;

            /**
             * 汇率 1.0
             */
            @JsonProperty("FExchangeRate")
            private BigDecimal exchangeRate;

            // 是否合并付款：付款 true 退款为false
            @JsonProperty("FCombinedPay")
            private boolean combinePay = true;
            // 拆分行：true
            @JsonProperty("FSplitEntry")
            private boolean splitEntry = false;
            /**
             * 报销金额本位币(含税）
             */
            @JsonProperty("FLocExpAmountSum")
            private BigDecimal locExpAmountSum;
            /**
             * 核定报销金额汇总(含税）
             */
            @JsonProperty("FExpAmountSum")
            private BigDecimal expAmountSum;
            /**
             * 退/付款金额本位币(含税）
             */
            @JsonProperty("FLocReqAmountSum")
            private BigDecimal locReqAmountSum;
            /**
             * 核定退/付款金额汇总(含税）
             */
            @JsonProperty("FReqAmountSum")
            private BigDecimal reqAmountSum;
            /**
             * 通过网上银行支付：FOnlineBankShow
             */
            @JsonProperty("FOnlineBankShow")
            private boolean onlineBankShow = true;
            /**
             * 创建时间
             */
            @JsonProperty("FCreateDate")
            private String createDate;
            /**
             * 创建人
             */
            @JsonProperty("FCreatorId")
            private FCreatorId creatorId;
            /**
             * 退款/付款：FRequestType 付款 1 退款 2
             */
            @JsonProperty("FRequestType")
            private String requestType;
            /**
             * 申请报销金额汇总(含税）
             */
            @JsonProperty("FReqReimbAmountSum")
            private BigDecimal reqReimbAmountSum;
            /**
             * 申请退/付款金额汇总(含税）
             */
            @JsonProperty("FReqPayReFoundAmountSum")
            private BigDecimal reqPayReFoundAmountSum;
            // 实报实付： true
            @JsonProperty("FRealPay")
            private boolean realPay = false;
            /**
             * 单据体
             */
            @JsonProperty("FEntity")
            private List<FEntityDTO> entity;


        }

        @NoArgsConstructor
        @Data
        public static class FNumberTDTO {
            @JsonProperty("FNumber")
            private String number;
        }

        @NoArgsConstructor
        @Data
        public static class FNUMBERDTO {
            @JsonProperty("FNUMBER")
            private String number;
        }

        @NoArgsConstructor
        @Data
        public static class FSTAFFNUMBERDTO {
            @JsonProperty("FSTAFFNUMBER")
            private String staffNumber;
        }

        @Data
        public static class FCreatorId {
            @JsonProperty("FUserID")
            private String userId;
        }
        @NoArgsConstructor
        @Data
        public static class FEntityDTO {

            /**
             * 费用项目编码 跟事由挂钩
             */
            @JsonProperty("FExpID")
            private FNUMBERDTO expId;
            /**
             * 费用承担部门 必填 费用归属部门
             */
            @JsonProperty("FExpenseDeptEntryID")
            private FNUMBERDTO expenseDeptEntryId;

            /**
             * 不含税金额本位币（不含税）
             */
            @JsonProperty("FLOCNOTAXAMOUNT")
            private BigDecimal locNoTaxAmount;

            /**
             * 费用金额（不含税）
             */
            @JsonProperty("FTaxSubmitAmt")
            private BigDecimal taxSubmitAmt;

            /**
             * 税额本位币
             */
            @JsonProperty("FLOCTAXAMOUNT")
            private BigDecimal locTaxAmount;

            /**
             * 税额
             */
            @JsonProperty("FTaxAmt")
            private BigDecimal taxAmt;

            /**
             * 通过网上银行支付：FOnlineBank false
             */
            @JsonProperty("FOnlineBank")
            private boolean onlineBank = false;
            /**
             * 备注
             */
            @JsonProperty("FRemark")
            private String remark;
            /**
             * 新品研发项目ID
             */
            @JsonProperty("F_yuan_Assistant")
            private FNumberTDTO yuanAssistant;

        }
    }

}
