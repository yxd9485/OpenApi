package com.fenbeitong.openapi.plugin.wechat.isv.util;

import java.util.Random;

/**
 * @author zhaokechun
 * @date 2018/11/26 20:26
 */
public class PhoneGenUtils {

    public static String genPhoneNum() {
        StringBuilder sb = new StringBuilder("320");
        Random random = new Random();
        int customSize = 8;
        for (int i = 0; i < customSize; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
