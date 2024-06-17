package com.fenbeitong.openapi.plugin.customize.wawj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: OpenWawjCostcenterConf</p>
 * <p>Description: 我爱我家成本中心配置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/11/10 6:14 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_wawj_costcenter_conf")
public class OpenWawjCostcenterConf {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 部门编码
     */
    @Column(name = "DEPT_CODE")
    private String deptCode;

    /**
     * 部门描述
     */
    @Column(name = "DEPT_NAME")
    private String deptName;

    /**
     * 汇总部门编码
     */
    @Column(name = "SUMMARY_DEPT_CODE")
    private String summaryDeptCode;


}
