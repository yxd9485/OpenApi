package com.fenbeitong.openapi.plugin.moka.util;


import lombok.extern.slf4j.Slf4j;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * 签名工具类
 *
 * @author aran
 */
@Slf4j
public class SignHelper {

    //private static final String pubkey;
    private static final String prikey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJRJAdtzgTgB4axJf4wvRtoK03F7c8AjGBXzf+OP0ZkeIg6RHyuT1bYSwDxTdaYgMQqb2x6ezI0HWYFrZd8KqODPgTIK+HCuHM07GTSjcYppIx+YsulhFGDfXTXfCXgEmbJvCLdf5mWFmqqiWw82qGLJnjS29rtWoX8PDbtEIWOdAgMBAAECgYBjAMuIpXmk1zzBNuE9y2LWuVuq7O2/Xq76GPVODfu263N5nlLdWXracXJ/1Ik8PUoA90Y5D3Uqw7lyuy3s2MUfLYsiTgGz4H5KlGu9z2CBaZkTZdwsYGBX4NDA9Xl28LDUhp/kQDvAfwV6B0YsSuiAXR6PcauiP6mYf3zC6jZ90QJBAN3uAaOSxY1a6+5HgPw49R+c7EcJWqZIQwyyuomEzTm8eiitPGQGbXYmOtrVZyaJzGo2NiI/7fy2muBXJ39xdRcCQQCrDLgbvJ8EGZkcYsLGwQndvbqRe8TnPAaZ0UvQXgD4pApWwpM0alIA3RAnAM2SI5+Jd4HMIvgQny7segtWdAVrAkB4+tvt3X7P1fazSEtqM2p8t0CeYN0HtHDIpU3G1ZxCkxpMvlMje9WECMcgsCCxA4FpjO/pr8v+bVT0Ys5nTOQ1AkBFGq1i31BrCqB1/FxuidyfjiBK2HMTbIfKKulzNBc2XKekp5VrLq0UljsJVuS2btfsZLC2tUX9CSKe/SuJcPQjAkEAtG5+G7Y67mosYUriIRBNdxtGgKP1GuZeYcxeNk7AAgaa8kx0ctBVuxmoIhR8cuQIVGoizYVXlOXo2n+eRF1e9A==";
/*

    static {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application-dev.yml"));
        Properties properties = yaml.getObject();
        //pubkey = properties.getProperty("park.pubkey");
        prikey = properties.getProperty("park.prikey");

        // System.out.println("pubkey=" +pubkey);
        System.out.println("prikey=" + prikey);
    }
*/


    /**
     * 获取签名的util
     *
     * @param map 请求参数
     * @return
     */
    public static String getSignStr(TreeMap<String, Object> map) {

        if (map.size() == 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer("");

        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            sb.append("&" + key + "=" + map.get(key));
        }
        sb.deleteCharAt(0);
        return sign(sb.toString());
    }

    public static String sign(String content) {
        //MD5加密后，字符串所有字符转换为大写
        return RsaSign(content);
    }

    /**
     * 加密
     */
    public static String RsaSign(String str4Sign) {


        PrivateKey rsaPriKey = getPrivateKey(prikey);
        Signature signature;
        try {
            signature = Signature.getInstance("MD5withRSA");
            signature.initSign(rsaPriKey);
            signature.update(str4Sign.getBytes());
            byte[] result = signature.sign();
            String getSign = Base64.getEncoder().encodeToString(result);
            log.info("签名后的内容为：" + getSign);
            return getSign;
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;


    }

    /**
     * 解密
     */
  /*  public static boolean RsaSignCheck(String signStr, byte[] result) {

        PublicKey pubKey = getPublicKey(pubkey);
        Signature signature;
        try {
            signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(pubKey);
            signature.update(signStr.getBytes());
            boolean signOk = signature.verify(result);
            logger.info("签名验证结果：" + signOk);
            return signOk;
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }*/
    private static Map<String, String> RsaKeyGenerator() {
        Map<String, String> keyPairMap = new HashMap<>();
        try {
            //1.初始化秘钥
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
            keyPairMap.put("priKey", encodeKey2StrByBase64(rsaPrivateKey.getEncoded()));
            keyPairMap.put("pubKey", encodeKey2StrByBase64(rsaPublicKey.getEncoded()));
            return keyPairMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String tempStr = "a=1&b=2";
        System.out.println("========" + new String(RsaSign(tempStr)));
        //RsaSignCheck(tempStr, RsaSign(tempStr));
    }

    public static String encodeKey2StrByBase64(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }

    public static PublicKey getPublicKey(String key) {
        byte[] keyBytes;
        keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory;

        try {
            keyFactory = KeyFactory.getInstance("RSA");

            PublicKey pubKey;

            pubKey = keyFactory.generatePublic(keySpec);

            return pubKey;
        } catch (InvalidKeySpecException e) {
            log.error("InvalidKeySpecException[公钥]：" + e.getStackTrace());
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException[公钥]：" + e.getStackTrace());
        }
        return null;

    }

    public static PrivateKey getPrivateKey(String key) {
        byte[] keyBytes;
        keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey priKey;

            priKey = keyFactory.generatePrivate(keySpec);

            return priKey;
        } catch (InvalidKeySpecException e) {
            log.error("InvalidKeySpecException[私钥]：" + e.getStackTrace());
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException[私钥]：" + e.getStackTrace());
        }
        return null;


    }
}



