package com.fenbeitong.openapi.plugin.welink.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author lizhen
 * @date 2020/6/4
 */
@Slf4j
public class WeLinkIsvMarketEncryptUtils {

    /**
     * 校验通知消息的合法性
     *
     * @param request       http请求通知消息
     * @param accessKey     接入码
     * @param encryptLength 加密长度
     * @return 验证结果
     */

    public static boolean verificateRequestParams(javax.servlet.http.HttpServletRequest request,

                                                  String accessKey, int encryptLength) {

        //解析出url内容

        Map<String, String[]> paramsMap = request.getParameterMap();

        String timeStamp = null;

        String authToken = null;

        String[] timeStampArray = paramsMap.get("timeStamp");

        if (null != timeStampArray && timeStampArray.length > 0) {

            timeStamp = timeStampArray[0];

        }

        String[] authTokenArray = paramsMap.get("authToken");

        if (null != authTokenArray && authTokenArray.length > 0) {

            authToken = authTokenArray[0];

        }

        //对剩下的参数进行排序，拼接成加密内容

        Map<String, String[]> sortedMap = new TreeMap<String, String[]>();

        sortedMap.putAll(paramsMap);

        sortedMap.remove("authToken");

        StringBuffer strBuffer = new StringBuffer();

        Set<String> keySet = sortedMap.keySet();

        Iterator<String> iter = keySet.iterator();

        while (iter.hasNext()) {

            String key = iter.next();

            String value = sortedMap.get(key)[0];

            strBuffer.append("&").append(key).append("=").append(value);

        }

        //修正消息体,去除第一个参数前面的&

        String reqParams = strBuffer.toString().substring(1);

        String key = accessKey + timeStamp;

        String signature = null;

        try {

            signature = generateResponseBodySignature(key, reqParams);

        } catch (InvalidKeyException | NoSuchAlgorithmException

                | IllegalStateException | UnsupportedEncodingException e) {

            log.error("验证签名失败：", e);

        }

        return authToken.equals(signature);

    }

    /**
     * 生成http响应消息体签名示例Demo
     *
     * @param key  用户在isv console分配的accessKey，请登录后查看
     * @param body http响应的报文
     * @return 加密结果
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IllegalStateException
     * @throws UnsupportedEncodingException
     */

    public static String generateResponseBodySignature(String key, String body)

            throws InvalidKeyException, NoSuchAlgorithmException,

            IllegalStateException, UnsupportedEncodingException {

        return base_64(hmacSHA256(key, body));

    }

    /**
     * hamcSHA256加密算法
     *
     * @param macKey  秘钥key
     * @param macData 加密内容-响应消息体
     * @return 加密密文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalStateException
     * @throws UnsupportedEncodingException
     */

    public static byte[] hmacSHA256(String macKey, String macData)

            throws NoSuchAlgorithmException, InvalidKeyException,

            IllegalStateException, UnsupportedEncodingException {

        SecretKeySpec secret =

                new SecretKeySpec(macKey.getBytes(), "HmacSHA256");

        Mac mac = Mac.getInstance("HmacSHA256");

        mac.init(secret);

        byte[] doFinal = mac.doFinal(macData.getBytes("UTF-8"));

        return doFinal;

    }

    /**
     * 字节数组转字符串
     *
     * @param bytes 字节数组
     * @return 字符串
     */

    public static String base_64(byte[] bytes) {

        return new String(Base64.encodeBase64(bytes));

    }

}
