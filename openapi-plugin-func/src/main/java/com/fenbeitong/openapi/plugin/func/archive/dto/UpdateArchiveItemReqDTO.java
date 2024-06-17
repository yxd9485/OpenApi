package com.fenbeitong.openapi.plugin.func.archive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @ClassName UpdateArchiveDTO
 * @Description 自定义档案项目更新
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/30 下午3:09
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArchiveItemReqDTO {
    //所属三方档案ID
    @NotBlank(message = "所属三方档案ID[third_archive_id]不可为空")
    @JsonProperty("third_archive_id")
    private String thirdArchiveId;
    @NotBlank(message = "档案名称[archive_name]不可为空")
    @JsonProperty("archive_name")
    private String archiveName;
    //类型：1：新增  2：更新
    @NotNull(message = "[type]不可为空")
    private Integer type;
    @NotEmpty(message = "[archive_item_list]为必填")
    @JsonProperty("archive_item_list")
    @Size(max = 50, message = "项目列表[archive_item_list]最多为50条")
    private List<ArchiveItemReqDTO> archiveItemList;

}

