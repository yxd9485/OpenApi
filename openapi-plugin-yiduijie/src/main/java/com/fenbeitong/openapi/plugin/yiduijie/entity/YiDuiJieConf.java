package com.fenbeitong.openapi.plugin.yiduijie.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>Title: YiDuiJieConf</p>
 * <p>Description: 财务部门服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 3:09 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "yiduijie_conf")
public class YiDuiJieConf {

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
     * 子账号编号
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 部署的客户端编号
     */
    @Column(name = "CLIENT_ID")
    private String clientId;

    /**
     * 应用实例编号
     */
    @Column(name = "APP_ID")
    private String appId;

    /**
     * 备注
     */
    @Column(name = "REMARK")
    private String remark;

}
