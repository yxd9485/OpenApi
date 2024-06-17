package com.fenbeitong.openapi.plugin.func.deprecated.valid.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.ValidService;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;


/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/7 11:49
 * @since 1.0
 */
@Service
@ServiceAspect
@Slf4j
public class ValidServiceImpl implements ValidService {


    @Override
    public void checkRequest(Object obj) {
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(obj);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
    }

    @Override
    public Integer checkParameterType(Object obj) {
        if
        (obj instanceof String){
            return 1;
        }
        else if(obj instanceof Integer){
            return  2;
        }
        else if(obj instanceof Double){
            return  3;
        }
        else if(obj instanceof Boolean){
            return  5;
        }
        else  if(obj instanceof  Character){
            return  6;
        }
        else if(obj instanceof  Float){
            return 7;
        }else {
            return -1;
        }
    }

    @Override
    public Integer parameterTypeValid(Object param, Integer tag) {
        if (param instanceof String) {
            tag = 1;
        }
        if (param instanceof Integer) {
            tag = 2;
        }
        if (param instanceof Double) {
            tag = 3;
        }
        if (param instanceof Boolean) {
            tag = 5;
        }
        if (param instanceof Character) {
            tag = 6;
        }
        if (param instanceof Float) {
            tag = 7;
        }
        if(param instanceof JSONArray){
            //数组，集合
            tag = 8;
        }
        if(param instanceof ArrayList){
            tag = 9;
        }

        return tag;
    }

    @Override
    public Map<String, Object> transformMap(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();
        Map<String, Object> returnMap = new HashMap<String, Object>();
        Iterator<Entry<String, String[]>> iter = properties.entrySet().iterator();
        String name = "";
        String value = "";
        while (iter.hasNext()) {
            Entry<String, String[]> entry = iter.next();
            name = entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    @Override
    public Boolean validate(String jsonString, List<String> checkKeys) {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        Object obj = JSONObject.parse(jsonString);
        Map<String,List<Object>> jsonMap = parseJSON2Map(obj,map);
        Set<String> keySet = jsonMap.keySet();
        Set<String> checkKeySet = new HashSet<String>(checkKeys);
        if(keySet.containsAll(checkKeySet)){
            for(String key : checkKeySet){
                List<Object> valueList = jsonMap.get(key);
                for(Object valueObj:valueList){
                    if(valueObj instanceof JSONArray){
                        if(0>=((JSONArray) valueObj).size()){
                            log.info("Income parameter: '"+key+"' is not valid");
                            return false;
                        }
                    }else {
                        String value = valueObj.toString();
                        if (StringUtils.isBlank(value)) {
                            log.info("Income parameter: '"+key+"' is not valid");
                            return false;
                        }
                    }
                }
            }

        }else{
            return false;
        }
        return true;
    }

    /**
     * @author Created by ivan on 下午1:00 18-11-28.
     * <p>递归，将JSON OBJECT转为MAP
     * @param jsonObject :
     * @param map :
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    static Map<String, List<Object>> parseJSON2Map(Object jsonObject,Map<String, List<Object>> map){
        if (jsonObject instanceof JSONArray) {
            JSONArray arr = (JSONArray) jsonObject;
            for (Object obj : arr) {
                parseJSON2Map(obj,map);
            }
        } else if (jsonObject instanceof JSONObject) {
            JSONObject jo = (JSONObject) jsonObject;
            Set<String> keys = jo.keySet();
            String[] array = keys.toArray(new String[keys.size()]);
            for (String key : array) {
                Object value = jo.get(key);
                if(map.containsKey(key)){
                    map.get(key).add(value);
                }else{
                    map.put(key,new ArrayList<Object>(){{add(value);}});
                }
                parseJSON2Map(value,map);
            }
        }
        return map;
    }
}
