package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunzhijiaEmployeeDTO {

    //人员的openid
    @JsonProperty("openId")
    private String openId;
    //审批单详情接口中人员ID字段
    @JsonProperty("oid")
    private String oid;
    //姓名
    @JsonProperty("name")
    private String name;
    //头像URL
    @JsonProperty("photoUrl")
    private String photoUrl;
    //手机号码
    @JsonProperty("phone")
    private String phone;
    //是否在通讯录中隐藏手机号码,0: 不隐藏; 1: 隐藏,默认为0
    @JsonProperty("isHidePhone")
    private String isHidePhone;
    //邮箱
    @JsonProperty("email")
    private String email;
    //组织长名称
    @JsonProperty("department")
    private String department;
    //组织id
    @JsonProperty("orgId")
    private String orgId;
    //企业工号
    @JsonProperty("jobNo")
    private String jobNo;
    //职位
    @JsonProperty("jobTitle")
    private String jobTitle;
    //性别,0: 不确定; 1: 男; 2: 女
    @JsonProperty("gender")
    private int gender;
    //状态 0: 注销，1: 正常，2: 禁用
    @JsonProperty("status")
    private int status;
    //是否部门负责人 0:否， 1：是
    @JsonProperty("orgUserType")
    private int orgUserType;
    @JsonProperty("contact")
    private String contact;


}
