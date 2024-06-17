package com.fenbeitong.openapi.plugin.voucher.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by huangsiyuan on 2021/09/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_express_config")
public class OpenExpressConfig {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 公司id
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 1:商务账单-生成凭证;2:虚拟卡核销单-生成凭证;3:对公付款单-生成凭证;4:报销单-生成凭证
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 处理前脚本
     */
    @Column(name = "before_script")
    private String beforeScript;

    /**
     * 行处理前脚本
     */
    @Column(name = "before_row_script")
    private String beforeRowScript;

    /**
     * 行处理后脚本
     */
    @Column(name = "after_row_script")
    private String afterRowScript;

    /**
     * 处理类
     */
    private String listener;

    /**
     * 创建时间
     */
    @Column(name = "create_at")
    private Long createAt;

    /**
     * 创建人ID
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 创建人名称
     */
    @Column(name = "create_name")
    private String createName;

    /**
     * 更新时间
     */
    @Column(name = "update_at")
    private Long updateAt;

    /**
     * 更新人ID
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 更新人名称
     */
    @Column(name = "update_name")
    private String updateName;

    /**
     * 是否删除
     */
    @Column(name = "is_del")
    private Integer isDel;

    /**
     * 是否测试
     */
    @Column(name = "is_test")
    private Integer isTest;


}
