package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author lizhen
 * @date 2020/6/3
 */
@Data
public class FeiShuDepartmentInfoRespDTO {

    private Integer code;

    private String msg;

    private DepartmentInfoData data;

    @Data
    public static class DepartmentInfoData {

//        @JsonProperty("department_info")
        @JsonProperty("department")
        private FeiShuDepartmentSimpleListRespDTO.DepartmentInfo departmentInfo;

    }

}
