package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QiqiRankReqDTO
 * @Description 职级dto
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiRankReqDTO {
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
}
