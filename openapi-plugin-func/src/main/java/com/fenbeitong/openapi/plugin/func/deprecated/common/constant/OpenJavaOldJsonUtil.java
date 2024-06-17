package com.fenbeitong.openapi.plugin.func.deprecated.common.constant;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;

import java.util.List;
import java.util.Set;

/**
 * module: 迁移open-java 项目<br/>
 * <p>
 * description: 迁移open-java 项目<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/11 13:20
 * @since 2.0
 */
@Deprecated
public class OpenJavaOldJsonUtil {


    /**
     * 下划线转驼峰
     * @param jsonString
     * @return
     */
    public static String convertUnderScoreToCamel(String jsonString) {
        Object obj = JSONObject.parse(jsonString);
        convert(obj, true, null);
        return obj.toString();
    }

    private static void convert(Object jsonObject, boolean camelCase, List<String> convertKeys) {
        if (jsonObject instanceof JSONArray) {
            JSONArray arr = (JSONArray) jsonObject;
            for (Object obj : arr) {
                convert(obj, camelCase, convertKeys);
            }
        } else if (jsonObject instanceof JSONObject) {
            JSONObject jo = (JSONObject) jsonObject;
            Set<String> keys = jo.keySet();
            String[] array = keys.toArray(new String[keys.size()]);
            for (String key : array) {
                Object value = jo.get(key);
                if (null == convertKeys || convertKeys.contains(key)) {
                    if (camelCase && key.contains("_")) {
                        String newkey = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key);
                        jo.remove(key);
                        jo.put(newkey, value);
                    } else if (!(camelCase)) {
                        String newkey = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);
                        jo.remove(key);
                        jo.put(newkey, value);
                    }
                }
                convert(value, camelCase, convertKeys);
            }
        }
    }
}
