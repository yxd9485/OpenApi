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
public class YiDaFormDetailRespDTO {

    private String gmtModified;

    private String creator;

    private String modifier;

    private UserDTO originator;

    private String title;

    private String gmtCreate;

    private String modelUuid;

    private Integer version;

    private String instValue;

    private UserDTO modifyUser;

    private String formInstId;

    private Map<String, Object> formData;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDTO {
        private String userId;

    }
}