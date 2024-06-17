package com.fenbeitong.openapi.plugin.dingtalk.yida.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * create on 2021-08-13 16:52:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YiDaApplyDetailRespDTO {

    private String processInstanceId;

    private String gmtModified;

    private String formUuid;

    private Map<String, Object> data;

    private OriginatorDTO originator;

    private String gmtCreate;

    private String title;

    private String instanceStatus;

    private Integer version;

    private String approvedResult;

    private String processCode;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OriginatorDTO {
        private String userId;

    }
}