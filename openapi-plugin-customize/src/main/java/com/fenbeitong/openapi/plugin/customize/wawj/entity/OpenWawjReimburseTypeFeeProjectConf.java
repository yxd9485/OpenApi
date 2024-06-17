package com.fenbeitong.openapi.plugin.customize.wawj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: OpenWawjReimburseTypeFeeProjectConf</p>
 * <p>Description: 我爱我家报销类型及费用项目配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/9 6:14 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_wawj_reimburse_type_fee_project_conf")
public class OpenWawjReimburseTypeFeeProjectConf {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 场景类型编码
     */
    @Column(name = "ORDER_CATEGORY_TYPE")
    private Integer orderCategoryType;

    /**
     * 场景类型名称
     */
    @Column(name = "ORDER_CATEGORY_NAME")
    private String orderCategoryName;

    /**
     * 报销单类型编码
     */
    @Column(name = "REIMBURSE_FORM_TYPE_CODE")
    private String reimburseFormTypeCode;

    /**
     * 报销单类型名称
     */
    @Column(name = "REIMBURSE_FORM_TYPE_Name")
    private String reimburseFormTypeName;

    /**
     * 报销类型编码
     */
    @Column(name = "REIMBURSE_TYPE_CODE")
    private String reimburseTypeCode;

    /**
     * 报销类型名称
     */
    @Column(name = "REIMBURSE_TYPE_NAME")
    private String reimburseTypeName;

    /**
     * 费用项目编码
     */
    @Column(name = "FEE_PROJECT_CODE")
    private String feeProjectCode;

    /**
     * 费用项目名称
     */
    @Column(name = "FEE_PROJECT_NAME")
    private String feeProjectName;


}
