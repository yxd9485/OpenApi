package com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * create on 2020-12-25 14:3:6
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FxiaokeGetUserInfoRespDTO {

    private Integer errorCode;

    private String errorMessage;

    private String openUserId;

    private String account;

    private String name;

    private String nickName;

    private boolean isStop;

    private String email;

    private String mobile;

    private String gender;

    private String position;

    private String profileImageUrl;

    private List<Long> departmentIds;

    private String qq;

    private String weixin;

    private String employeeNumber;

    private String hireDate;

    private String birthDate;

    private String startWorkDate;

    private Long createTime;

    private String leaderId;


}