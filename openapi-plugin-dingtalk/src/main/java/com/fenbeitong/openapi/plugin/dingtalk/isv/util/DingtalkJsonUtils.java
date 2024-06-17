package com.fenbeitong.openapi.plugin.dingtalk.isv.util;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.internal.parser.json.ObjectJsonParser;

/**
 * json转钉钉dto
 *
 * @author lizhen
 * @date 2020/7/15
 */
public class DingtalkJsonUtils {

    public static <T extends TaobaoResponse> T parseResponse(String json, Class<T> clazz) throws ApiException {
        ObjectJsonParser<T> parser = new ObjectJsonParser(clazz, true);
        T rsp = parser.parse(json, "top");
        rsp.setBody(json);
        return rsp;
    }
}
