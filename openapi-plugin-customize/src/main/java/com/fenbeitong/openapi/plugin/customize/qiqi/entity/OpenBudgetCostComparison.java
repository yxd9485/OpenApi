package com.fenbeitong.openapi.plugin.customize.qiqi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName OpenBudgetCostComparison
 * @Description 预算费用对照表
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/6
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "open_budget_cost_comparison")
public class OpenBudgetCostComparison {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "company_Id")
    private String companyId;

    @Column(name = "fb_id")
    private String fbId;

    @Column(name = "open_type")
    private Integer openType;

    @Column(name = "state")
    private Integer state;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "budget_id")
    private String budgetId;

    @Column(name = "budget_code")
    private String budgetCode;

    @Column(name = "budget_name")
    private String budgetName;

    @Column(name = "cost_id")
    private String costId;

    @Column(name = "cost_code")
    private String costCode;

    @Column(name = "cost_name")
    private String costName;

    @Column(name = "cost_type_name")
    private String costTypeName;

}
