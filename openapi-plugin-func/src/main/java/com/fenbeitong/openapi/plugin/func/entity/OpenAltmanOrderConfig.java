package com.fenbeitong.openapi.plugin.func.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by xiaowei on 2020/05/27.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_altman_order_config")
public class OpenAltmanOrderConfig {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 公司ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 场景类型air/car/hotel/train/mall/takeaway
     */
    @Column(name = "scence_type")
    private String scenceType;

    /**
     * 订单名称  万能订单—德邦用车订单
     */
    @Column(name = "order_name")
    private String orderName;

    /**
     * 订单简写  万能订单—德邦用车订单
     */
    @Column(name = "order_snapshot")
    private String orderSnapshot;

    /**
     * 供应商ID 3
     */
    @Column(name = "supplier_id")
    private Integer supplierId;

    /**
     * 供应商名称 曹操
     */
    @Column(name = "supplier_name")
    private String supplierName;

    /**
     * 业务类别ID 100011
     */
    @Column(name = "order_type_classify")
    private Integer orderTypeClassify;

    /**
     * 业务类别名称 曹操
     */
    @Column(name = "order_type_classify_name")
    private String orderTypeClassifyName;

    /**
     * 业务名称 用车
     */
    @Column(name = "order_type_name")
    private String orderTypeName;

    /**
     * 业务描述 德邦-曹操用车
     */
    @Column(name = "order_type_desc")
    private String orderTypeDesc;

    /**
     * 分贝业务类型1 pop 2 托管 3 采销
     */
    @Column(name = "business_type")
    private Integer businessType;

    /**
     * 发票提供的状态1、提供 0、不提供
     */
    @Column(name = "invoic_provide_status")
    private Integer invoicProvideStatus;

    /**
     * 开票的类型 1、专票 2、普票/电子票 27、企业配置
     */
    @Column(name = "scene_invoice_type")
    private Integer sceneInvoiceType;

    /**
     * 开票方 1、遵循开票规则 2、回填所选供应商名称
     */
    @Column(name = "invoice_provide_type")
    private Integer invoiceProvideType;

    /**
     * 开票方名称 / 遵循开票规则 / 回填所选供应商名称
     */
    @Column(name = "invoice_provide_name")
    private String invoiceProvideName;

    /**
     * 发票提供者
     */
    @Column(name = "invoice_provider")
    private String invoiceProvider;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;


}
