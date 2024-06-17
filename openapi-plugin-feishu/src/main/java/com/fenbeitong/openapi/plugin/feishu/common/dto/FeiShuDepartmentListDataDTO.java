package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lizhen
 * @date 2020/6/2
 */
@Data
public class FeiShuDepartmentListDataDTO {

    private Integer code;

    private String msg;

    private DepartmentSimpleList data;

    @Data
    public static class DepartmentSimpleList {

        @JsonProperty("has_more")
        private boolean hasMore;

        @JsonProperty("page_token")
        private String pageToken;

        @JsonProperty("items")
        private List<DepartmentDataInfo> items;

    }

    @Data
    public static class DepartmentDataInfo {

        private String id;

        private String name;

        @JsonProperty("parent_department_id")
        private String parentId;

        @JsonProperty("open_department_id")
        private String openDepartmentId;

        @JsonProperty("department_id")
        private String departmentId;
    }
}
