package com.fenbeitong.openapi.plugin.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * xxl-job-admin中配置的参数
 *
 * @author ctl
 * @date 2022/7/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpJobParamDTO implements Serializable {

    /**
     * 要调用的接口地址
     * 如：http://openapi-dev.fenbeijinfu.com/test/t1
     */
    private String url;

    /**
     * 请求方式
     * 如：post
     */
    private String method;

    /**
     * 调用接口时的header
     * post请求时 contentType必传
     * 如：application/json
     */
    private Map<String, Object> header;

    /**
     * 调用接口时的参数 json结构 具体内容根据每个接口参数来配置
     */
    private Map<String, Object> body;
}
