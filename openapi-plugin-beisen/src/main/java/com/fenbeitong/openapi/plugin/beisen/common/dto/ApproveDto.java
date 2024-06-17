package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: ApproveDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/9/28 4:08 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveDto {
    @JsonProperty("pageIndex")
    public Integer pageIndex;

    @JsonProperty("pageSize")
    public Integer pageSize;

    @JsonProperty("totalCount")
    public Integer totalCount;

    @JsonProperty("results")
    public List<Results> results;

    @Data
    public static class Results {
        @JsonProperty("id")
        public String id;
        @JsonProperty("type")
        public Integer type;
        @JsonProperty("third_id")
        public String thirdId;
    }
}
