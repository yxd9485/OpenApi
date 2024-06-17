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
 * Created by wanghaoqiang on 2021/04/17.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "zhongxin_isv_company")
public class ZhongxinIsvCompany {

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
     * 企业三方ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 企业三方名称
     */
    @Column(name = "CORP_NAME")
    private String corpName;

    /**
     * 企业名称
     */
    @Column(name = "COMPANY_NAME")
    private String companyName;

    /**
     * 授权状态，0已授权，1未授权，2已过期
     */
    @Column(name = "STATE")
    private String state;

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

    /**
     * 授权人ID
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 授权人名称
     */
    @Column(name = "USER_NAME")
    private String userName;

    /**
     * 应用id
     */
    @Column(name = "APP_ID")
    private String appId;

    /**
     * 全国组织机构代码
     */
    @Column(name = "CORP_CODE")
    private String corpCode;

    /**
     * 统一社会信用代码
     */
    @Column(name = "SC_CODE")
    private String scCode;


}
