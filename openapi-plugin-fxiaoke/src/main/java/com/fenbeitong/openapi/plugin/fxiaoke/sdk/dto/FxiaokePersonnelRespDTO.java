package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: FxiaokeDepartmentSimpleListRespDTO</p>
 * <p>Description: 人员</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-01 16:52
 */
@Data
public class FxiaokePersonnelRespDTO {

    @JsonProperty("employees")
    private List<PersonnelInfo> employees;

    @JsonProperty("pageNumber")
    private Integer pageNumber;

    @JsonProperty("pageCount")
    private Integer pageCount;

    @JsonProperty("totalCount")
    private Integer totalCount;

    @JsonProperty("lastChangedTime")
    private String lastChangedTime;

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("errorDescription")
    private String errorDescription;

    @JsonProperty("traceId")
    private String traceId;

    @Data
    public static class PersonnelInfo {

        // 开放平台员工帐号
        @JsonProperty("openUserId")
        private String openUserId;

        // 员工账号
        @JsonProperty("account")
        private String account;

        // 员工账号
        @JsonProperty("name")
        private String name;

        // 员工昵称
        @JsonProperty("nickName")
        private String nickName;

        // 员工状态，如果为true,则表示此员工离职，否则，该员工状态为在职
        @JsonProperty("isStop")
        private Boolean isStop;

        @JsonProperty("email")
        private String email;

        @JsonProperty("mobile")
        private String mobile;


        // 员工性别：M(男) F(女)
        @JsonProperty("gender")
        private String gender;

        // 员工职位
        @JsonProperty("position")
        private String position;

        @JsonProperty("profileImageUrl")
        private String profileImageUrl;

        // 员工所属部门及其父部门ID列表
        @JsonProperty("departmentIds")
        private List<String> departmentIds;


        // 员工主属部门ID
        @JsonProperty("mainDepartmentId")
        private String mainDepartmentId;

        // 员工附属部门ID列表
        @JsonProperty("attachingDepartmentIds")
        private List<String> attachingDepartmentIds;

        @JsonProperty("qq")
        private String qq;

        @JsonProperty("weixin")
        private String weixin;

        @JsonProperty("employeeNumber")
        private String employeeNumber;

        @JsonProperty("hireDate")
        private String hireDate;

        @JsonProperty("birth_date")
        private String birthDate;

        @JsonProperty("startWorkDate")
        private String startWorkDate;

        @JsonProperty("createTime")
        private String createTime;

        @JsonProperty("leaderId")
        private String leaderId;


    }

}
