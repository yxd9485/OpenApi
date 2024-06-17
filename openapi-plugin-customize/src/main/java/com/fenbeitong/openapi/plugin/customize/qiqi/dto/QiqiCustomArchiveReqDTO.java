package com.fenbeitong.openapi.plugin.customize.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName QiqiCustomArchiveReqDto
 * @Description 自定义档案dto
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiCustomArchiveReqDTO {

    /**
     * 上级信息
     */
    @JsonProperty("parentId")
    private String parentId;

    /**
     * ID
     */
    @JsonProperty("id")
    private String id;
    /**
     * 编码
     */
    @JsonProperty("code")
    private String code;
    /**
     * 名称
     */
    @JsonProperty("name")
    private String name;
    /**
     * 停用时间(停用日期的时间戳)
     */
    @JsonProperty("disabledTime")
    private Long disabledTime;

    /**
     * 来源档案
     */
    @JsonProperty("refDocMappingsObject")
    private List<QiqiRefDocMappingDTO> refDocMappingsObject;

}
