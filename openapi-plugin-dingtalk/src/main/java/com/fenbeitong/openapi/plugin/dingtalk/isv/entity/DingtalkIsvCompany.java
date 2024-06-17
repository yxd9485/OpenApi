package com.fenbeitong.openapi.plugin.dingtalk.isv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2020/07/13.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dingtalk_isv_company")
public class DingtalkIsvCompany {

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
     * 企业名称
     */
    @Column(name = "COMPANY_NAME")
    private String companyName;

    /**
     * 永久授权码
     */
    @Column(name = "PERMANENT_CODE")
    private String permanentCode;

    /**
     * 授权方应用ID
     */
    @Column(name = "AGENTID")
    private Long agentid;

    /**
     * 授权人三方ID
     */
    @Column(name = "THIRD_ADMIN_ID")
    private String thirdAdminId;

    /**
     * 授权状态，0取消授权，1已授权，2已过期
     */
    @Column(name = "STATE")
    private Integer state;

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
     * 主企业id
     */
    @Column(name = "MAIN_CORP_ID")
    private String mainCorpId;

}
