package com.fenbeitong.openapi.plugin.func.archive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName ArchiveItemReqDTO
 * @Description 自定义档案项目
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/31 下午4:29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveItemReqDTO {

    //档案项目编号
    @NotBlank(message = "档案项目编号[code]不可为空")
    @JsonProperty("code")
    private String code;
    //档案项目名称
    @NotBlank(message = "档案项目名称[name]不可为空")
    private String name;
    //所属三方档案ID
//    @NotBlank(message = "所属三方档案ID[third_archive_id]不可为空")
    @JsonProperty("third_archive_id")
    private String thirdArchiveId;
    // 所属三方上级项目id
    @JsonProperty("third_parent_id")
    private String thirdParentId;
    // 所属三方项目id
    @JsonProperty("third_project_id")
    private String thirdProjectId;
    //档案项目状态 1启用 0停用
    private Integer state;
    //1:全部人员可用，2：部分人员可用
    @JsonProperty("use_range")
    private Integer useRange;
    //成员(人)
    @JsonProperty("third_member_ids")
    private List<String> thirdMemberIds;
    //成员（部门）
    @JsonProperty("third_member_dept_ids")
    private List<String> thirdMemberDeptIds;
    //公司id
    @JsonProperty("company_id")
    private String companyId;


}
