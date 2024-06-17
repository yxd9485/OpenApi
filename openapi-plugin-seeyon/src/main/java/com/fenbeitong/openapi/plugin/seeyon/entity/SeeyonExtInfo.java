package com.fenbeitong.openapi.plugin.seeyon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hanshuqi on 2020/05/12.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seeyon_ext_info")
public class SeeyonExtInfo {

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
     * 获取指定字段
     */
    @Column(name = "TARGET_COLUM")
    private String targetColum;
    /**
     * 属性取值字段，根据指定字段获取值，可以配置不同字段获取不同值
     */
    @Column(name = "MAP_KEY")
    private String mapKey;

    /**
     * 分贝通人员权限类型
     */
    @Column(name = "MAP_VALUE")
    private String mapValue;

    /**
     * 分贝人员权限类型
     */
    @Column(name = "ROLE_TYPE")
    private Integer roleType;

    /**
     * 状态属性：0:可用，1:不可用
     */
    @Column(name = "STATE")
    private String state;
    /**
     * 状态属性：0:可用，1:不可用
     */
    @Column(name = "TYPE")
    private Integer type;

    /**
     * 脚本属性
     */
    @Column(name = "SCRIP")
    private String scrip;

    /**
     * 扩展字段
     */
    @Column(name = "EXT_INFO")
    private String extInfo;

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
