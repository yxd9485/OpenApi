package com.fenbeitong.openapi.plugin.func.deprecated.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * module: openapi-java 响应示例基础类<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/5 15:05
 * @since 2.0
 */
@Data
public class OpenResponseResultEntity<T> {

    @JsonProperty("request_id")
    private String requestId;

    private Integer code;

    private Integer type;

    private String title;

    private String msg;

    private T data;

}
