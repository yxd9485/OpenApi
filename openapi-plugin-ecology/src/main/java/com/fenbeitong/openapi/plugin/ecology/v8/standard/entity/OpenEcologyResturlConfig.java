package com.fenbeitong.openapi.plugin.ecology.v8.standard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by zhangpeng on 2021/12/31.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_ecology_resturl_config")
public class OpenEcologyResturlConfig {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 公司ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 泛微颁发的APPID，用于rest接口调用
     */
    @Column(name = "ecology_app_id")
    private String ecologyAppId;

    /**
     * 域名
     */
    @Column(name = "domain_name")
    private String domainName;

    /**
     * 接口地址
     */
    @Column(name = "url")
    private String url;

    /**
     * 员工自定义参数
     */
    @Column(name = "user_params")
    private String userParams;

    /**
     * 部门自定义参数
     */
    @Column(name = "department_params")
    private String departmentParams;

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
