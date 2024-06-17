package com.fenbeitong.openapi.plugin.qiqi.util;

import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试aws签名工具
 *
 * @author yankunjie
 */
@Component
@Slf4j
public class AwsSignUtils {

    @Value("${qiqi.host}")
    private  String qiqiHost;

    private static String host=null;

    @PostConstruct
    public void getHost(){
        host = this.qiqiHost;
    }

    public static String getSign(String secret,String openId,String accessKeyId,String path,String data) throws Exception {

        long timestamp = System.currentTimeMillis();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Key-Id", accessKeyId);
        headers.put("Open-Id", openId);
        headers.put("X-Amz-Date", com.q7link.openapi.utils.AwsSignUtils.DATE_FORMAT_ISO.format(timestamp));
        headers.put("host", host.replace("https://", "").replace("http://", ""));
        Map<String, String> body = Collections.singletonMap("json", data);
        Map<String, String> signForHeader = com.q7link.openapi.utils.AwsSignUtils.geneSignForHeader(accessKeyId, secret, timestamp, "POST", path, null, headers, body);

        log.info(String.format("签名结果：%s", signForHeader.get("Authorization")));
        return StringUtils.obj2str(signForHeader.get("Authorization"));
    }
}
