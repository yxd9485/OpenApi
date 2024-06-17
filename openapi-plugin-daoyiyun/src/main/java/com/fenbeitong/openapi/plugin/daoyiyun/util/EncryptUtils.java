package com.fenbeitong.openapi.plugin.daoyiyun.util;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


/**
 * 道一云加密工具
 *
 * @author lizhen
 */
@Slf4j
public final class EncryptUtils {
    /**
     * 加密算法
     */
    private static final String ENCRY_ALGORITHM = "AES";

    /**
     * 加密算法/加密模式/填充类型
     * 采用AES加密，ECB加密模式，PKCS7Padding填充
     */
    private static final String CIPHER_MODE = "AES/ECB/PKCS7Padding";

    /**
     * 设置加密字符集
     * 采用 UTF-8 字符集
     */
    private static final String CHARACTER = "UTF-8";

    private EncryptUtils() {
    }

    /**
     * AES加密
     *
     * @param content    加密内容
     * @param encryptKey 加密密钥
     * @return
     */
    private static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        SecretKey key = getKey(encryptKey);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content.getBytes(CHARACTER));
    }

    /**
     * 生成密钥
     *
     * @param strKey
     * @return
     */
    public static SecretKey getKey(String strKey) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(ENCRY_ALGORITHM);
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(strKey.getBytes());
            generator.init(128, secureRandom);
            return generator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(" 初始化密钥出现异常 ");
        }
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    private static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        SecretKey key = getKey(decryptKey);
        // 添加加密模式
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(encryptBytes), CHARACTER);
    }


    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    private static byte[] base64Decode(String base64Code) throws Exception {
        return Base64.decodeBase64(base64Code.getBytes(CHARACTER));
    }

    /**
     * AES加密为base 64 code
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String aesEncrypt(String content, String encryptKey) {
        try {
            return Base64.encodeBase64String(aesEncryptToBytes(content, encryptKey));
        } catch (Exception e) {
            log.error("aes加密失败", e);
            throw new OpenApiArgumentException("aes加密失败");
        }
    }

    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr, String decryptKey){
        try {
            return StringUtils.isBlank(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
        } catch (Exception e) {
            log.error("aes解密失败", e);
            throw new OpenApiArgumentException("aes解密失败");
        }
    }
}
