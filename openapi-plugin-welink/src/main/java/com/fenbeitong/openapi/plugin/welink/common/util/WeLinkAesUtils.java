package com.fenbeitong.openapi.plugin.welink.common.util;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvCallbackReqDTO;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvCallbackCompanyAuthTrialDTO;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密和解密的示例代码
 */
public class WeLinkAesUtils{

    private static final String ALGORITHM = "AES";
    private static final String defaultCharset = "UTF-8";
    private static final String KEY_GCM_AES = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 128;

    /**
     * 生成length字节的偏移量IV
     * createIV的功能<br>
     *
     * @param length
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String createIV(int length) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return encodeToBase64(salt);
    }

    /**
     * 解密——使用自定义的加密key
     *
     * @param data
     * @param key
     * @param ivStr
     * @return
     * @throws Exception
     */
    public static String decryptByGcm(String data, String key, String ivStr) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_GCM_AES);

        byte[] iv = decodeFromBase64(ivStr);
        SecretKeySpec keySpec = getSecretKeySpec(key);
        cipher.init(2, keySpec, new GCMParameterSpec(AES_KEY_SIZE, iv));
        byte[] content = decodeFromBase64(data);
        byte[] result = cipher.doFinal(content);
        return new String(result);
    }

    /**
     * 加密——使用自定义的加密key
     *
     * @param data
     * @param key
     * @param ivStr
     * @return
     * @throws Exception
     */
    public static String encryptByGcm(String data, String key, String ivStr) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_GCM_AES);

        byte[] iv = decodeFromBase64(ivStr);
        SecretKeySpec keySpec = getSecretKeySpec(key);
        cipher.init(1, keySpec, new GCMParameterSpec(AES_KEY_SIZE, iv));
        byte[] content = data.getBytes(defaultCharset);
        byte[] result = cipher.doFinal(content);
        return encodeToBase64(result);
    }

    private static byte[] decodeFromBase64(String data) {
        return Base64.getDecoder().decode(data);
    }

    private static String encodeToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * 公共使用，获取SecretKeySpec
     *
     * @param key
     * @return
     */
    private static SecretKeySpec getSecretKeySpec(String key) {
        SecretKeySpec keySpec = null;
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom secureRandom= SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes(defaultCharset));
            kgen.init(AES_KEY_SIZE, secureRandom);
            //3.产生原始对称密钥
            SecretKey secretKey = kgen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] enCodeFormat = secretKey.getEncoded();
            //5.根据字节数组生成AES密钥
            keySpec = new SecretKeySpec(enCodeFormat, ALGORITHM);
        } catch (Exception e) {
            System.out.println("To do get SecretKeySpec exception!");
        }
        return keySpec;
    }

    public static void main(String[] args) throws Exception {
//        String secret = "8cf860c0-30b7-4357-a104-fa627c59085d";//app的secret，加解密使用，开放平台获得
//        String reqJson="{\"eventType\":\"corpAuth\",\"tenantId\":\"tenant\",\"timestamp\":\"1565167553\"}";
//        String respJson="{\"timestamp\":\"1565167553\",\"msg\":\"success\"}";
//        System.out.println("[请求Json]："+reqJson);
//        String ivStr = createIV(16);
//        String reqStr=encryptByGcm(reqJson,secret, ivStr);
//        System.out.println("[请求密文]：" + ivStr + reqStr);
//        String reqJsonAfterDecrypt=decryptByGcm(reqStr, secret, ivStr);
//        System.out.println("[请求密文解析后]："+reqJsonAfterDecrypt);
//        String ivStr2 = createIV(16);
//        System.out.println("[响应Json]："+respJson);
//        String respStr=encryptByGcm(respJson,secret, ivStr2);
//        System.out.println("[响应密文]：" + ivStr2 + respStr);
//        String respJsonAfterDecrypt=decryptByGcm(respStr,secret, ivStr2);
//        System.out.println("[响应密文解析后]："+respJsonAfterDecrypt);
//        String secret = "41ee365f-c8ce-4678-8c38-473ccc3a21a6";
//        String reqStr = "NYENN0sTqdW53ii8YKYLDihPluynbAqZAFUy0vNwfx9i2VyyrqeWq4iEHrx43En6tZhSBRzjMkCS+2ziXGQGy5OL2y5zeiu5hxqOcvYSnxaf4A==";
//        String ivStr = "k7fdXkWmqMQFvL4WaiRZZw==";
//        String reqJsonAfterDecrypt=decryptByGcm(reqStr, secret, ivStr);
//        System.out.println(reqJsonAfterDecrypt);
//
        String appSecret = "41ee365f-c8ce-4678-8c38-473ccc3a21a6";
        String requestBody = "QFkHNVuv73XrRAHYV/+4bA==srWil9onkZi6cMJoWC8UeSSHKReVTTh5iIVHKdBHeHt3DUbzXdoB7nlzBNph5oC5SfM3Ebgj8f79Zq2vkrcQB/QC/4dnbUpDukYVt/CmuNKppHSxOCiSuYEgoEwWUWqz1KEuAym69DQD1hnjKH89OrFYOjWW1G3e475/u8CulDGITerNrqnKfQpBbZLayEgtJ9kBZiDb6vkxllnOKytsdPcA1Hw=";
        String ivStr = requestBody.substring(0, 24);
        String reqStr = requestBody.substring(24);
        String reqJson = WeLinkAesUtils.decryptByGcm(reqStr, appSecret, ivStr);
        System.out.println(reqJson);


    }
}

