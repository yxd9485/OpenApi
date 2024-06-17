package com.fenbeitong.openapi.plugin.customize.wawj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by huangsiyuan on 2020/10/27.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_wawj_fuli_apply")
public class OpenWawjFuliApply {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 申请单id
     */
    @Column(name = "APPLY_ID")
    private String applyId;

    /**
     * 分贝员工ID
     */
    @Column(name = "EMPLOYEE_ID")
    private String employeeId;

    /**
     * 员工姓名
     */
    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    /**
     * 员工手机号
     */
    @Column(name = "EMPLOYEE_PHONE")
    private String employeePhone;

    /**
     * 公司ID
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

    /**
     * 公司名称
     */
    @Column(name = "COMPANY_NAME")
    private String companyName;

    /**
     * 第三方用户ID
     */
    @Column(name = "THIRD_USER_ID")
    private String thirdUserId;

    /**
     * 工作日
     */
    @Column(name = "WORK_DATE")
    private Date workDate;

    /**
     * 申请时间
     */
    @Column(name = "APPLY_TIME")
    private Date applyTime;

    /**
     * 0:初始值;1:已处理;2:已关闭
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 用于区分延时晚到加班类型
     */
    @Column(name = "TYPE")
    private Integer type;

}
