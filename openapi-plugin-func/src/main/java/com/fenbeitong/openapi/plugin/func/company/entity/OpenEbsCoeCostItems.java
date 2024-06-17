package com.fenbeitong.openapi.plugin.func.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by huangsiyuan on 2020/09/21.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_ebs_coe_cost_items")
public class OpenEbsCoeCostItems {

    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 关联ID
     */
    @Column(name = "RELATION_ID")
    private String relationId;

    /**
     * 预算类型ID
     */
    @Column(name = "BUDGET_TYPE_ID")
    private String budgetTypeId;

    /**
     * 类型
     */
    @Column(name = "TARGET_TYPE_CODE")
    private String targetTypeCode;

    /**
     * 费用用途ID
     */
    @Column(name = "COST_ITEM_ID")
    private String costItemId;

    /**
     * 费用用途编码
     */
    @Column(name = "COST_ITEM_CODE")
    private String costItemCode;

    /**
     * 费用类型
     */
    @Column(name = "COST_TYPE_CODE")
    private String costTypeCode;

    /**
     * 费用属性编码
     */
    @Column(name = "COST_ATTR_CODE")
    private String costAttrCode;

    /**
     * 费用属性描述
     */
    @Column(name = "COST_ITEM_DESC")
    private String costItemDesc;

    /**
     * 部门是否固定
     */
    @Column(name = "DEPT_MODIFY")
    private String deptModify;

    /**
     * 成本中心段
     */
    @Column(name = "GL_ACCOUNT_CODE")
    private String glAccountCode;

    /**
     * 会计科目段
     */
    @Column(name = "REFERENCE_CODE")
    private String referenceCode;

    /**
     * 说明
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * 是否推广类
     */
    @Column(name = "IS_MARKETING")
    private String isMarketing;

    /**
     * 有效标识
     */
    @Column(name = "ENABLED_FLAG")
    private String enabledFlag;

    /**
     * 生效日期
     */
    @Column(name = "ENABLE_START_DATE")
    private String enableStartDate;

    /**
     * 失效日期
     */
    @Column(name = "ENABLE_END_DATE")
    private String enableEndDate;

    /**
     * 最后更新日期
     */
    @Column(name = "CREATION_DATE")
    private String creationDate;

    /**
     * 最后更新人
     */
    @Column(name = "CREATED_BY")
    private String createdBy;

    /**
     * 最后更新日期
     */
    @Column(name = "LAST_UPDATE_DATE")
    private String lastUpdateDate;

    /**
     * 最后更新人
     */
    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    /**
     * 属性段1
     */
    @Column(name = "ATTRIBUTE1")
    private String attribute1;

    /**
     * 属性段2
     */
    @Column(name = "ATTRIBUTE2")
    private String attribute2;

    /**
     * 属性段3
     */
    @Column(name = "ATTRIBUTE3")
    private String attribute3;

    /**
     * 属性段4
     */
    @Column(name = "ATTRIBUTE4")
    private String attribute4;

    /**
     * 属性段5
     */
    @Column(name = "ATTRIBUTE5")
    private String attribute5;

    /**
     * 属性段6
     */
    @Column(name = "ATTRIBUTE6")
    private String attribute6;

    /**
     * 属性段7
     */
    @Column(name = "ATTRIBUTE7")
    private String attribute7;

    /**
     * 属性段8
     */
    @Column(name = "ATTRIBUTE8")
    private String attribute8;

    /**
     * 属性段9
     */
    @Column(name = "ATTRIBUTE9")
    private String attribute9;

    /**
     * 属性段10
     */
    @Column(name = "ATTRIBUTE10")
    private String attribute10;

    /**
     * 接口传入日期
     */
    @Column(name = "INTERFACE_DATE")
    private String interfaceDate;

    /**
     * 是否最新版标识
     */
    @Column(name = "CURR_FLAG")
    private String currFlag;


}
