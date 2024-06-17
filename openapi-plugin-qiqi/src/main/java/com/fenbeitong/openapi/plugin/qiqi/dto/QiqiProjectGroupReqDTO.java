package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QiqiProjectGroupReqDTO
 * @Description 项目分组dto
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/25
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiProjectGroupReqDTO {

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
     * 描述
     */
    @JsonProperty("descrtption")
    private String descrtption;
}
