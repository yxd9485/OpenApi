package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QIqiDepartmentReqDTO
 * @Description 企企部门数据同步
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/5/13 下午10:41
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiDepartmentReqDTO {
    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("id")
    private String id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("manager_id")
    private String managerId;
    @JsonProperty("level")
    private Integer level;
    @JsonProperty("disabledTime")
    private Long disabledTime;

}
