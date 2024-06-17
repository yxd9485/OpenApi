package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 获取档案数据任务dto
 * @author lizhen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveDataJobReqDTO {
    /**
     * 锁Key
     */
    @JsonProperty("lock_key")
    private String lockKey;

    private String host;

    @JsonProperty("access_key")
    private String accessKey;

    @JsonProperty("secret_key")
    private String secretKey;


    @JsonProperty("etl_config_id")
    private Long etlConfigId;

    @JsonProperty("ext_attr")
    private Map<String, Object> extAttr;

}
