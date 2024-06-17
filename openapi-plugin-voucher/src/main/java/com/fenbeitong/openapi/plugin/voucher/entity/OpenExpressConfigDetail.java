package com.fenbeitong.openapi.plugin.voucher.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by huangsiyuan on 2021/09/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_express_config_detail")
public class OpenExpressConfigDetail {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 主键id
     */
    @Column(name = "main_id")
    private String mainId;

    /**
     * 条件描述
     */
    @Column(name = "condition_desc")
    private String conditionDesc;

    /**
     * 条件表达式
     */
    @Column(name = "condition_express")
    private String conditionExpress;

    /**
     * 符合条件的json
     */
    @Column(name = "match_value")
    private String matchValue;

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
