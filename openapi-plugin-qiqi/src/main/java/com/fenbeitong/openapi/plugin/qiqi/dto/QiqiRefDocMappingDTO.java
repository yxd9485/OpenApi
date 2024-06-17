package com.fenbeitong.openapi.plugin.qiqi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QiqiRefDocMapping
 * @Description 来源档案
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/7
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QiqiRefDocMappingDTO {
    /**
     * ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 引用档案
     */
    @JsonProperty("refDoc")
    private RefDoc refDoc;

    @Data
    public static class RefDoc {
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
}
