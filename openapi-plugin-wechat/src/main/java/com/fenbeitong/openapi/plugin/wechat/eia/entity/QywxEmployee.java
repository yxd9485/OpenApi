package com.fenbeitong.openapi.plugin.wechat.eia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "qywx_employee")
public class QywxEmployee {

    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 企业微信公司ID，根据公司ID区分不同公司人员数据
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 企业微信人员ID
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 企业微信人员姓名
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 企业微信部门集合，企业微信部门包含多部门，为集合
     */
    @Column(name = "DEPARTMENT")
    private String department;

    /**
     * （企业微信要求手机号和邮箱选填一个即可）
     */
    @Column(name = "MOBILE")
    private String mobile;

    /**
     * 企业微信人员性别
     */
    @Column(name = "GENDER")
    private String gender;

    /**
     * 1表示启用的成员，0表示被禁用
     */
    @Column(name = "ENABLE")
    private Integer enable;

    /**
     * 1:已激活，2:已禁用，4:未激活 已激活代表已激活企业微信或已关注微工作台
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 扩展属性
     */
    @Column(name = "EXTATTR")
    private String extattr;

    /**
     * 职位
     */
    @Column(name = "POSITION")
    private String position;

    /**
     * 在所在的部门内是否为上级
     */
    @Column(name = "IS_LEADER_IN_DEPT")
    private String isLeaderInDept;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "AVATAR")
    private String avatar;
    @Column(name = "THUMB_AVATAR")
    private String thumbAvatar;
    @Column(name = "TELEPHONE")
    private String telephone;
    @Column(name = "ALIAS")
    private String alias;
    @Column(name = "QR_CODE")
    private String qrCode;
    @Column(name = "EXTERNAL_PROFILE")
    private String externalProfile;
    @Column(name = "EXTERNAL_POSITION")
    private String externalPosition;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

}
