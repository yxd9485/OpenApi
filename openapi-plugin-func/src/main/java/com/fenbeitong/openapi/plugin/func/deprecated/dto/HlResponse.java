package com.fenbeitong.openapi.plugin.func.deprecated.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * module: 迁移Java 响应对象<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/4 11:55
 * @since 2.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class HlResponse<T> {

    /**
     * 状态码
     */
    int code;
    /**
     * 错误信息
     */
    String msg;
    /**
     * 响应结果
     */
    T data;
}
