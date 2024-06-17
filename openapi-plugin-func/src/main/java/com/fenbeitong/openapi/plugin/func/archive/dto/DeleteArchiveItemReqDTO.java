package com.fenbeitong.openapi.plugin.func.archive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassName DeleteArchiveItemReqDTO
 * @Description 批量删除档案项目
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/31 上午9:31
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteArchiveItemReqDTO {
    @JsonProperty("third_archive_id")
    @NotBlank(message = "【third_archive_id】不能为空")
    private String thirdArchiveId;
    @JsonProperty("id_list")
    @NotEmpty(message = "【id_list】不能为空")
    @Size(max = 50, message = "项目ID[id_list]最多为50条")
    private List<String> idList;
    @JsonProperty("archive_id")
    private String archiveId;
}
