package com.fenbeitong.openapi.plugin.yunzhijia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 详细信息参考地址：
 * https://yunzhijia.com/cloudflow-openplatform/other/3002
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YunzhijiaApplyRespDTO {

    @JsonProperty("errorCode")
    private Integer errorCode;
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("error")
    private boolean error;
    @JsonProperty("data")
    private YunzhijiaApplyData data;

    @Data
    public static class YunzhijiaApplyData {
        //这次发起的表单模版唯一id
        @JsonProperty("formDefId")
        private String formDefId;
        // 这次发起的表单实例唯一id
        @JsonProperty("formInstId")
        private String formInstId;
        // 这次发起的流程实例唯一id
        @JsonProperty("flowInstId")
        private String flowInstId;
    }

}
