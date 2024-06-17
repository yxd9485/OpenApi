package com.fenbeitong.openapi.plugin.qiqi.util;

import com.q7link.openapi.utils.AwsSignUtils;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BuildHeaderUtil
 * @Description 请求头构建
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/7/21
 **/
public class BuildHeaderUtil {
    /**
     * 构建企企签名请求头
     *
     * @param accessKeyId 公钥
     * @param openId      三方企业id
     * @param timestamp   当前时间戳
     * @param host        ip
     * @return headers    构建好的签名请求头
     */
    public static Map<String, String> buildSignHeaders(String accessKeyId, String openId, Long timestamp, String host) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Key-Id", accessKeyId);
        headers.put("Open-Id", openId);
        headers.put("X-Amz-Date", AwsSignUtils.DATE_FORMAT_ISO.format(timestamp));
        headers.put("host", host.replace("https://", "").replace("http://", ""));
        return headers;
    }

    /**
     * 构建企企http请求头
     *
     * @param accessKeyId   公钥
     * @param openId        三方企业id
     * @param timestamp     当前时间戳
     * @param host          ip
     * @param authorization 授权信息
     * @return httpHeaders 构建好的http请求头
     */
    public static HttpHeaders buildHttpHeaders(String accessKeyId, String openId, Long timestamp, String host, String authorization) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Access-Key-Id", accessKeyId);
        httpHeaders.add("Open-Id", openId);
        httpHeaders.add("X-Amz-Date", AwsSignUtils.DATE_FORMAT_ISO.format(timestamp));
        httpHeaders.add("host", host.replace("https://", "").replace("http://", ""));
        httpHeaders.add("Authorization", authorization);
        return httpHeaders;
    }
}
