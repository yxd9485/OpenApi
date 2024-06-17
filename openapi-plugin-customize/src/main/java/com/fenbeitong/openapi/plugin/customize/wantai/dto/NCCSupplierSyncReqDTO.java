package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


/**
 * 供应商同步入参
 * @author zhangjindong
 * @date 2022/9/21 8:21 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NCCSupplierSyncReqDTO {

    @JsonProperty("lock_key")
    private String lockKey;

    private String host;

    @JsonProperty("access_key")
    private String accessKey;

    @JsonProperty("secret_key")
    private String secretKey;

    @JsonProperty("ext_attr")
    private Map<String, Map<String,String>> extAttr;
}
