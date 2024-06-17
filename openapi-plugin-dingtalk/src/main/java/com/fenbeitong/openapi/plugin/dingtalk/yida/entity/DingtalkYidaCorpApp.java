package com.fenbeitong.openapi.plugin.dingtalk.yida.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2021/09/06.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dingtalk_yida_corp_app")
public class DingtalkYidaCorpApp {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 分贝通企业ID
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

    /**
     * 企业ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 
     */
    @Column(name = "APP_KEY")
    private String appKey;

    /**
     * 
     */
    @Column(name = "APP_SECRET")
    private String appSecret;

    /**
     * APP名称
     */
    @Column(name = "APP_NAME")
    private String appName;

    /**
     * 
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
