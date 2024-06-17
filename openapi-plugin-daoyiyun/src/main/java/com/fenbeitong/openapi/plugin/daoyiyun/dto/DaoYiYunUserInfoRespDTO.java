package com.fenbeitong.openapi.plugin.daoyiyun.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaoYiYunUserInfoRespDTO {

    private UserInfo data;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {

        private String account;

        private String defaultDepartmentId;

        private String defaultDepartmentName;

        private List<String> departmentIds;

        private Integer gender;

        private String id;

        private String name;

        private String telephone;

    }
}
