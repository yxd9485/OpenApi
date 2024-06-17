package com.fenbeitong.openapi.plugin.job.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.job.common.MethodParamEnum;
import com.fenbeitong.openapi.plugin.job.dto.HttpJobParamDTO;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.job.common.ContentTypeParamEnum;
import com.google.common.collect.Maps;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 通用的JobHandler<br/>
 * 解析xxl-job-admin中配置的参数，并调用对应的http接口
 * 参数示例：{@link HttpJobParamDTO}
 *
 * @author ctl
 * @date 2022/7/6
 */
@Component
public class HttpJobHandler {

    /**
     * header的必传key contentType
     */
    public final static String CONTENT_TYPE = "contentType";

    /**
     * 很多老接口返回的直接是success字符串 需要兼容
     */
    public final static String SUCCESS = "success";

    /**
     * 很多老接口返回的直接是ok字符串 需要兼容
     */
    public final static String OK = "ok";

    @XxlJob("openHttpJobHandler")
    public void openHttpJobHandler() {
        // 获取xxl-job-admin配置的参数
        String param = getParam();
        // 解析参数 转成对象
        HttpJobParamDTO httpJobParamDTO = parseParam(param);
        // 校验必传参数是否合规
        verify(httpJobParamDTO);
        // 执行远程请求
        remoteRequest(httpJobParamDTO);
    }

    /**
     * 根据参数判断 不同类型执行远程请求<br/>
     * 1.get请求 url拼接参数 或 @PathVariable传参<br/>
     * 2.post请求 application/json 传参<br/>
     * 3.post请求 application/x-www-form-urlencoded 传参<br/>
     *
     * @param httpJobParamDTO 入参
     */
    private void remoteRequest(HttpJobParamDTO httpJobParamDTO) {
        String method = httpJobParamDTO.getMethod();
        String url = httpJobParamDTO.getUrl();
        Map<String, Object> header = httpJobParamDTO.getHeader();
        if (MethodParamEnum.isGet(method)) {
            // get请求
            parseResult(url, RestHttpUtils.get(url, getHttpHeaders(header), Maps.newHashMap()));
        } else if (MethodParamEnum.isPost(method)) {
            // post请求时 需校验contentType
            verifyContentType(header);
            // 判断是json还是form
            if (ContentTypeParamEnum.isJson(header.get(CONTENT_TYPE).toString())) {
                // json传参
                parseResult(url, RestHttpUtils.postJson(url, getHttpHeaders(header), JsonUtils.toJson(httpJobParamDTO.getBody())));
            } else if (ContentTypeParamEnum.isForm(header.get(CONTENT_TYPE).toString())) {
                // form传参
                parseResult(url, RestHttpUtils.postForm(url, getHttpHeaders(header), getFormBody(httpJobParamDTO.getBody())));
            } else {
                // post请求中 json或form之外的 做失败处理
                handleFail("post请求未传header.contentType 或 contentType格式不兼容:" + JsonUtils.toJson(header));
            }
        } else {
            // post或get之外的 做失败处理
            handleFail("不兼容的method:" + method);
        }
    }

    /**
     * 校验contentType
     *
     * @param header 入参的header
     */
    private void verifyContentType(Map<String, Object> header) {
        if (ObjectUtils.isEmpty(header) || ObjectUtils.isEmpty(header.get(CONTENT_TYPE)) || !ContentTypeParamEnum.verify(header.get(CONTENT_TYPE).toString())) {
            handleFail("post请求未传header.contentType 或 contentType格式不兼容:" + JsonUtils.toJson(header));
        }
    }

    /**
     * 校验必须的参数 url和method
     *
     * @param httpJobParamDTO 入参对象
     */
    private void verify(HttpJobParamDTO httpJobParamDTO) {
        String url = httpJobParamDTO.getUrl();
        if (StringUtils.isBlank(url)) {
            handleFail("url不能为空:" + url);
        }
        String method = httpJobParamDTO.getMethod();
        if (StringUtils.isBlank(method) || !MethodParamEnum.verify(method)) {
            handleFail("不兼容的method:" + method);
        }
    }

    /**
     * 将参数解析成对象
     *
     * @param param 从xxl-job配置的参数
     * @return 解析后的对象
     */
    private HttpJobParamDTO parseParam(String param) {
        HttpJobParamDTO httpJobParamDTO = JsonUtils.toObj(param, HttpJobParamDTO.class);
        if (ObjectUtils.isEmpty(httpJobParamDTO)) {
            handleFail("参数解析异常，格式有误:" + param);
        }
        return httpJobParamDTO;
    }

    /**
     * 从xxl-job上下文获取配置的参数
     *
     * @return 配置的参数
     */
    private String getParam() {
        String param = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(param)) {
            handleFail("参数为空");
        }
        return param;
    }

    /**
     * 处理失败情况<br/>
     * 1.打印xxl-job日志<br/>
     * 2.xxl-job状态设为失败<br/>
     * 3.抛出异常阻断业务<br/>
     *
     * @param msg 错误信息
     */
    private void handleFail(String msg) {
        XxlJobHelper.log(msg);
        XxlJobHelper.handleFail();
        throw new FinhubException(RespCode.ARGUMENT_ERROR, msg);
    }

    /**
     * 解析结果 失败抛出异常<br/>
     * 判断逻辑：<br/>
     * 响应result字符串为空判定成功 兼容老接口返回void<br/>
     * 响应result不为空 且 为忽略大小写的"success"时 判定成功<br/>
     * 响应result不为空 且 能解析成code msg data对象 且 code为0 判定成功<br/>
     * 其余均判定失败
     *
     * @param url    调用的接口地址
     * @param result 响应结果
     */
    private void parseResult(String url, String result) {
        if (StringUtils.isBlank(result)) {
            // 响应result字符串为空判定成功 兼容老接口返回void
            return;
        }
        // 老接口很多直接返回的"success"字符串 需要兼容
        if (SUCCESS.equalsIgnoreCase(result) || OK.equalsIgnoreCase(result)) {
            // 成功 直接return
            return;
        }
        OpenApiResponseDTO<?> res = JsonUtils.toObj(result, new TypeReference<OpenApiResponseDTO<?>>() {
        });
        if (ObjectUtils.isEmpty(res) || !res.success()) {
            // 非code msg data 或 code不为0 判定为失败
            handleFail("失败的响应结果,url:" + url + ",result:" + result);
        }
    }

    /**
     * 转换成form传参需要的map
     *
     * @param body 入参的body
     * @return form传参需要的map
     */
    private MultiValueMap<String, Object> getFormBody(Map<String, Object> body) {
        MultiValueMap<String, Object> formBody = new LinkedMultiValueMap<>();
        if (!ObjectUtils.isEmpty(body)) {
            body.forEach(formBody::add);
        }
        return formBody;
    }

    /**
     * 转换成httpHeaders
     *
     * @param header 入参的header结构
     * @return httpHeaders
     */
    private HttpHeaders getHttpHeaders(Map<String, Object> header) {
        HttpHeaders headers = new HttpHeaders();
        if (!ObjectUtils.isEmpty(headers)) {
            header.forEach((k, v) ->
                headers.add(k, v.toString()));
        }
        return headers;
    }

}
