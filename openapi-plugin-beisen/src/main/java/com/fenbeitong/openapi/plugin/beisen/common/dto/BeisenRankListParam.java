package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName BeisenRankListParam
 * @Description 北森职级参数
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/16
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeisenRankListParam {

    @JsonProperty("timeWindowQueryType")
    private int timeWindowQueryType;
    @JsonProperty("startTime")
    private String startTime;
    @JsonProperty("stopTime")
    private String stopTime;
    @JsonProperty("capacity")
    private int capacity;
    @JsonProperty("columns")
    private String[] columns;
    @JsonProperty("extQueries")
    private List<BeisenExtQuery> extQueries;
    @JsonProperty("isWithDeleted")
    private boolean isWithDeleted;
    @JsonProperty("enableTranslate")
    private boolean enableTranslate;
    @JsonProperty("scrollId")
    private String scrollId;

    @Data
    @Builder
    public static class BeisenExtQuery {
        @JsonProperty("fieldName")
        private String fieldName;
        @JsonProperty("queryType")
        private int queryType;
        @JsonProperty("values")
        private String[] values;
    }
}
