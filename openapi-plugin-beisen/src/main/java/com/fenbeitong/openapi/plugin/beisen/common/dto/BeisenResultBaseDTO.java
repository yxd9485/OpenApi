package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: BeisenResultBaseDTO<p>
 * <p>Description: 北森返回结果基类<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/9/12 19:52
 */
@Data
public class BeisenResultBaseDTO<T> {
    @JsonProperty("scrollId")
    private String scrollId;
    @JsonProperty("isLastData")
    private boolean isLastData;
    @JsonProperty("total")
    private int total;
    @JsonProperty("data")
    private List<T> data;
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;
}
