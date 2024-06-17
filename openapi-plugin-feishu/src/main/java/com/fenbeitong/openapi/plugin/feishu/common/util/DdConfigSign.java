package com.fenbeitong.openapi.plugin.feishu.common.util;

import lombok.extern.slf4j.Slf4j;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.Random;

/**
 * Created by xiaohai on 2021/11/17.
 */
@Slf4j
public class DdConfigSign {

    /**
     * 计算 h5sdk.config 的签名参数
     *
     * @param jsticket  之前获取的 jsticket
     * @param nonceStr  随机字符串
     * @param timeStamp 当前时间戳
     * @param url       调用 h5sdk.config 的当前页面 URL
     * @return
     */
    public static String sign(String jsticket, String nonceStr, long timeStamp, String url) throws Exception {
        String plain = "jsapi_ticket=" + jsticket + "&noncestr=" + nonceStr + "&timestamp=" + String.valueOf(timeStamp)
                + "&url=" + url;
        try {
            log.info("DdConfigSign, plain={}", plain);
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.reset();
            sha1.update(plain.getBytes("UTF-8"));
            return byteToHex(sha1.digest());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // 字节数组转化成十六进制字符串
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }


    public static String getRandomStr(int count) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
