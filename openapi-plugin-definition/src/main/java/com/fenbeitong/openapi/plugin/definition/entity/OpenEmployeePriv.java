package com.fenbeitong.openapi.plugin.definition.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by xiaowei on 2020/05/19.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_employee_priv")
public class OpenEmployeePriv {

    /**
     * 主键ID
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
     * 标识不同场景
     */
    @Column(name = "SCENE")
    private String scene;

    /**
     * 具体权限规则信息
     */
    @Column(name = "PRIV_JSON_DATA")
    private String privJsonData;

    /**
     * 角色类型，不同角色对应不同的权限和规则信息
     */
    @Column(name = "ROLE_TYPE")
    private Long roleType;

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
