package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author machao
 * @date 2022/9/16
 */
@Data
public class ReimbursementPushDTO {

    /**
     * 报销单号
     */
    @JSONField(name = "reimbursement_number")
    private Entry<String> reimbursementNumber;

    /**
     * 提交人（三方员工id）
     */
    private Entry<String> submitter;

    /**
     * 法人公司
     */
    @JSONField(name = "corporate_company")
    private Entry<String> corporateCompany;

    /**
     * 部门（三方部门id）
     */
    @JSONField(name = "department")
    private Entry<Long> department;

    /**
     * 部门Id
     */
    @JSONField(name = "dep_id")
    private Entry<String> depId;

    /**
     * 报销申请日期
     */
    @JSONField(name = "reimbursementapplication_date")
    private Entry<String> reimbursementApplicationDate;

    /**
     * 报销起始日期
     */
    @JSONField(name = "reimbursement_starttime")
    private Entry<String> reimbursementStartTime;

    /**
     * 报销结束日期
     */
    @JSONField(name = "reimbursement_endtime")
    private Entry<String> reimbursementEndTime;

    /**
     * 项目类型
     */
    @JSONField(name = "project_type")
    private Entry<String> projectType;

    /**
     * 项目编号（项目名称）
     */
    @JSONField(name = "projectno")
    private Entry<String> projectNo;

    /**
     * 客商名称
     */
    @JSONField(name = "merchant_name")
    private Entry<String> merchantName;

    /**
     * 报销事由（备注）
     */
    @JSONField(name = "reasonforreimbursement")
    private Entry<String> reasonForReimbursement;

    /**
     * 费用明细
     */
    @JSONField(name = "chargedetails")
    private Entry<List<Charge>> chargedDetails;

    @Data
    public static class Charge {

        /**
         * 费用名称
         */
        @JSONField(name = "chargedetails_cost")
        private Entry<String> chargeDetailsCost;

        /**
         * 工作类型
         */
        @JSONField(name = "chargedetails_jobtype")
        private Entry<String> chargeDetailsJobType;

        /**
         * 费用类型
         */
        @JSONField(name = "chargedetails_typesoffee")
        private Entry<String> chargeDetailsChargeType;

        /**
         * 支付方式
         */
        @JSONField(name = "chargedetails_paymentmethod")
        private Entry<String> chargeDetailsPaymentMethod;

        /**
         * 费用描述
         */
        @JSONField(name = "cchargedetails_feedescription")
        private Entry<String> chargeDetailsFeeDescription;

        /**
         * 费用归属
         */
        @JSONField(name = "chargedetails_attributionofexpenses")
        private Entry<String> chargeDetailsAttributionOfExpenses;

        /**
         * 费用归属类型
         */
        @JSONField(name = "chargedetails_expenseattributiontype")
        private Entry<String> chargeDetailsExpenseAttributionType;

        /**
         * 费用归属三方id
         */
        @JSONField(name = "costattribution_id")
        private Entry<String> costAttributionId;

        /**
         * 交易总额 单位/元
         */
        @JSONField(name = "totaltransaction")
        private Entry<BigDecimal> totalTransaction;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entry<T> {
        private T value;
    }

}
