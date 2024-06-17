package com.fenbeitong.openapi.plugin.kingdee.support.dto;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "kingdee")
@Component
public class KingdeeConfig {

    /**
     * 金蝶url前缀
     */
    private String url;

    /**
     * 金蝶登录的acctID
     */
    private String acctId;

    /**
     * 金蝶登录的用户名
     */
    private String userName;

    /**
     * 金蝶登录的密码
     */
    private String password;

    /**
     * 金蝶登录的lcid2052
     */
    private Long lcid;


    /**
     * 登录接口
     */
    private String login;

    /**
     * 查看接口
     */
    private String view;

    /**
     * 保存接口
     */
    private String save;

    /**
     * 提交接口
     */
    private String submit;


    /**
     * 审核接口
     */
    private String audit;

    /**
     * 状态改变接口
     */
    private String statusConver;

    /**
     * 单据查询接口
     */
    private String billQury;

    /**
     * 免密登陆使用的appId
     */
    private String appId;

    /**
     * 免密登陆使用的appSecret
     */
    private String appSecret;

    /**
     * 免密登陆接口
     */
    private String loginByAppSecret;
}
