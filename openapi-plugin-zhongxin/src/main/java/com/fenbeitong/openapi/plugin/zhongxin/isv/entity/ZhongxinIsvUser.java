package com.fenbeitong.openapi.plugin.zhongxin.isv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by wanghaoqiang on 2021/04/22.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "zhongxin_isv_user")
public class ZhongxinIsvUser {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 分贝通企业ID
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

    /**
     * 用户手机号
     */
    @Column(name = "PHONE_NUM")
    private String phoneNum;

    /**
     * 用户名称
     */
    @Column(name = "USER_NAME")
    private String userName;

    /**
     * 用户id
     */
    @Column(name = "HASH")
    private String hash;

    /**
     * 证件类型
     */
    @Column(name = "ID_TYPE")
    private String idType;

    /**
     * 证件号码
     */
    @Column(name = "ID_NUM")
    private String idNum;

    /**
     * 分贝通员工编号
     */
    @Column(name = "EMPLOYEE_ID")
    private String employeeId;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
