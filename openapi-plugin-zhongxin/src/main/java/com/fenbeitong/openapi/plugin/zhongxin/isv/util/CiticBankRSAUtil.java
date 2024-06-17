package com.fenbeitong.openapi.plugin.zhongxin.isv.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * <p>Title:  CiticBankRSAUtil</p>
 * <p>Description: 中银银行RSA加解密</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/17 上午11:36
 **/
public class CiticBankRSAUtil {

    /**
     * 针对要签名验签的内容做ASCII排序
     *
     * @param jsonObject
     * @return
     * @throws UnsupportedEncodingException
     */
    public static StringBuffer sortJSONObject(JSONObject jsonObject) {

        if (jsonObject == null) {
            return null;
        }

        StringBuffer plain = new StringBuffer();
        List<Map.Entry<String, Object>> infolds = new ArrayList<Map.Entry<String, Object>>(jsonObject.entrySet());

        //ASCII 排序（字典序）
        Collections.sort(infolds, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                return (o1.getKey().compareTo(o2.getKey()));
            }
        });
        plain.append("{");
        for (Map.Entry<String, Object> item : infolds) {
            String key = item.getKey();
            if ("commonDataList".equalsIgnoreCase(key)) {
                continue;
            }
            plain.append("\"" + key + "\"");
            plain.append(":");
            Object val = item.getValue();
            if (val instanceof JSONObject) {
                StringBuffer str = sortJSONObject((JSONObject) val);
                plain.append(str);
            } else if (val instanceof String) {
                plain.append("\"" + val + "\"");
            } else if (val instanceof JSONArray) {
                plain.append("[");
                net.sf.json.JSONArray arr = (JSONArray) val;
                for (int i = 0; i < arr.size(); i++) {
                    StringBuffer tempstr = new StringBuffer();
                    Object obj = arr.get(i);
                    if (obj instanceof JSONObject) {
                        tempstr = sortJSONObject((JSONObject) obj);
                    }
                    plain.append(tempstr);
                }
                plain.append("]");
            }

            plain.append(",");
        }
        plain.delete(plain.length() - 1, plain.length());
        if (plain.length() > 0) {
            plain.append("}");
        }
        return plain;
    }

    /**
     * RSA签名
     *
     * @param businessJsonStr
     * @param privateKeyStr
     * @return
     */
    public static String sign(String businessJsonStr, String privateKeyStr) throws Exception {
        //获取私钥实体
        PrivateKey privatekey = getPrivateKey(privateKeyStr);
        //使用java原生方法签名明文
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initSign(privatekey);
        signature.update(businessJsonStr.getBytes("UTF-8"));
        byte[] bytarr = signature.sign();
        //得到签名字符串并打印
        String signData =new String(CiticBase64Util.encode(bytarr),"UTF-8");
        signData = signData.replaceAll("\r|\n", "");
        return signData;
    }

    /**
     * RSA验签
     *
     * @param businessJsonStr
     * @param publicKey
     * @param signData
     * @return
     */
    public static boolean verify(String businessJsonStr, String publicKey, String signData) {
        try {

            PublicKey publickey = getPublicKey(publicKey);
            //使用java原生方法验证签名
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(publickey);
            signature.update(businessJsonStr.getBytes("UTF-8"));
            boolean verifySign=signature.verify(CiticBase64Util.decode(signData.getBytes("UTF-8")));
            return verifySign;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * RSA加密
     *
     * @param key
     * @param data
     * @return
     */
    public static byte[] encrypt(RSAKey key, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(1, (Key) key);
        int step = key.getModulus().bitLength() / 8;
        int n = data.length / step;
        if (n > 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int i = 0; i < n; i++) {
                baos.write(cipher.doFinal(data, i * step, step));
            }
            if ((n = data.length % step) != 0) {
                baos.write(cipher.doFinal(data, data.length - n, n));
            }
            return baos.toByteArray();
        }
        return cipher.doFinal(data);

    }

    /**
     * RSA解密
     *
     * @param key
     * @param raw
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(RSAKey key, byte[] raw) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(2, (Key) key);
        int step = key.getModulus().bitLength() / 8;
        int n = raw.length / step;
        if (n > 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int i = 0; i < n; i++) {
                baos.write(cipher.doFinal(raw, i * step, step));
            }
            return baos.toByteArray();
        }
        return cipher.doFinal(raw);
    }

    /**
     * 获取RSA私钥
     *
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {

        byte[] privatekeyBytes=CiticBase64Util.decode(privateKeyStr.getBytes("UTF-8"));
        //初始化RSA密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        //读取成私钥实体
        PKCS8EncodedKeySpec privatekeySpec = new PKCS8EncodedKeySpec(privatekeyBytes);
        PrivateKey privatekey = keyFactory.generatePrivate(privatekeySpec);

        return privatekey;

    }

    /**
     * 获取RSA公钥
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String publicKeyStr) throws Exception {

        //转化成二进制公钥数据
        byte[] publickeyBytes=CiticBase64Util.decode(publicKeyStr.getBytes("UTF-8"));
        //初始化RSA密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        //读取成公钥实体
        X509EncodedKeySpec publickeySpec = new X509EncodedKeySpec(publickeyBytes);
        PublicKey publickey = keyFactory.generatePublic(publickeySpec);
        return publickey;

    }

}
