package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: DepDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-31 10:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataDto {
    @JsonProperty("code")
    private String code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("ext")
    private List<String> ext;

    @JsonProperty("result")
    private Result result;

    @Data
    public static class Result {
        @JsonProperty("totalSize")
        private Integer totalSize;

        @JsonProperty("count")
        private Integer count;

        @JsonProperty("records")
        private  Object records;

    }
}

