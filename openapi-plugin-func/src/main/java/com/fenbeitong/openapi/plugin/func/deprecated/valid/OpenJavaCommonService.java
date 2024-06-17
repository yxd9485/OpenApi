package com.fenbeitong.openapi.plugin.func.deprecated.valid;

import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * module: 迁移open-java二期<br/>
 * <p>
 * description: 迁移Java 工具类 保持原有项目语义<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/4 11:13
 * @since 1.0
 */
public interface OpenJavaCommonService {

    /**
     * open-java 保持原有项目语义 获取token
     * @param param
     * @return
     */
    String getToken(Map<String, Object> param);

    /**
     * open-java 保持原有项目语义 获取token 升级版
     * @param param
     * @param bool
     * @return
     */
    String getToken(Map<String, Object> param,Boolean bool);

    /**
     * open-java 保持原有项目语义  json 转换保持原有 open-java 方式
     * @param data
     * @return
     */
    JSONObject isJson(String data);


    /**
     * open-java 保持原有项目语义 获取费用归属信息
     * @param paramMap
     * @return
     */
    Map<String, Object> getCostAttributionInfo(Map<String, Object> paramMap);

    /**
     * open-java 保持原有项目语义 根据分贝人员ID查询第三方ID
     * @param map
     * @return
     */
    String getEmployeeThirdInfoByFbId(Map<String, String> map);

    /**
     * open-java 保持原有项目语义 根据分贝部门ID查询第三方部门信息
     * @param param
     * @param json
     * @return
     */
    JSONObject getOrgThirdInfoByFbId(Map<String, String> param, JSONObject json);

    /**
     * open-java 获取sign
     * @param timestamp
     * @param data
     * @param signKey
     * @return
     */
    String genSign(long timestamp, String data, String signKey);


    /**
     * open-Java 获取body 数据
     * @param request
     * @return
     */
    String getRequestBody(HttpServletRequest request);

    /**
     * <p>下划线JSON转驼峰JSON,指定keys
     * @param jsonString
     * @param underScoreKeys
     * @return
     */
    String convertUnderScoreToCamel(String jsonString, List<String> underScoreKeys);

    /**
     * 下划线JSON转驼峰JSON
     * @param jsonString
     * @return
     */
    String convertUnderScoreToCamel(String jsonString);

    /**
     * 组装公共请求参数
     * @param httpRequest
     * @param request
     * @return
     */
    Map<String, Object> covertParams(HttpServletRequest httpRequest, ApiRequest request);


    /**
     * HttpServletRequest 获取请求参数转map
     * @param httpRequest
     * @return
     */
    Map<String, String> getParameterMap(HttpServletRequest httpRequest);


}
