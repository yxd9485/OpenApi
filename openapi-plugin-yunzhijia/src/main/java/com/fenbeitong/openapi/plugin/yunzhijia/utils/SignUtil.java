package com.fenbeitong.openapi.plugin.yunzhijia.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;

public class SignUtil {
    public static String sign(String... data) {
        Arrays.sort(data);
        return DigestUtils.sha1Hex(join(data));
    }

    public static String join(Object... param) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < param.length; i++) {
            sb.append(param[i]);
        }
        return sb.toString();
    }
}
