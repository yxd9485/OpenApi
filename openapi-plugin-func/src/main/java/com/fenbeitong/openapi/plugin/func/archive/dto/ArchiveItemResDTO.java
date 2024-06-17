package com.fenbeitong.openapi.plugin.func.archive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ArchiveItemResDTo
 * @Description 自定义档案新增出参
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/31 下午5:49
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveItemResDTO {
    @JsonProperty("error_code")
    private Integer errorCode;
    @JsonProperty("error_msg")
    private String errorMsg;
    private String id;
    @JsonProperty("code")
    private String code;
    //档案项目名称
    private String name;
    @JsonProperty("third_project_id")
    private String thirdProjectId;
}
