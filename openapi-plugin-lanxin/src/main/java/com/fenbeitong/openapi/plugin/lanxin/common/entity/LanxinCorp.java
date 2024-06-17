package com.fenbeitong.openapi.plugin.lanxin.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by duhui on 2021/12/06.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lanxin_corp")
public class LanxinCorp {

    /**
     * 
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 企业id
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * appId
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 组织ID
     */
    @Column(name = "organization_id")
    private String organizationId;

    /**
     * appsecret
     */
    @Column(name = "app_secret")
    private String appSecret;

    /**
     * 开放平台网关地址
     */
    @Column(name = "gateway_url")
    private String gatewayUrl;

    /**
     * OAuth授权页地址
     */
    @Column(name = "auth_url")
    private String authUrl;

    /**
     * 开发者中心地址
     */
    @Column(name = "develop_center_url")
    private String developCenterUrl;

    /**
     * 管理平台地址
     */
    @Column(name = "management_platform_url")
    private String managementPlatformUrl;

    /**
     * 回调地址
     */
    @Column(name = "callback_url")
    private String callbackUrl;

    /**
     * 0无效 1有效
     */
    @Column(name = "state")
    private Long state;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;



}
