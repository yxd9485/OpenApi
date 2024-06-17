package com.fenbeitong.openapi.plugin.feishu.common.dto.organizationv3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书新版自定义字段信息DTO
 * @author zhangpeng
 * @date 2022/4/20 11:58 上午
 */
@Data
public class CustomAttrsDTO {

    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private ValueDTO value;

    @Data
    public static class ValueDTO {
        @JsonProperty("text")
        private String text;
        @JsonProperty("option_value")
        private String optionValue;
        @JsonProperty("url")
        private String url;
    }

}
