package com.fenbeitong.openapi.plugin.customize.hairou.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author :zhiqiang.zhang
 * @title: HaiRouRestApiUtils
 * @projectName openapi-plugin
 * @description:
 * @date 2022/5/18
 */
@Slf4j
public class HaiRouRestApiUtils {

    public static String getMD5Str(String plainText) {
        //定义一个字节数组
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(plainText.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            log.error("没有md5这个算法！", e);
        }
        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 获取当前日期时间。 YYYY-MM-DD HH:MM:SS
     *
     * @return 当前日期时间
     */
    private static String getCurDateTime() {
        Date newdate = new Date();
        long datetime = newdate.getTime();
        Timestamp timestamp = new Timestamp(datetime);
        return (timestamp.toString()).substring(0, 19);
    }

    /**
     * 获取时间戳   格式如：19990101235959
     *
     * @return 接口需要的时间戳字段
     */
    public static String getTimestamp() {
        return getCurDateTime().replace("-", "").replace(":", "").replace(" ", "");
    }

}
