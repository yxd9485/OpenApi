package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QiqiCostReqDTO
 * @Description 费用类别dto
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiCostReqDTO {
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
     * 说明
     */
    @JsonProperty("description")
    private String description;
    /**
     * 是否停用
     */
    @JsonProperty("isDisabled")
    private Boolean isDisabled;
    /**
     * 级次
     */
    private Integer level;
    /**
     * 是否叶级
     */
    private Boolean isLeaf;
    /**
     * 下级
     */
    private QiqiCostReqDTO childrenObject;
}
