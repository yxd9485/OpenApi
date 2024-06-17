package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName GenerateSign
 * @Description sign生成工具类
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/7/1 下午12:39
 **/
public class GenerateSign {
    public static String hmacSha1Encrypt(String content, String appSecret) throws Exception {
        byte[] keyBytes = appSecret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec localSecretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA1");
        Mac localMac = Mac.getInstance("HmacSHA1");
        localMac.init(localSecretKeySpec);
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        localMac.update(contentBytes);
        String localMacStr = Base64.encodeBase64String(localMac.doFinal());
        return URLEncoder.encode(localMacStr);
    }
}
