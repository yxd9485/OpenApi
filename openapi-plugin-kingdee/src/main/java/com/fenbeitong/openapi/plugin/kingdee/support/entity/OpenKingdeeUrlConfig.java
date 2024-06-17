package com.fenbeitong.openapi.plugin.kingdee.support.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 金蝶请求地址配置
 * Created by zhangpeng on 2021/06/04.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_kingdee_url_config")
public class OpenKingdeeUrlConfig {

    /**
     *
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
     * 金蝶登录的acctID
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
     * 金蝶登录的lcid2052
     */
    @Column(name = "lcid")
    private String lcid;

    /**
     * 金蝶appId
     */
    @Column(name = "appId")
    public String appId;

    /**
     * 金蝶秘钥
     */
    @Column(name = "appSecret")
    public String appSecret;

    /**
     * 金蝶tokenUrl
     */
    @Column(name = "tokenUrl")
    public String tokenUrl;

    /**
     * 金蝶
     */
    @Column(name = "iteamUrl")
    public String iteamUrl;

}
