package com.fenbeitong.openapi.plugin.kingdee.support.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by ctl on 2021/07/01.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_third_kingdee_config")
public class OpenThirdKingdeeConfig {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     * 公司ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 金蝶url前缀
     */
    @Column(name = "url")
    private String url;

    /**
     * 帐套id
     */
    @Column(name = "acct_id")
    private String acctId;

    /**
     * 金蝶登录的用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 金蝶登录的密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 语言id 中文2052，英文1033，繁体3076
     */
    @Column(name = "lcid")
    private String lcid;

    /**
     * 免密登陆的appId
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 免密登陆的appSecret
     */
    @Column(name = "app_secret")
    private String appSecret;

    /**
     * 扩展字段,json格式存储
     */
    @Column(name = "expand_info")
    private String expandInfo;


}
