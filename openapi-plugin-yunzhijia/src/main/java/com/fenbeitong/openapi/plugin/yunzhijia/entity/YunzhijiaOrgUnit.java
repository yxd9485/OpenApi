package com.fenbeitong.openapi.plugin.yunzhijia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hanshuqi on 2020/03/24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "yunzhijia_org_unit")
public class YunzhijiaOrgUnit {

    /**
     * 自增主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 企业ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 云之家部门ID
     */
    @Column(name = "YUNZHIJIA_ORG_ID")
    private String yunzhijiaOrgId;

    /**
     * 云之家父部门ID
     */
    @Column(name = "YUNZHIJIA_PARENT_ORG_ID")
    private String yunzhijiaParentOrgId;

    /**
     * 云之家部门名称
     */
    @Column(name = "YUNZHIJIA_ORG_NAME")
    private String yunzhijiaOrgName;

    /**
     * 部门状态 0：可用，1：不可用
     */
    @Column(name = "STATE")
    private Integer state;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
