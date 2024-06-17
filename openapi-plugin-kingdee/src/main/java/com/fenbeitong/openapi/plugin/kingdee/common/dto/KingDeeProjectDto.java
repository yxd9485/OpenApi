package com.fenbeitong.openapi.plugin.kingdee.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 金蝶项目信息DTO
 * @Auther zhang.peng
 * @Date 2021/6/15
 */
@Data
@NoArgsConstructor
public class KingDeeProjectDto {

    List<Project> projectList;

    @Data
    public static class Project{

        private String projectCode;

        private String projectName;

        private String userId;

        private String createTime;

        private String endTime;

        private String userName;

    }
}
