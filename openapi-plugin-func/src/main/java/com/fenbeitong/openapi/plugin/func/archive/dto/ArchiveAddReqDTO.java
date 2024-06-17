package com.fenbeitong.openapi.plugin.func.archive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ArchiveAddReqDto
 * @Description 新增档案
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/11/1 下午8:25
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveAddReqDTO {
    @JsonProperty("company_id")
    private String companyId;
    //档案编号
    @JsonProperty("code")
    private String code;
    //档案名称
    @JsonProperty("name")
    private String  name;
    //三方档案id
    @JsonProperty("third_archive_id")
    private String thirdArchiveId;
    //档案属性 1.成本中心
    @JsonProperty("archive_file")
    private Integer archiveFile;

}
