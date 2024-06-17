package com.fenbeitong.openapi.plugin.welink.isv.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * create on 2020-04-17 16:51:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeLinkIsvUsersListRespDTO {

    private String code;

    private String message;

    private Integer pageNo;

    private Integer pages;

    private String pageSize;

    private Integer total;

    private List<WeLinkIsvUserInfo> data;

    @Data
    public static class WeLinkIsvUserInfo {

        private String userStatus;

        private String userId;

        private String deptCode;

        private String deptNameCn;

        private String deptNameEn;

        private String userNameCn;

        private String userNameEn;

        private String creationTime;
    }
}