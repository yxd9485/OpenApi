package com.fenbeitong.openapi.plugin.wechat.eia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "qywx_org_unit")
public class QywxOrgUnit implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 企业微信公司ID，用于区分不同公司进行数据同步
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 企业微信部门ID
     */
    @Column(name = "QYWX_ORG_ID")
    private Long qywxOrgId;

    /**
     * 企业微信父部门ID
     */
    @Column(name = "QYWX_PARENT_ORG_ID")
    private Long qywxParentOrgId;

    /**
     * 企业微信部门名称
     */
    @Column(name = "QYWX_ORG_NAME")
    private String qywxOrgName;

    /**
     * 企业微信部门排序
     */
    @Column(name = "QYWX_ORG_ORDER")
    private Integer qywxOrgOrder;

    /**
     * 部门状态，0：可用，1：不可用
     */
    @Column(name = "STATE")
    private Integer state;


    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 部门更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

}
