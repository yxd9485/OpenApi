package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create on 2020-12-28 14:1:3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeGetByNickNameRespDTO {

    private List<Employee> empList;

    private Integer errorCode;

    private String errorMessage;

    private String errorDescription;

    private String traceId;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Employee {

        private Integer enterpriseId;

        private String openUserId;

        private String account;

        private String fullName;

        private String name;

        private String status;

        private String mobile;

        private String leaderId;

        private String telephone;

        private String role;

        private String post;

        private String qq;

        private String email;

        private String gender;

        private String profileImage;

        private String description;

        private String weixin;

        private String msn;

        private String extensionNumber;

        private Mobilesetting mobileSetting;

        private String workingState;

        private boolean isActive;

        private List<Integer> mainDepartmentIds;

        private List<Integer> departmentIds;

        private List<String> departmentAsteriskIds;

        private List<String> employeeAsteriskIds;

        private String birthDate;

        private String hireDate;

        private String empNum;

        private String startWorkDate;

        private Long stopTime;

        private Long createTime;

        private Long updateTime;

        private String nameSpell;

        private String nameOrder;
    }

    /**
     * create on 2020-12-28 14:1:3
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Mobilesetting {

        private String mobileStatus;

        private List<String> departmentIds;

        private List<String> employeeIds;

    }

}