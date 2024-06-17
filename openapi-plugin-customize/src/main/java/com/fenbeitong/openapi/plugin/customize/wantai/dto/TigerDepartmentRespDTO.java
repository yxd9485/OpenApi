package com.fenbeitong.openapi.plugin.customize.wantai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * tiger部门同步返回DTO
 *
 * @author lizhen
 */
@Data
public class TigerDepartmentRespDTO {

    @JsonProperty("trace_id")
    private String traceId;

    private Integer code;

    private String msg;

    @JsonProperty("request_id")
    private String requestId;

    public TigerDepartmentRespData data;


    @Data
    public static class TigerDepartmentRespData {
        List<TigerDepartmentsResp> departments;
    }


    @Data
    public static class TigerDepartmentsResp {
        @JsonProperty("third_id")
        private String thirdId;
        @JsonProperty("error_msg")
        private String errorMsg;
    }
}
