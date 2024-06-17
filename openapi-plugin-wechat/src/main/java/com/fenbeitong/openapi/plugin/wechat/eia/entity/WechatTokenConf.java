package com.fenbeitong.openapi.plugin.wechat.eia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by huangsiyuan on 2020/02/26.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_wechat_token_conf")
public class WechatTokenConf {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

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
     * 请求方法
     */
    @Column(name = "REQUEST_METHOD")
    private String requestMethod;

    /**
     * 请求地址
     */
    @Column(name = "REQUEST_URL")
    private String requestUrl;

    /**
     * 请求体
     */
    @Column(name = "REQUEST_BODY")
    private String requestBody;

    /**
     * 类名
     */
    @Column(name = "CLASS_NAME")
    private String className;

    /**
     * token
     */
    @Column(name = "TOKEN_EXPRESS")
    private String tokenExpress;

}
