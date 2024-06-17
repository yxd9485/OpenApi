package com.fenbeitong.openapi.plugin.func.sso.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>Title: OpenYufuSsoConfig</p>
 * <p>Description: 玉符单点登录配置实体类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/26 7:42 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_yufu_sso_config")
public class OpenYufuSsoConfig {

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
     * 平台类型
     */
    @Column(name = "PLATFORM_TYPE")
    private Integer platformType;

    /**
     * 租户
     */
    @Column(name = "TENANT")
    private String tenant;

    /**
     * issuer
     */
    @Column(name = "ISSUER")
    private String issuer;

    /**
     * audience
     */
    @Column(name = "AUDIENCE")
    private String audience;

    /**
     * 用户名类型 1:手机号;2:邮箱
     */
    @Column(name = "USER_NAME_TYPE")
    private Integer userNameType;

    /**
     * 公钥key
     */
    @Column(name = "PUBLIC_KEY")
    private String publicKey;


}
