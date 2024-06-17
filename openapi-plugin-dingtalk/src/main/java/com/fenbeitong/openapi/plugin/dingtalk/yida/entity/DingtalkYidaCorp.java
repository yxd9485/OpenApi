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
 * Created by lizhen on 2021/08/13.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dingtalk_yida_corp")
public class DingtalkYidaCorp {

    /**
     * 
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
     * 钉钉平台企业ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 易搭密钥
     */
    @Column(name = "ACCESS_KEY")
    private String accessKey;

    /**
     * 易搭密钥
     */
    @Column(name = "SECRET_KEY")
    private String secretKey;

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
