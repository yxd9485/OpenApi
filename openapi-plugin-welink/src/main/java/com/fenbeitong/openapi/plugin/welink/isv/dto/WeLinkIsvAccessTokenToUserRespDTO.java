package com.fenbeitong.openapi.plugin.welink.isv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-04-15 19:17:18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvAccessTokenToUserRespDTO {

    private String code;

    private String message;

    private String userId;

    private String tenantId;

}