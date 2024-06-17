package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BeisenRankDTO
 * @Description 北森职级
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/16
 **/
@Data
public class BeisenRankDTO {
    @JsonProperty("scrollId")
    private String scrollId;
    @JsonProperty("isLastData")
    private boolean isLastData;
    @JsonProperty("total")
    private int total;
    @JsonProperty("data")
    private List<RankDto> data;
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;

    @Data
    public static class RankDto {
        @JsonProperty("oId")
        private String oId;
        @JsonProperty("name")
        private String name;
        @JsonProperty("pOIdOrgAdmin")
        private String poIdOrgAdmin;
        @JsonProperty("status")
        private int status;
        @JsonProperty("createdTime")
        private String createTime;
        @JsonProperty("modifiedTime")
        private String modifiedTime;
        @JsonProperty("broadType")
        private String broadType;
    }
}
