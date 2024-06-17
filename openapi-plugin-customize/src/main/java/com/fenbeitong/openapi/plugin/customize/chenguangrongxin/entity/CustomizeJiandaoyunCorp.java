package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName CustomizeJiandaoyunCorp
 * @Description 简道云企业配置表
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/4
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jiandaoyun_corp")
public class CustomizeJiandaoyunCorp {
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
     * 公司名称
     */
    @Column(name = "company_name")
    private String companyName;

    /**
     * 三方公司id
     */
    @Column(name = "corp_id")
    private String corpId;

    /**
     * key
     */
    @Column(name = "app_key")
    private String appKey;

    /**
     * 状态（0-停用，1-启用）
     */
    @Column(name = "state")
    private Integer state;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;
}
