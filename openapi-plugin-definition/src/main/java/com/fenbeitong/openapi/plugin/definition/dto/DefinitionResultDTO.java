package com.fenbeitong.openapi.plugin.definition.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import lombok.Builder;
import lombok.Data;
import org.slf4j.MDC;

/**
 * 自助连接配置统一结果
 * Created by log.chang on 2019/12/14.
 */
@Data
@Builder
public class DefinitionResultDTO<T> {

    @JsonProperty("request_id")
    private String requestId;

    private Integer code;

    private String msg;

    private T data;

    public static DefinitionResultDTO error(int code, String msg) {
        return DefinitionResultDTO
                .builder()
                .code(code)
                .requestId(MDC.get("requestId"))
                .msg(msg).build();
    }

    public static DefinitionResultDTO error(String msg) {
        return error(RespCode.ERROR, msg);
    }

    public static <T> DefinitionResultDTO success(String msg, T data) {
        return DefinitionResultDTO
                .builder()
                .code(RespCode.SUCCESS)
                .msg(msg)
                .requestId(MDC.get("requestId"))
                .data(data).build();
    }

    public static <T> DefinitionResultDTO success(T data) {
        return success("success", data);
    }
}
