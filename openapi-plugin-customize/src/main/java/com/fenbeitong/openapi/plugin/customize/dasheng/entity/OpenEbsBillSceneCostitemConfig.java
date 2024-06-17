package com.fenbeitong.openapi.plugin.customize.dasheng.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by huangsiyuan on 2020/10/19.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_ebs_bill_scene_costitem_config")
public class OpenEbsBillSceneCostitemConfig {

    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 场景编号
     */
    @Column(name = "ORDER_CATEGORY")
    private String orderCategory;

    /**
     * 场景名称
     */
    @Column(name = "ORDER_CATEGORY_NAME")
    private String orderCategoryName;

    /**
     * 费用用途编码
     */
    @Column(name = "COST_ITEM_CODE")
    private String costItemCode;


}
