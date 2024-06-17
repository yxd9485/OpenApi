package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.support.customform.dto.CustomFormListResDTO;
import lombok.Data;


/**
 * @author zhangjindong
 * @date 2022/9/21 8:21 PM
 */
@Data
public class TigerFormListRespDTO {

    @JsonProperty("trace_id")
    private String traceId;

    private Integer code;

    private String msg;

    @JsonProperty("request_id")
    private String requestId;

    public CustomFormListResDTO data;
}
