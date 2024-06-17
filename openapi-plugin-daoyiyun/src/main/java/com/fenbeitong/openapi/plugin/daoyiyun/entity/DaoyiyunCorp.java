package com.fenbeitong.openapi.plugin.daoyiyun.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2022/06/02.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "daoyiyun_corp")
public class DaoyiyunCorp {

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 分贝通企业ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 道一云企业ID
     */
    @Column(name = "corp_id")
    private String corpId;

    /**
     * 租户密钥
     */
    @Column(name = "secret")
    private String secret;

    /**
     * 管理员账号
     */
    @Column(name = "account")
    private String account;

    /**
     * 应用ID
     */
    @Column(name = "application_id")
    private String applicationId;

    /**
     * 应用推送回调的token
     */
    @Column(name = "token")
    private String token;

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
