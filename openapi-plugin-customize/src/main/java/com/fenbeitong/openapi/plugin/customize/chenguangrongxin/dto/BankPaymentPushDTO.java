package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName BankPaymentPushDTO
 * @Description 对公付款推送参数
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/20
 **/
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class BankPaymentPushDTO {
    /**
     * 申请表单唯一码
     */
    @JSONField(name = "jdy_third_apply_id")
    private Entry<String> jdyThirdApplyId;

    /**
     * 三方系统申请人id
     */
    @JSONField(name = "jdy_third_employee_id")
    private Entry<String> jdyThirdEmployeeId;

    /**
     * 对公付款金额
     */
    @JSONField(name = "jdy_estimated_total_amount")
    private Entry<BigDecimal> jdyEstimatedTotalAmount;

    /**
     * 申请单名称
     */
    @JSONField(name = "jdy_payment_name")
    private Entry<String> jdyPaymentName;

    /**
     * 分贝通内供应商id（三方系统供应商id）
     */
    @JSONField(name = "jdy_supplier_id")
    private Entry<String> jdySupplierId;

    /**
     * 付款时间
     */
    @JSONField(name = "jdy_payment_time")
    private Entry<String> jdyPaymentTime;

    /**
     * 付款用途
     */
    @JSONField(name = "jdy_payment_use")
    private Entry<String> jdyPaymentUse;

    /**
     * 申请事由
     */
    @JSONField(name = "jdy_apply_reason")
    private Entry<String> jdyApplyReason;

    /**
     * 费用类别
     */
    @JSONField(name = "jdy_cost_category")
    private Entry<String> jdyCostCategory;

    /**
     * 费用类别code
     */
    @JSONField(name = "jdy_costcategory_code")
    private Entry<String> jdyCostcategoryCode;

    /**
     * 费用类别名称
     */
    @JSONField(name = "jdy_costcategory_name")
    private Entry<String> jdyCostcategoryName;

    /**
     * 费用归属信息
     */
    @JSONField(name = "jdy_cost_attributions")
    private Entry<String> jdyCostAttributions;

    /**
     * 费用归属类型
     */
    @JSONField(name = "jdy_cost_type")
    private Entry<String> jdyCostType;

    /**
     * 三方系统自定义档案ID
     */
    @JSONField(name = "jdy_third_archive_id")
    private Entry<String> jdyThirdArchiveId;

    /**
     * 自定义档案名称
     */
    @JSONField(name = "jdy_archive_name")
    private Entry<String> jdyArchiveName;

    /**
     * 费用归属明细
     */
    @JSONField(name = "jdy_detail")
    private Entry<String> jdyDetail;

    /**
     * 三方系统费用归属id
     */
    @JSONField(name = "jdy_third_id")
    private Entry<String> jdyThirdId;

    /**
     * 费用归属name
     */
    @JSONField(name = "jdy_name")
    private Entry<String> jdyName;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entry<T> {
        private T value;
    }
}
