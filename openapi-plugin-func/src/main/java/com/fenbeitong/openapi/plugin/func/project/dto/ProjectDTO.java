package com.fenbeitong.openapi.plugin.func.project.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: ProjectDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-11 14:43
 */
@Data
public class ProjectDTO {

    public String companyId;

    public List<element> projectList;


    @Data
    public static class element {
        /**
         * 项目编号
         */
        public String projectNo;

        /**
         * 项目名称
         */
        public String name;
    }
}
