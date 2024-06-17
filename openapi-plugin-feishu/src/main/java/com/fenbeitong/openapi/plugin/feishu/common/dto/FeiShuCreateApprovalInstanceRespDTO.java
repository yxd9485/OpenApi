package com.fenbeitong.openapi.plugin.feishu.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建实例
 * create on 2020-12-01 17:40:23
 * @author lizhen
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuCreateApprovalInstanceRespDTO {

    private Integer code;

    private String msg;

    private CreateInstanceData data;

    @Data
    public static class CreateInstanceData {
        @JsonProperty("instance_code")
        private String instanceCode;
    }
}