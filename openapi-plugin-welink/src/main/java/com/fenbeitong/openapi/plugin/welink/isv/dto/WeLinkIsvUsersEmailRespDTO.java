package com.fenbeitong.openapi.plugin.welink.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-04-20 16:17:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvUsersEmailRespDTO {

    private String code;

    private String message;

    private String userStatus;

    private String userId;

    private String deptCode;

    private String userNameCn;

    private String userNameEn;

    private String corpUserId;

    private String userEmail;

    private String creationTime;


}