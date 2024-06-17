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
@Table(name = "open_ebs_org_cost_relations")
public class OpenEbsOrgCostRelations {

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
     * PS部门类型
     */
    @Column(name = "PS_SET_ID")
    private String psSetId;

    /**
     * PS部门编码
     */
    @Column(name = "PS_DEPT_ID")
    private String psDeptId;

    /**
     * PS部门描述
     */
    @Column(name = "PS_DEPT_DESC")
    private String psDeptDesc;

    /**
     * PS生效日期
     */
    @Column(name = "PS_EFFECTIVE_DATE")
    private String psEffectiveDate;

    /**
     * EBS成本中心编码
     */
    @Column(name = "EBS_CC_CODE")
    private String ebsCcCode;

    /**
     * EBS成本中心描述
     */
    @Column(name = "EBS_CC_DESC")
    private String ebsCcDesc;

    /**
     * 父部门编码
     */
    @Column(name = "PARENT_DEPT_CODE")
    private String parentDeptCode;

    /**
     * PS有效标识
     */
    @Column(name = "ENABLED_FLAG")
    private String enabledFlag;

    /**
     * EBS生效日期
     */
    @Column(name = "START_DATE_ACTIVE")
    private String startDateActive;

    /**
     * EBS失效日期
     */
    @Column(name = "END_DATE_ACTIVE")
    private String endDateActive;

    /**
     * EBS有效标识
     */
    @Column(name = "CC_ENABLED_FLAG")
    private String ccEnabledFlag;

    /**
     * 有效日期
     */
    @Column(name = "ENABLE_START_DATE")
    private String enableStartDate;

    /**
     * 失效日期
     */
    @Column(name = "ENABLE_END_DATE")
    private String enableEndDate;

    /**
     * 创建日期
     */
    @Column(name = "CREATION_DATE")
    private String creationDate;

    /**
     * 创建人
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
     * 最新版本标识
     */
    @Column(name = "CURR_FLAG")
    private String currFlag;

    /**
     * 费用属性编码
     */
    @Column(name = "COST_ATTR_CODE")
    private String costAttrCode;


}
