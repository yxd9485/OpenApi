package com.fenbeitong.openapi.plugin.zhongxin.isv.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

/**
 * <p>Title: CiticBankAESUtil </p>
 * <p>Description: 中信银行AES加解密 </p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 上午11:31
 **/
public class CiticBankAESUtil {

    /**
     * 获取一个随机AES密钥
     *
     * @return
     */
    public static String getRandomAESKey() {
        int aeskeyLength = 16;
        String aeskeyString = "";
        char[] e = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
        int index = 0;
        Random r = new Random();
        for (int i = 0; i < aeskeyLength; i++) {
            index = r.nextInt(64);
            aeskeyString += e[index];
        }
        return aeskeyString;
    }

    /**
     * AES加密
     *
     * @param businessJsonStr
     * @param aeskey
     * @return
     */
    public static byte[] encrypt(String businessJsonStr, String aeskey) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, aeskey);// 创建密码器
        byte[] result = cipher.doFinal(businessJsonStr.getBytes("UTF-8"));// 加密
        return result;
    }

    /**
     * AES解密
     *
     * @param content
     * @param aeskey
     * @return
     */
    public static byte[] decrypt(byte[] content, String aeskey) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, aeskey);// 创建密码器
        byte[] result = cipher.doFinal(content);
        return result; // 明文
    }

    /**
     * 创建AES密码器
     *
     * @param mode
     * @param key
     * @return
     */
    private static Cipher getCipher(int mode, String key) {
        // mode =Cipher.DECRYPT_MODE or Cipher.ENCRYPT_MODE
        Cipher mCipher;
        byte[] keyPtr = new byte[16];
        IvParameterSpec ivParam = new IvParameterSpec(keyPtr);
        byte[] passPtr = key.getBytes();
        try {
            mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            for (int i = 0; i < 16; i++) {
                if (i < passPtr.length)
                    keyPtr[i] = passPtr[i];
                else
                    keyPtr[i] = 0;
            }
            SecretKeySpec keySpec = new SecretKeySpec(keyPtr, "AES");
            mCipher.init(mode, keySpec, ivParam);
            return mCipher;
        } catch (Exception e) {
        }
        return null;
    }
}
