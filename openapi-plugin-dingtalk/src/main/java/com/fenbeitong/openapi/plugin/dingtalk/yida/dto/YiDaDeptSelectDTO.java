package com.fenbeitong.openapi.plugin.dingtalk.yida.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 明细中的部门下拉框
 *
 * @author ctl
 * @date 2022/3/3
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class YiDaDeptSelectDTO implements Serializable {

    @JsonProperty("text")
    private TextDTO text;
    @JsonProperty("value")
    private String value;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class TextDTO {
        @JsonProperty("pureEn_US")
        private String pureenUs;
        @JsonProperty("en_US")
        private String enUs;
        @JsonProperty("zh_CN")
        private String zhCn;
        @JsonProperty("type")
        private String type;
    }

}
