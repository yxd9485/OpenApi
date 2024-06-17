package com.fenbeitong.openapi.plugin.func.deprecated.valid.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.ApiConstants;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.GlobalResponseCodeOld;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.OpenJavaCommonService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaCompanyEmployeeService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaDepartmentService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaProjectService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/4 11:20
 * @since 1.0
 */
@Service
@ServiceAspect
@Slf4j
public class OpenJavaCommonServiceImpl implements OpenJavaCommonService {

    @Value("${fe.sign.key}")
    private String signKey;


    @Autowired
    private OpenJavaCompanyEmployeeService openJavaCompanyEmployeeService;

    @Autowired
    private OpenJavaDepartmentService openJavaDepartmentService;

    @Autowired
    private OpenJavaProjectService openJavaProjectService;


    /**
     * 根据分贝通第三方用户id 获取手机号信息
     *
     * @param param
     * @return
     */
    @Override
    public String getToken(Map<String, Object> param) {
        Map<String, Object> data = Maps.newHashMap();
        String token = null;
        Object queryEmployee = openJavaCompanyEmployeeService.getQueryEmployee(param, null);
        data = JsonUtils.toObj(JsonUtils.toJson(queryEmployee), new TypeReference<Map<String, Object>>() {
        });
        if (data.containsKey("token")) {
            token = StringUtils.obj2str(data.get("token"));
        }
        return token;
    }


    @Override
    public JSONObject isJson(String data) {
        if (StringUtils.isBlank(data)) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getMessage());
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(data);
        } catch (Exception ex) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getMessage());

        }
        return jsonObject;
    }

    @Override
    public Map<String, Object> getCostAttributionInfo(Map<String, Object> paramMap) {
        Integer costType = (Integer) paramMap.get("type");
        String companyId = (String) paramMap.get("company_id");
        String attributionId = (String) paramMap.get("cost_attribution_id");
        String token = (String) paramMap.get("token");
        Map<String, Object> resultMap = Maps.newHashMap();
        if (ObjectUtils.isEmpty(costType)) {
            return resultMap;
        }
        if (1 == costType) {
            HashMap<String, Object> paramHashMap = Maps.newHashMap();
            paramHashMap.put("companyId", companyId);
            paramHashMap.put("operatorId", "OpenApi");
            paramHashMap.put("orgId", attributionId);
            paramHashMap.put("type", 1);
            Object costAttributionInfo = null;
            try {
                costAttributionInfo = openJavaDepartmentService.getOrgUnitDetail(JsonUtils.toJson(paramHashMap), token);
                JSONObject dataInfo = (JSONObject) JSONObject.parse(JsonUtils.toJson(costAttributionInfo));
                String thirdCostAttributionId = dataInfo.getString("third_org_id") == null ? "" : dataInfo.getString("third_org_id");
                resultMap.put("third_cost_attribution_id", thirdCostAttributionId);
            } catch (Exception e) {
                log.info("调用接口异常", e);
                resultMap.put("third_cost_attribution_id", "");
            }
        } else if (2 == costType) {
            Map<String, Object> requestMap = Maps.newHashMap();
            Map<String, Object> dataParamMap = Maps.newHashMap();
            dataParamMap.put("companyId", companyId);
            dataParamMap.put("id", attributionId);
            requestMap.put("token", token);
            requestMap.put("data", JsonUtils.toJson(dataParamMap));
            try {
                Object projectDetail = openJavaProjectService.getProjectDetail(requestMap, token);
                Map<String, Object> dataMap = JsonUtils.toObj(JsonUtils.toJson(projectDetail), new TypeReference<Map<String, Object>>() {
                });
                String thirdCostId = dataMap.get("thirdCostId") == null ? "" : (String) dataMap.get("thirdCostId");
                resultMap.put("third_cost_attribution_id", thirdCostId);
            } catch (Exception e) {
                log.info("火车详情调用失败原因");
                resultMap.put("third_cost_attribution_id", "");
            }
        }
        return resultMap;
    }

    @Override
    public String getEmployeeThirdInfoByFbId(Map<String, String> map) {
        String companyId = map.get("company_id");
        String oderOwnerId = map.get("order_owner_id");
        JSONObject employeeInfoDataJson = new JSONObject();
        employeeInfoDataJson.put("companyId", companyId);
        employeeInfoDataJson.put("employeeId", oderOwnerId);
        employeeInfoDataJson.put("type", 1);
        employeeInfoDataJson.put("userType", 1);
        String thirdEmployeeId = "";
        try {
            Object employeeThird = openJavaProjectService.getEmployeeThird(employeeInfoDataJson, null);
            Map<String, Object> employeeDetailMap = JsonUtils.toObj(StringUtils.obj2str(employeeThird), new TypeReference<Map<String, Object>>() {
            });
            Map employeeInfo = (Map) employeeDetailMap.get("employee");
            thirdEmployeeId = (String) employeeInfo.get("thirdEmployeeId");
        } catch (Exception e) {
            log.info("查询第三方用户详情异常 异常信息->>>", e);
            HashMap<String, String> dimissionDetailMap = Maps.newHashMap();
            dimissionDetailMap.put("company_id", companyId);
            dimissionDetailMap.put("employee_id", oderOwnerId);
            Object resignThirdEmployee = openJavaCompanyEmployeeService.getResignThirdEmployee(dimissionDetailMap, null);
            Map<String, Object> dataMap = JsonUtils.toObj(JsonUtils.toJson(resignThirdEmployee), new TypeReference<Map<String, Object>>() {
            });
            List employeeDimissionList = (List) dataMap.get("employee_dimission_list");
            //说明没有离职数据，人员不在公司内，没有绑定组织架构
            if (employeeDimissionList.isEmpty()) {
                thirdEmployeeId = "";
            } else {
                Map dimissionMap = (Map) employeeDimissionList.get(0);
                thirdEmployeeId = (String) dimissionMap.get("third_employee_id");
            }
        }
        return thirdEmployeeId;
    }

    @Override
    public JSONObject getOrgThirdInfoByFbId(Map<String, String> param, JSONObject json) {
        String companyId = param.get("company_id");
        String fbOrgId = param.get("org_id");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("companyId", companyId);
        jsonObject.put("orgId", fbOrgId);
        jsonObject.put("operatorId", "OpenApi");
        jsonObject.put("type", 1);
        try {
            Object orgUnitDetail = openJavaDepartmentService.getOrgUnitDetail(jsonObject, null);
            JSONObject jsonObject1 = isJson(JsonUtils.toJson(orgUnitDetail));
            String comapnyName = jsonObject1.getString("company_name");
            String orgUnitName = jsonObject1.getString("name");
            String thirdOrgId = jsonObject1.getString("third_org_id");
            JSONArray deptJsonArray = jsonObject1.getJSONArray("parent_dept_list");
            JSONObject deptInfoJsonObject = (JSONObject) deptJsonArray.get(0);
            String thirdParentDepId = deptInfoJsonObject.getString("third_org_id");
            String thirdParentOrgName = deptInfoJsonObject.getString("name");
            json.put("company_name", comapnyName);
            json.put("third_org_name", orgUnitName);
            if (org.apache.commons.lang3.StringUtils.isBlank(thirdOrgId)) {
                json.put("third_org_id", "");
            } else {
                json.put("third_org_id", thirdOrgId);
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(thirdParentDepId)) {
                json.put("third_parent_org_id", "");
            } else {
                json.put("third_parent_org_id", thirdParentDepId);
            }
            json.put("third_parent_org_name", thirdParentOrgName);
        } catch (Exception e) {
            json.put("company_name", "");
            json.put("third_org_name", "");
            json.put("third_org_id", "");
            json.put("third_parent_org_id", "");
            json.put("third_parent_org_name", "");
            log.info("getOrgThirdInfoByFbId 信息异常", e);
        }
        return json;
    }

    @Override
    public String genSign(long timestamp, String data, String signKey) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        param.put("timestamp", timestamp);
        param.put("data", data);
        return genSign(param, signKey);
    }

    @Override
    public String getToken(Map<String, Object> param, Boolean bool) {
        if (Boolean.TRUE.equals(bool)) {
            long timestamp = System.currentTimeMillis();
            String appId = (String) param.get("company_id");
            String sign = this.genSign(timestamp, appId, signKey);
            param.put("timestamp", timestamp);
            param.put("sign", sign);
            return getToken(param);
        }
        return getToken(param);

    }

    @Override
    public String getRequestBody(HttpServletRequest request) {
        String body = null;
        try {
            InputStream inputStream = request.getInputStream();
            body = new String(IOUtils.toByteArray(inputStream), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.info("read http request failed.", e);
        }
        return body;

    }

    @Override
    public String convertUnderScoreToCamel(String jsonString, List<String> underScoreKeys) {
        Object obj = JSONObject.parse(jsonString);
        convert(obj, true, underScoreKeys);
        return obj.toString();
    }

    @Override
    public String convertUnderScoreToCamel(String jsonString) {
        Object obj = JSONObject.parse(jsonString);
        convert(obj, true, null);
        return obj.toString();
    }

    @Override
    public Map<String, Object> covertParams(HttpServletRequest httpRequest, ApiRequest request) {
        Map<String, Object> requestMap = new HashMap<String, Object>(2);
        Map<String, Object> param = new HashMap<String, Object>(3);
        param.put(ApiConstants.USER_ID, request.getEmployeeId());
        param.put(ApiConstants.COMPANY_ID, httpRequest.getAttribute("companyId"));
        if (ApiConstants.APP_TYPE_FBT.equals(request.getEmployeeType().toString())) {
            param.put(ApiConstants.APP_TYPE, ApiConstants.APP_TYPE_FBT);
        } else {
            param.put(ApiConstants.APP_TYPE, ApiConstants.APP_TYPE_THIRD);
        }
        String token = this.getToken(param, true);
        requestMap.put(ApiConstants.FBT_TOKEN, StringUtils.isNotBlank(token) ? token : "");
        if (StringUtils.isNotBlank(request.getData())) {
            String jsonParam = this.convertUnderScoreToCamel(request.getData());
            //companyId重新赋值
            JSONObject companyIdJsonObject = this.isJson(jsonParam);
            companyIdJsonObject.put("companyId",httpRequest.getAttribute("companyId"));
            jsonParam = JsonUtils.toJson(companyIdJsonObject);
            requestMap.put(ApiConstants.JSON_PARAM, jsonParam);
        } else {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        return requestMap;
    }

    @Override
    public Map<String, String> getParameterMap(HttpServletRequest httpRequest) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> paramNames = httpRequest.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = httpRequest.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    map.put(paramName, paramValue);
                }
            }
        }
        return map;
    }

    /**
     * 根据参数和 signKey 生成签名
     */
    private static String genSign(Map<String, Object> param, String signKey) {
        /** 1、生成签名的时候,将颁发的 sign_key 加入到传递的参数中，参与加密 */
        param.put("sign_key", signKey);
        /** 2、传递的参数(包含 sign_key )，已 & 形式连接 k=v，生成小写的 md5 串 */
        String joinParam = joinParam(param);
        /** 3、生成小写的 md5 串 */
        log.info("传递的参数进行加密，joinParam={}" + joinParam);

        byte[] bytes = new byte[0];
        try {
            bytes = joinParam.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return DigestUtils.md5Hex(bytes);
    }

    /**
     * 使用&、=拼接变量名和变量值
     */
    private static String joinParam(Map<String, Object> param) {
        StringJoiner stringJoiner = new StringJoiner("&");
        param.forEach((k, v) -> stringJoiner.add(k + "=" + v));
        return stringJoiner.toString();
    }


    /**
     * @param jsonObject :
     * @return void
     * @author Created by ivan on 下午5:31 18-11-26.
     * <p>递归，将JSONObject的下划线key转为驼峰key
     */
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
