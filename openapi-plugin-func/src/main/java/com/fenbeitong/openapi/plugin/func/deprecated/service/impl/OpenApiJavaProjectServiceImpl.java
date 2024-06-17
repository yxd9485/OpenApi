package com.fenbeitong.openapi.plugin.func.deprecated.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.ApiConstants;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.GlobalResponseCodeOld;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenApiJavaProjectService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.OpenJavaCommonService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.ValidService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaProjectService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * module: 迁移 open-java 项目<br/>
 * <p>
 * description: 项目模块<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/16 17:48
 */
@Service
@ServiceAspect
public class OpenApiJavaProjectServiceImpl implements OpenApiJavaProjectService {

    @Autowired
    private OpenJavaCommonService openJavaCommonService;

    @Autowired
    private ValidService validService;

    @Autowired
    private OpenJavaProjectService openJavaProjectService;

    @Override
    public Object updateState(HttpServletRequest httpRequest, ApiRequest request) {
        Map<String, Object> requestMap = openJavaCommonService.covertParams(httpRequest, request);
        String[] checkMainKeys = {"state"};
        String jsonParam = requestMap.get(ApiConstants.JSON_PARAM).toString();
        JSONObject jsonData = JSONObject.parseObject(jsonParam);
        if (null != jsonData.get("id") && StringUtils.isBlank(jsonData.get("id").toString()) && null != jsonData.get("thirdCostId") && StringUtils.isBlank(jsonData.get("thirdCostId").toString())) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if(!validService.validate(jsonParam,checkKeys)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Map<String, Object> map = JsonUtils.toObj(jsonParam, new TypeReference<Map<String, Object>>() {
        });
        return openJavaProjectService.updateThirdProjectState(map,(String) requestMap.get(ApiConstants.FBT_TOKEN));
    }

    @Override
    public Object updateStateBatch(HttpServletRequest httpRequest, ApiRequest request) {
        Map<String, Object> requestMap = openJavaCommonService.covertParams(httpRequest, request);
        String[] checkMainKeys = {"type","state","idList"};
        String jsonParam = requestMap.get(ApiConstants.JSON_PARAM).toString();
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if(!validService.validate(jsonParam,checkKeys)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Map<String, Object> map = JsonUtils.toObj(jsonParam, new TypeReference<Map<String, Object>>() {
        });
        return openJavaProjectService.updateThirdProjectStateByBatch(map,(String) requestMap.get(ApiConstants.FBT_TOKEN));
    }

    @Override
    public Object projectList(HttpServletRequest httpRequest, ApiRequest request) {
        Map<String, Object> requestMap = openJavaCommonService.covertParams(httpRequest, request);
        Map<String, Object> map = JsonUtils.toObj(requestMap.get("data").toString(), new TypeReference<Map<String, Object>>() {
        });
        return openJavaProjectService.listThirdProject(map,(String) requestMap.get("token"));
    }

    @Override
    public Object createBatch(HttpServletRequest httpRequest, ApiRequest request) {
        Map<String, Object> requestMap = openJavaCommonService.covertParams(httpRequest, request);
        String[] checkMainKeys = {"type","userId","projectInfo","thirdCostId","code","name"};
        String jsonParam = requestMap.get(ApiConstants.JSON_PARAM).toString();
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if(!validService.validate(jsonParam,checkKeys)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Map<String, Object> map = JsonUtils.toObj(jsonParam, new TypeReference<Map<String, Object>>() {
        });
        return openJavaProjectService.createThirdProjectByBatch(map,(String) requestMap.get("token"));
    }
}
