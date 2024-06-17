package com.fenbeitong.openapi.plugin.func.deprecated.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.ApiConstants;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.GlobalResponseCodeOld;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenApiJavaBudgetService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.OpenJavaCommonService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.ValidService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaBudgetService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaCompanyEmployeeService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * module: 迁移 open-java 项目<br/>
 * <p>
 * description: 预算模块 <br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/11 19:53
 */
@Service
@ServiceAspect
@Slf4j
public class OpenApiJavaBudgetServiceImpl implements OpenApiJavaBudgetService {

    @Autowired
    private OpenJavaCommonService openJavaCommonService;

    @Autowired
    private ValidService validService;

    @Autowired
    private OpenJavaBudgetService openJavaBudgetService;

    @Autowired
    private OpenJavaCompanyEmployeeService openJavaCompanyEmployeeService;

    @Override
    public Object createBudget(HttpServletRequest httpRequest, ApiRequest request) {
        List<String> keys = Lists.newArrayList();
        keys.add("item_list");
        keys.add("type_list");
        Map<String, Object> requestMap = this.covertParams(httpRequest, request, keys);
        log.info("新增第三方预算请求参数 {}",JsonUtils.toJson(requestMap));
        String[] checkMainKeys = {"budget_type","budget_name","warn_percent1","warn_percent2","execution_cycle","manager_msg_warn","budget_thrid_id"};
        String jsonParam = requestMap.get(ApiConstants.JSON_PARAM).toString();
        JSONObject jsonData = JSONObject.parseObject(jsonParam);
        JSONArray itemList = jsonData.getJSONArray("itemList");
        if(ObjUtils.isEmpty(itemList)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        //过滤掉itemList参数，该参数单独处理
        jsonData.remove("itemList");
        if(!validService.validate(jsonData.toJSONString(),checkKeys)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }

        for(Object item:itemList){
            JSONObject jsonItem = (JSONObject)item;
            JSONArray typeList = jsonItem.getJSONArray("typeList");
            jsonItem.remove("typeList");
            String[] checkItemKeys={"amount_limit","over_limit_control","sort"};
            List<String> checkKeys1 = new ArrayList<String>(Arrays.asList(checkItemKeys));
            if(!validService.validate(jsonItem.toJSONString(),checkKeys1)){
                throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
            }
            //校验typeList数据
            if(ObjUtils.isNotEmpty(typeList)){
                for(Object type:typeList){
                    JSONObject jsonType = (JSONObject)type;
                    String[] checkTypeKeys={"biz_type","sort"};
                    List<String> checkTypeKeys1 = new ArrayList<String>(Arrays.asList(checkTypeKeys));
                    if(!validService.validate(jsonType.toJSONString(),checkTypeKeys1)){
                        throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
                    }
                }
            }
        }
        String token = (String) requestMap.get(ApiConstants.FBT_TOKEN);
        Map<String, Object> map = JsonUtils.toObj(requestMap.get(ApiConstants.JSON_PARAM).toString(), new TypeReference<Map<String, Object>>() {
        });
        return openJavaBudgetService.createThirdBudget(map,token);
    }

    @Override
    public Object updateBudget(HttpServletRequest httpRequest, ApiRequest request) {
        List<String> keys = new ArrayList<>();
        keys.add("item_list");
        keys.add("type_list");
        Map<String, Object> requestMap = this.covertParams(httpRequest, request, keys);
        log.info("更新第三方预算请求参数 {}", JsonUtils.toJson(requestMap));
        Object requestData = requestMap.get(ApiConstants.JSON_PARAM);
        JSONObject json = openJavaCommonService.isJson(requestData.toString());
        String token = (String)requestMap.get(ApiConstants.FBT_TOKEN);
        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("id",json.get("id"));
        Object fbBudgetIdById = openJavaBudgetService.getFbBudgetIdById(paramMap, token);
        JSONObject json1 = openJavaCommonService.isJson(JsonUtils.toJson(fbBudgetIdById));
        json.put("id",json1.get("id"));
        requestMap.put("data",json.toJSONString());
        String[] checkMainKeys = {"budget_type","budget_name","warn_percent1","warn_percent2","execution_cycle","manager_msg_warn","id"};
        JSONObject jsonData = JSONObject.parseObject(json.toJSONString());
        JSONArray itemList = jsonData.getJSONArray("itemList");
        if(ObjUtils.isEmpty(itemList)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        //过滤掉itemList参数，该参数单独处理
        jsonData.remove("itemList");
        if(Boolean.FALSE.equals(validService.validate(jsonData.toJSONString(),checkKeys))){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        for(Object item:itemList){
            JSONObject jsonItem = (JSONObject) item;
            JSONArray typeList = jsonItem.getJSONArray("typeList");
            jsonItem.remove("typeList");
            String[] checkItemKeys={"amount_limit","over_limit_control","sort"};
            List<String> checkKeys1 = new ArrayList<String>(Arrays.asList(checkItemKeys));
            if(Boolean.FALSE.equals(validService.validate(jsonItem.toJSONString(),checkKeys1))){
                throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
            }
            //校验typeList数据
            if(ObjUtils.isNotEmpty(typeList)){
                for(Object type:typeList){
                    JSONObject jsonType = (JSONObject)type;
                    String[] checkTypeKeys={"biz_type","sort"};
                    List<String> checkTypeKeys1 = new ArrayList<String>(Arrays.asList(checkTypeKeys));
                    if(Boolean.FALSE.equals(validService.validate(jsonType.toJSONString(),checkTypeKeys1))){
                        throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
                    }
                }
            }
        }
        String xAuthToken =  (String) requestMap.get(ApiConstants.FBT_TOKEN);
        Map<String, Object> map = JsonUtils.toObj(requestMap.get(ApiConstants.JSON_PARAM).toString(), new TypeReference<Map<String, Object>>() {
        });
        return openJavaBudgetService.updateThirdBudget(map,xAuthToken);
    }

    @Override
    public Object deleteBudget(HttpServletRequest httpRequest, ApiRequest request) {
        Map<String, Object> requestMap = this.covertParams(httpRequest, request, null);
        log.info("删除第三方预算请求参数 {}", JsonUtils.toJson(requestMap));
        String  dataRequestString = (String)requestMap.get(ApiConstants.JSON_PARAM);
        JSONObject json = openJavaCommonService.isJson(dataRequestString);
        Object budgetId = json.get("id");
        String token = (String)requestMap.get(ApiConstants.FBT_TOKEN);
        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("id",budgetId);
        Object fbBudgetIdById = openJavaBudgetService.getFbBudgetIdById(paramMap, token);
        JSONObject jsonObject = openJavaCommonService.isJson(JsonUtils.toJson(fbBudgetIdById));
        Object fbBudgetId = jsonObject.get("id");
        json.put("id",fbBudgetId);
        requestMap.put("data",json.toJSONString());
        return openJavaBudgetService.deleteThirdBudget(json,token);
    }

    @Override
    public Object saveApplyBudget(HttpServletRequest httpRequest, ApiRequest request) {
        List<String> list = Lists.newArrayList();
        list.add("id");
        Map<String, Object> requestMap = this.covertParams(httpRequest, request, list);
        log.info("应用保存第三方预算请求参数 {}", JsonUtils.toJson(requestMap));
        String[] checkMainKeys = {"budget_type","budget_id","item_list","item_list_type"};
        JSONObject jsonData = openJavaCommonService.isJson((String) requestMap.get(ApiConstants.JSON_PARAM));
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if(!validService.validate(jsonData.toJSONString(),checkKeys)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Object data = requestMap.get("data");
        Map<String, Object> aMap = JsonUtils.toObj((String) data, new TypeReference<Map<String, Object>>() {
        });
        Integer itemListType  = (Integer) aMap.get("item_list_type");
        Integer budgetType  = (Integer) aMap.get("budget_type");
        Object strBudgetId = aMap.get("budget_id");
        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("id",strBudgetId);
        Object fbBudgetIdById = openJavaBudgetService.getFbBudgetIdById(paramMap, (String) requestMap.get(ApiConstants.FBT_TOKEN));
        if (ObjectUtils.isEmpty(fbBudgetIdById)) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getMessage());
        }
        JSONObject jsonObject = openJavaCommonService.isJson(fbBudgetIdById.toString());
        Object fbBudgetId = jsonObject.get("id");
        aMap.put("budget_id",fbBudgetId);
        String companyId = (String)requestMap.get("company_id");
        //根据type判断ID类型
        Object itemList = aMap.get("item_list");
        HashMap<String, Object> objectObjectHashMap = Maps.newHashMap();
        objectObjectHashMap.put("companyId",companyId);
        objectObjectHashMap.put("ids",itemList);
        objectObjectHashMap.put("type",2);

        //为第三方ID，根据第三方ID查询分贝ID
        if(itemListType == 2){
            //为第三方ID，根据第三方ID查询分贝ID
            if(budgetType == 1){
                objectObjectHashMap.put("businessType",3);
                //部门ID
            }else if(budgetType == 2){
                objectObjectHashMap.put("businessType",1);
            }else{//项目ID
                objectObjectHashMap.put("businessType",2);
            }

            Object idExchange = openJavaCompanyEmployeeService.getIdExchange(objectObjectHashMap,(String) requestMap.get(ApiConstants.FBT_TOKEN));
            Map<String, Object> map = JsonUtils.toObj((String) idExchange, new TypeReference<Map<String, Object>>() {
            });
            Collection<Object> values = map.values();
            List itemList1 =(List)itemList;
            //判断请求的数据数量与返回的数据数量是否相等，如果不相等说明有不一致的数据，需要提示错误信息
            if(values.size() != itemList1.size() ){
                throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
            }
            aMap.put("item_list",values);
            aMap.put("budget_type",budgetType);
            aMap.remove("item_list_type");
            String s1 = JsonUtils.toJson(aMap);
            requestMap.put("data",s1);
        }else{
            aMap.remove("item_list_type");
            requestMap.put("data",JsonUtils.toJson(aMap));
        }
        Map<String, Object> map = JsonUtils.toObj((String) requestMap.get("data"), new TypeReference<Map<String, Object>>() {
        });
        return openJavaBudgetService.thirdBudgetApplySave(map,(String) requestMap.get(ApiConstants.FBT_TOKEN));
    }

    @Override
    public Object queryBudgetList(HttpServletRequest httpRequest, ApiRequest request) {
        List<String> list = Lists.newArrayList();
        list.add("id");
        Map<String, Object> requestMap = this.covertParams(httpRequest, request, list);
        log.info("查询第三方预算列表请求参数 {}", JsonUtils.toJson(requestMap));
        String[] checkMainKeys = {"budget_type","page_index","page_size"};
        String jsonParam = requestMap.get(ApiConstants.JSON_PARAM).toString();
        JSONObject jsonData = openJavaCommonService.isJson(jsonParam);
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if(!validService.validate(jsonData.toJSONString(),checkKeys)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        String token  = (String)requestMap.get("token");
        Integer budgetType = jsonData.getInteger("budget_type");
        Integer pageIndex = jsonData.getInteger("page_index");
        Integer pageSize = jsonData.getInteger("page_size");
        requestMap.put("budget_type",budgetType);
        requestMap.put("page_index",pageIndex);
        requestMap.put("page_size",pageSize);
        requestMap.remove("data");
        return openJavaBudgetService.getBudgetList(requestMap,token);
    }

    @Override
    public Object queryBudgetDetail(HttpServletRequest httpRequest, ApiRequest request) {
        Map<String, Object> requestMap = this.covertParams(httpRequest, request, null);
        log.info("查询第三方预算详情请求参数 {}", JsonUtils.toJson(requestMap));
        Object dataRequestString = requestMap.get(ApiConstants.JSON_PARAM);
        JSONObject json = openJavaCommonService.isJson(dataRequestString.toString());
        Object budgetId = json.get("id");
        String token = (String)requestMap.get(ApiConstants.FBT_TOKEN);
        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("id",budgetId);
        Object fbBudgetIdById = openJavaBudgetService.getFbBudgetIdById(paramMap, token);
        JSONObject jsonObject = openJavaCommonService.isJson(JsonUtils.toJson(fbBudgetIdById));
        Object fbBudgetId = jsonObject.get("id");
        //没有查询到分贝ID
        if(ObjUtils.isEmpty(fbBudgetId)){
            throw new FinhubException(GlobalResponseCodeOld.BUDGET_ID_IS_NOT_EXIST.getCode(),GlobalResponseCodeOld.BUDGET_ID_IS_NOT_EXIST.getMessage());
        }
        Map<String,Object> paramsMap = Maps.newHashMap();
        paramsMap.put("id",fbBudgetId);
        return openJavaBudgetService.getThirdBudgetDetail(paramsMap,token);
    }

    @Override
    public Object queryBudgetProgress(HttpServletRequest httpRequest, ApiRequest request) {
        List<String> list = Lists.newArrayList();
        list.add("id");
        Map<String, Object> requestMap = this.covertParams(httpRequest, request, list);
        log.info("查询第三方预算进度请求参数 {}", JsonUtils.toJson(requestMap));
        String[] checkMainKeys = {"budget_type","company_id","type"};
        String[] checkEmployeeIdKeys = {"employee_id"};
        String[] checkOrgIdKeys = {"department_id"};
        String[] checkCostIdKeys = {"cost_center_id"};
        JSONObject jsonData = openJavaCommonService.isJson(requestMap.get(ApiConstants.JSON_PARAM).toString());
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if(!validService.validate(jsonData.toJSONString(),checkKeys)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Integer budgetType = jsonData.getInteger("budget_type");
        Integer type = jsonData.getInteger("type");
        String employeeId = "";
        String orgId = "";
        String costCenterId = "";
        //根据业务类型判断是人员ID，部门ID，项目中心ID
        if(1 == budgetType){
          // 第三方人员ID
            if(2 == type){
                employeeId = jsonData.getString("employee_id");
            }
            List<String> checkKeys1 = new ArrayList<String>(Arrays.asList(checkEmployeeIdKeys));
            if(!validService.validate(jsonData.toJSONString(),checkKeys1)){
                throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
            }
        }
        if(2 == budgetType){
            if(2 == type){
                //第三方部门ID
                orgId = jsonData.getString("department_id");
            }
            List<String> checkKeys3 = new ArrayList<String>(Arrays.asList(checkOrgIdKeys));
            if(!validService.validate(jsonData.toJSONString(),checkKeys3)){
                throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
            }
        }
        if(3 == budgetType){
            if(2 == type){
                //第三方项目ID
                costCenterId = jsonData.getString("cost_center_id");
            }
            List<String> checkKeys2 = new ArrayList<String>(Arrays.asList(checkCostIdKeys));
            if(!validService.validate(jsonData.toJSONString(),checkKeys2)){
                throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
            }
        }
        HashMap<String, Object> stringObjectHashMap = Maps.newHashMap();
        String companyId = (String) httpRequest.getAttribute("companyId");
        stringObjectHashMap.put("companyId",companyId);
        String token = (String) requestMap.get("token");
        List listAll = Lists.newArrayList();
        if(StringUtils.isNotBlank(employeeId)){
            //传递的是人员ID,并且一定是第三方ID
            listAll.add(employeeId);
            stringObjectHashMap.put("ids",listAll);
            stringObjectHashMap.put("type",2);
            stringObjectHashMap.put("businessType",3);
            String s = JsonUtils.toJson(stringObjectHashMap);
            requestMap.put("data",s);
            requestMap.remove("token");
            Object idExchange = openJavaCompanyEmployeeService.getIdExchange(stringObjectHashMap, token);
            Map<String, Object> map = JsonUtils.toObj(JsonUtils.toJson(idExchange), new TypeReference<Map<String, Object>>() {
            });
            Collection<Object> values = map.values();
            List idExchangeList = new ArrayList(values);
            Object o = idExchangeList.get(0);
            if(budgetType == 1){
                //人员
                jsonData.put("employee_id",o);
            }else if(budgetType == 2){
                jsonData.put("department_id",o);
            }else{
                jsonData.put("cost_center_id",o);
            }
            requestMap.put("data",jsonData.toJSONString());
        }

        if(StringUtils.isNotBlank(orgId)){
            //传递的是部门ID,并且一定是第三方ID
            listAll.add(orgId);
            stringObjectHashMap.put("ids",listAll);
            stringObjectHashMap.put("type",2);
            stringObjectHashMap.put("businessType",1);
            String s = JsonUtils.toJson(stringObjectHashMap);
            requestMap.remove("token");
            requestMap.put("data",s);
            Object idExchange = openJavaCompanyEmployeeService.getIdExchange(stringObjectHashMap, token);
            Map<String, Object> map = JsonUtils.toObj(JsonUtils.toJson(idExchange), new TypeReference<Map<String, Object>>() {
            });
            Collection<Object> values = map.values();
            List idExchangeList = new ArrayList(values);
            Object o = idExchangeList.get(0);
            if(budgetType == 1){
                //人员
                jsonData.put("employee_id",o);
            }else if(budgetType == 2){
                jsonData.put("department_id",o);
            }else{
                jsonData.put("cost_center_id",o);
            }
            requestMap.put("data",jsonData.toJSONString());
        }
        if(StringUtils.isNotBlank(costCenterId)){
            //传递的是部门ID,并且一定是第三方ID
            listAll.add(costCenterId);
            stringObjectHashMap.put("ids",listAll);
            stringObjectHashMap.put("type",2);
            stringObjectHashMap.put("businessType",2);
            String s = JsonUtils.toJson(stringObjectHashMap);
            requestMap.put("data",s);
            Object idExchange = openJavaCompanyEmployeeService.getIdExchange(stringObjectHashMap, token);
            Map<String, Object> map = JsonUtils.toObj(JsonUtils.toJson(idExchange), new TypeReference<Map<String, Object>>() {
            });
            Collection<Object> values = map.values();
            List idExchangeList = new ArrayList(values);
            Object o = idExchangeList.get(0);
            if(budgetType == 1){
                //人员
                jsonData.put("employee_id",o);
            }else if(budgetType == 2){
                jsonData.put("department_id",o);
            }else{
                jsonData.put("cost_center_id",o);
            }
            requestMap.put("data",jsonData.toJSONString());
        }
        // Call service
        requestMap.remove("token");
        Map<String, Object> map = JsonUtils.toObj((String) requestMap.get("data"), new TypeReference<Map<String, Object>>() {
        });
        return openJavaBudgetService.getBudgetProgress(map,token);
    }


    private Map<String, Object> covertParams(HttpServletRequest httpRequest,ApiRequest request,List<String> keys) {
        String companyId = (String) httpRequest.getAttribute("companyId");
        Map<String, Object> param = Maps.newHashMap();
        param.put(ApiConstants.USER_ID,request.getEmployeeId());
        param.put(ApiConstants.COMPANY_ID,companyId);
        if (ApiConstants.APP_TYPE_FBT.equals(String.valueOf(request.getEmployeeType()))) {
            param.put(ApiConstants.APP_TYPE, ApiConstants.APP_TYPE_FBT);
            param.put(ApiConstants.UC_APP_TYPE,ApiConstants.UC_APP_TYPE_FBT);
        } else {
            param.put(ApiConstants.APP_TYPE, ApiConstants.APP_TYPE_THIRD);
            param.put(ApiConstants.UC_APP_TYPE, ApiConstants.UC_APP_TYPE_THIRD);
        }
        String token = openJavaCommonService.getToken(param, true);
        Map<String, Object> requestMap = Maps.newHashMap();
        requestMap.put(ApiConstants.FBT_TOKEN,StringUtils.isNotBlank(token) ? token : "");
        if (StringUtils.isNotBlank(request.getData())) {
            String jsonParam = openJavaCommonService.convertUnderScoreToCamel(request.getData(), keys);
            JSONObject companyIdJsonObject = openJavaCommonService.isJson(jsonParam);
            companyIdJsonObject.put("company_id",companyId);
            jsonParam = JsonUtils.toJson(companyIdJsonObject);
            requestMap.put(ApiConstants.JSON_PARAM,jsonParam);
            requestMap.put("company_id",companyId);
        } else {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        return requestMap;
    }
}
