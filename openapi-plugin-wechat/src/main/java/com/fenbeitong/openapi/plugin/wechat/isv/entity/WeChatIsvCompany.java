package com.fenbeitong.openapi.plugin.wechat.isv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 企业微信ISV企业信息
 * Created by lizhen on 2020/03/20.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "qywx_isv_company")
public class WeChatIsvCompany {

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
    private Integer agentid;

    /**
     * 授权人三方ID
     */
    @Column(name = "THIRD_ADMIN_ID")
    private String thirdAdminId;

    /**
     * 授权人ID
     */
    @Column(name = "ADMIN_ID")
    private String adminId;

    /**
     * 组织机构SECRET
     */
    @Column(name = "ORG_SECRET")
    private String orgSecret;

    /**
     * 授权状态，0取消授权，1已授权，2已过期
     */
    @Column(name = "STATE")
    private Integer state;

}
