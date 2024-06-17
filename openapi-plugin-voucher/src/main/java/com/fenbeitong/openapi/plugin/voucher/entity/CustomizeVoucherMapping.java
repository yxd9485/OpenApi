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
 * Created by huangsiyuan on 2021/09/30.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customize_voucher_mapping")
public class CustomizeVoucherMapping {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 账单编号
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 类型 1:人员;2:部门;3:项目
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 源编码
     */
    @Column(name = "src_code")
    private String srcCode;

    /**
     * 源名称
     */
    @Column(name = "src_name")
    private String srcName;

    /**
     * 目标编码
     */
    @Column(name = "tgt_code")
    private String tgtCode;

    /**
     * 目标名称
     */
    @Column(name = "tgt_name")
    private String tgtName;

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
