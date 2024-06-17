package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName CostAttributionsDTO
 * @Description 费用归属信息
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/9/21
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostAttributionsDTO {
    /**
     * 费用归属类型
     */
    @JsonProperty("type")
    private String type;

    /**
     * 自定义档案id
     */
    @JsonProperty("third_archive_id")
    private String thirdArchiveId;

    /**
     * 自定义档案名称
     */
    @JsonProperty("archive_name")
    private String archiveName;

    /**
     * 费用归属明细
     */
    @JsonProperty("details")
    private List<CostAttributionDetail> details;

    @Data
    public static class CostAttributionDetail {
        /**
         * 三方费用归属id
         */
        @JsonProperty("third_id")
        private String thirdId;

        /**
         * 费用归属名称
         */
        @JsonProperty("name")
        private String name;
    }
}
