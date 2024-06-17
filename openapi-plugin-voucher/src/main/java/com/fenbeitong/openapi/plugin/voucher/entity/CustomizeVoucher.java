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
 * Created by huangsiyuan on 2021/09/28.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customize_voucher")
public class CustomizeVoucher {

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
     * 账单编号
     */
    @Column(name = "bill_no")
    private String billNo;

    /**
     * 状态: 1:生成中;2:生成失败;3:生成成功
     */
    @Column(name = "status")
    private Integer status;

    /**
     * excel url地址
     */
    @Column(name = "url")
    private String url;

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
