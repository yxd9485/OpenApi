package com.fenbeitong.openapi.plugin.feishu.isv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2020/06/01.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "feishu_isv_company")
public class FeishuIsvCompany {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 分贝通企业ID
     */
    @Column(name = "COMPANY_ID")
    private String companyId;

    /**
     * 企业三方ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 企业名称
     */
    @Column(name = "COMPANY_NAME")
    private String companyName;

    /**
     * 授权状态，0取消授权，1已授权，2已过期
     */
    @Column(name = "STATE")
    private Integer state;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}
