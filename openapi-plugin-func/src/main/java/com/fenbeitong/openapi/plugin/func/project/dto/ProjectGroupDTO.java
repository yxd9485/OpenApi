package com.fenbeitong.openapi.plugin.func.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 项目分组
 * @Author: xiaohai
 * @Date: 2021/10/25 上午10:06
 */
@Data
public class ProjectGroupDTO {

    /**
     * 三方分组id
     */
    @JsonProperty("third_group_id")
    private String thirdGroupId;

    /**
     * 项目分组名称
     */
    @JsonProperty("group_name")
    private String groupName;

    /**
     * 项目分组描述
     */
    @JsonProperty("group_desc")
    private String groupDesc;

    /**
     *项目id集合
     */
    @JsonProperty("center_ids")
    private List<String> centerIdList;

}
