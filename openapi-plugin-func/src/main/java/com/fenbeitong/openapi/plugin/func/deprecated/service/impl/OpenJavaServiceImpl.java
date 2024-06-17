package com.fenbeitong.openapi.plugin.func.deprecated.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.ApiConstants;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.GlobalResponseCodeOld;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.OpenJavaOldJsonUtil;
import com.fenbeitong.openapi.plugin.func.deprecated.dto.auth.OpenAuthThirdUser;
import com.fenbeitong.openapi.plugin.func.deprecated.dto.auth.OpenTokenRes;
import com.fenbeitong.openapi.plugin.func.deprecated.dto.employee.OpenEmployeeIDetailRespDTO;
import com.fenbeitong.openapi.plugin.func.deprecated.dto.employee.QueryThirdEmployeeRespDTO;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenJavaService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.OpenJavaCommonService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.ValidService;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.support.common.constant.OpenCoreConstant;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PhoneValidateDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PhoneValidate;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaCompanyEmployeeService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaDepartmentService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaProjectService;
import com.fenbeitong.openapi.plugin.support.employee.dto.EmployeeThirdRequestDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.util.ApiJwtTokenTool;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.utils.ObjUtils;
import com.luastar.swift.base.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * module: 迁移openapi-java项目<br/>
 * <p>
 * description: 获取token<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/5 17:10
 * @since 2.0
 */
@Service
@ServiceAspect
@Slf4j
public class OpenJavaServiceImpl implements OpenJavaService {

    @Value("${host.app.web.url}")
    private String webHost;

    @Value("${fe.sign.key}")
    private String signKey;

    @Value("${host.usercenter}")
    private String ucHost;

    @Value("${idg.id}")
    private String IDG_COMPANY_ID;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PhoneValidateDao phoneValidateDao;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private ApiJwtTokenTool apiJwtTokenTool;

    @Autowired
    private OpenJavaProjectService openJavaProjectService;

    @Autowired
    private OpenJavaCompanyEmployeeService companyEmployeeService;

    @Autowired
    private ValidService validService;

    @Autowired
    private OpenJavaDepartmentService service;

    @Autowired
    private OpenJavaCommonService openJavaCommonService;



    private static final String FULL_NAME = "fullName";
    private static final String ORG_UNIT_FULL_NAME = "orgUnitFullName";
    private static final String ORG_UNIT_CODE = "orgUnitCode";
    private static final String APP_ID="app_id";
    private static final String APP_KEY="app_key";
    private static final String TP_USER_ID="tp_user_id";
    private static final String TP_PHONE = "tp_mobile";





    @Override
    public String getToken(String appId, String appKey) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appKey)) {
            throw new OpenApiFuncException(GlobalResponseCodeOld.APP_DATA_IS_NULL.getCode(), GlobalResponseCodeOld.APP_DATA_IS_NULL.getMessage());
        }
        log.info("第三方传递，进行分发鉴权Token，app_id={},app_key={}", appId, appKey);
        String key = appId + appKey;
        String apiString = (String) redisTemplate.opsForValue().get(key);
        String token = null;
        if (StringUtils.isBlank(apiString)) {
            AuthDefinition authInfoByAppId = authDefinitionDao.getOpenAuthInfoByAppId(appId, appKey);
            if (authInfoByAppId == null) {
                throw new OpenApiFuncException(GlobalResponseCodeOld.APP_KEY_ERROR.getCode(), GlobalResponseCodeOld.APP_KEY_ERROR.getMessage());
            }
            Map<String, String> jwt = Maps.newHashMap();
            jwt.put("appId", appId);
            try {
                token = apiJwtTokenTool.genJWTToken(jwt).getToken();
            } catch (UnsupportedEncodingException e) {
                log.info("获取token异常异常信息->>>>", e);
                throw new OpenApiFuncException(GlobalResponseCodeOld.TOKEN_ERROR.getCode(), GlobalResponseCodeOld.TOKEN_ERROR.getMessage());
            }
            redisTemplate.opsForValue().set(key, token, OpenCoreConstant.KEY_COOKIES_TIMES, TimeUnit.SECONDS);
        } else {
            token = apiString;
        }
        OpenTokenRes openTokenRes = new OpenTokenRes();
        openTokenRes.setAccessToken(token);
        return JsonUtils.toJson(openTokenRes);
    }

    @Override
    public Object getEmployeeInfo(OpenEmployeeIDetailRespDTO request) {
        log.info("根据员工ID获取员工信息请求参数:employeeInfoData={}", JsonUtils.toJson(request));
        if (ObjectUtils.isEmpty(request.getEmployeeId())) {
            throw new OpenApiFuncException(GlobalResponseCodeOld.THIRD_COMPANY_EMPLOYEE_ID_DATA_IS_NULL.getCode(), GlobalResponseCodeOld.THIRD_COMPANY_EMPLOYEE_ID_DATA_IS_NULL.getMessage());
        }
        log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "获取第三方员工信息发送数据 {}", JsonUtils.toJson(request));
        Object employeeData = getEmployeeData(request);
        Map<String, Object> employeeDetail = JsonUtils.toObj(JsonUtils.toJson(employeeData), new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> employeeInfo = JsonUtils.toObj(JsonUtils.toJson(employeeDetail.get("employee")), new TypeReference<Map<String, Object>>() {
        });
        String thirdEmployeeId = (String) employeeInfo.get("thirdEmployeeId");
        List<Object> employeeCertificateBeans = JsonUtils.toObj(JsonUtils.toJson(employeeDetail.get("employeeCertificateBeans")), new TypeReference<List<Object>>() {
        });
        employeeInfo.put("third_employee_id", thirdEmployeeId);
        employeeInfo.remove("thirdEmployeeId");
        employeeDetail.put("employee", employeeInfo);
        employeeDetail.put("employee_certificate_beans", employeeCertificateBeans);
        employeeDetail.remove("employeeCertificateBeans");
        return employeeDetail;
    }

    @Override
    public Object getAddThirdProject(HttpServletRequest httpRequest, ApiRequest request) {
        Map<String, Object> requestMap =  openJavaCommonService.covertParams(httpRequest, request);
        // Check
        String[] checkMainKeys = {"type","userId","thirdCostId","code","name"};
        String jsonParam = requestMap.get(ApiConstants.JSON_PARAM).toString();
        String token =(String) requestMap.get(ApiConstants.FBT_TOKEN);
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if(!validService.validate(jsonParam,checkKeys)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Map<String, Object> map = JsonUtils.toObj(jsonParam, new TypeReference<Map<String, Object>>() {
        });
        return openJavaProjectService.addProject(map, token);
    }

    @Override
    public Object getUpdateThirdProject(HttpServletRequest httpRequest, ApiRequest request) {
        Map<String, Object> requestMap = openJavaCommonService.covertParams(httpRequest, request);
        // Check
        String[] checkMainKeys = {"type","userId"};
        String jsonParam = requestMap.get(ApiConstants.JSON_PARAM).toString();
        String token =(String) requestMap.get(ApiConstants.FBT_TOKEN);
        JSONObject jsonData = JSONObject.parseObject(jsonParam);
        if(null!=jsonData.get("id")&& org.apache.commons.lang.StringUtils.isBlank(jsonData.get("id").toString())&&null!=jsonData.get("thirdCostId")&& org.apache.commons.lang.StringUtils.isBlank(jsonData.get("thirdCostId").toString())){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if(!validService.validate(jsonParam,checkKeys)){
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Map<String, Object> map = JsonUtils.toObj(jsonParam, new TypeReference<Map<String, Object>>() {
        });
        return openJavaProjectService.updateProject(map, token);
    }

    @Override
    public Object getThirdOrgUnitDetail(HttpServletRequest httpRequest, ApiRequest request) {
        String token = request.getAccessToken();
        String detailThirdData = request.getData();
        String companyId = (String) httpRequest.getAttribute("companyId");
        JSONObject jsonDetail = isJson(detailThirdData);
        if (ObjectUtils.isEmpty(jsonDetail)) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getMessage());
        }
        if (!jsonDetail.containsKey("third_org_id")) {
            throw new FinhubException(GlobalResponseCodeOld.ORG_UNIT_ID_DATA_IS_NULL.getCode(), GlobalResponseCodeOld.ORG_UNIT_ID_DATA_IS_NULL.getMessage());
        }
        Integer thirdOrgIdType = validService.checkParameterType(jsonDetail.get("third_org_id"));
        if (thirdOrgIdType != 1) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Object thirdOrgId = jsonDetail.get("third_org_id");
        if (!jsonDetail.containsKey("type")) {
            throw new FinhubException(GlobalResponseCodeOld.ORG_UNIT_ID_TYPE_DATA_IS_NULL.getCode(), GlobalResponseCodeOld.ORG_UNIT_ID_TYPE_DATA_IS_NULL.getMessage());
        }
        Integer orgTypeType = validService.checkParameterType(jsonDetail.get("type"));
        if (orgTypeType != 1) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Object orgType = jsonDetail.get("type");
        String operatorId = "OpenApi";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("companyId", companyId);
        jsonObject.put("orgId", thirdOrgId);
        jsonObject.put("operatorId", operatorId);
        jsonObject.put("type", orgType);
        log.info("第三方部门详情请求参数，detailOrgSendData={}", JsonUtils.toJson(jsonObject));
        Object orgUnitDetail = service.getOrgUnitDetail(jsonObject, token);
        JSONObject resultData = (JSONObject) JSONObject.parse(JsonUtils.toJson(orgUnitDetail));
        if (ObjectUtils.isNotEmpty(resultData)) {
            buildResult(resultData);
        }
        return resultData;
    }

    @Override
    public Object getRegister(HttpServletRequest request) {
        String body = getBody(request);
        log.info("鉴权接口入参对象->>>{}", body);
        if (StringUtils.isBlank(body)) {
            throw new FinhubException(GlobalResponseCodeOld.APP_DATA_IS_NULL.getCode(), GlobalResponseCodeOld.APP_DATA_IS_NULL.getMessage());
        }
        AuthDefinition authInfo = JsonUtils.toObj(body, AuthDefinition.class);
        try {
            authInfo = register(authInfo);
        } catch (FinhubException e) {
            log.info("注册企业对接失败，authInfo={}", JsonUtils.toJson(authInfo));
        }
        AuthDefinition authDefinition = new AuthDefinition();
        AuthDefinition authInfoByAppId = authDefinitionDao.getAuthInfoByAppId(authInfo.getAppId());
        if (ObjectUtils.isNotEmpty(authInfoByAppId)) {
            authDefinition = authInfoByAppId;
        }
        return authDefinition;
    }

    @Override
    public Object getQueryThirdEmployee(HttpServletRequest httpRequest) {
        QueryThirdEmployeeRespDTO queryThirdEmployeeRespDTO = new QueryThirdEmployeeRespDTO();
        Map<String, Object> parameterMap = validService.transformMap(httpRequest);
        if (ObjectUtils.isEmpty(parameterMap)) {
            return queryThirdEmployeeRespDTO;
        }
        String companyId = (String) parameterMap.get("company_id");
        long timestamp = System.currentTimeMillis();
        String sign = genSign(timestamp, companyId, signKey);
        parameterMap.put("timestamp", timestamp);
        parameterMap.put("sign", sign);
        Map<String, Object> queryDataMap = getQueryThirdEmployeeData(parameterMap);
        if (ObjectUtils.isNotEmpty(queryDataMap)) {
            queryThirdEmployeeRespDTO = QueryThirdEmployeeRespDTO.builder()
                .employeeId((String) queryDataMap.get("user_id"))
                .token((String) queryDataMap.get("token"))
                .build();
        }
        return queryThirdEmployeeRespDTO;
    }

    @Override
    public Object getAuth(HttpServletRequest httpRequest) {
        Map<String, Object> parameterMap = JsonUtils.toObj(openJavaCommonService.getRequestBody(httpRequest), new TypeReference<Map<String, Object>>() {
        });
        if (ObjectUtils.isEmpty(parameterMap)) {
            throw new FinhubException(GlobalResponseCodeOld.APP_DATA_IS_NULL.getCode(),GlobalResponseCodeOld.APP_DATA_IS_NULL.getMessage());
        }
        log.info("第三方传递，进行分发鉴权Token，Body={}",JsonUtils.toJson(parameterMap));
        if (!parameterMap.containsKey(APP_ID) || ObjectUtils.isEmpty(parameterMap.get(APP_ID))) {
            throw new FinhubException(GlobalResponseCodeOld.UnEmployeeId.getCode(),GlobalResponseCodeOld.UnEmployeeId.getMessage());
        }
        if (!parameterMap.containsKey(APP_KEY) || ObjectUtils.isEmpty(parameterMap.get(APP_KEY))) {
            throw new FinhubException(GlobalResponseCodeOld.UnEmployeeId.getCode(),GlobalResponseCodeOld.UnEmployeeId.getMessage());
        }
        if (!parameterMap.containsKey(TP_USER_ID) && !parameterMap.containsKey(TP_PHONE)) {
            throw new FinhubException(GlobalResponseCodeOld.UnEmployeeId.getCode(),GlobalResponseCodeOld.UnEmployeeId.getMessage());
        }
        if (ObjectUtils.isEmpty(parameterMap.get(TP_USER_ID)) && ObjectUtils.isEmpty(parameterMap.get(TP_PHONE))) {
            throw new FinhubException(GlobalResponseCodeOld.UnEmployeeId.getCode(),GlobalResponseCodeOld.UnEmployeeId.getMessage());
        }
        String tpMobile =(String) parameterMap.get(TP_PHONE);
        String appId = (String) parameterMap.get(APP_ID);
        String tpUserId = (String) parameterMap.get(TP_USER_ID);
        String appKey =(String) parameterMap.get(APP_KEY);

        if (parameterMap.get(APP_ID).equals(IDG_COMPANY_ID)) {
            if(!StringUtils.isBlank(tpMobile) && !tpMobile.equals("29900000278") && !tpMobile.equals("29900000254") && !tpMobile.equals("29900000091") && !tpMobile.equals("29900000043") && !tpMobile.startsWith("1")){
               //  Long virtualPhone =null;
                //使用的是虚拟手机号，进行分贝通的虚拟手机号的获取
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                //正常情况是要判断取出的是否为long类型，然后再进行类型转换
                Long virtualPhone = (Long)valueOperations.get(appId+"-"+tpUserId+"-"+tpMobile);
                tpMobile = String.valueOf(virtualPhone);
                log.info("进行获取token时获取的虚拟手机号 {}",virtualPhone);
            }
        } else {
            Long virtualPhone =null;
            //根据公司ID查询是否需要手机号校验
            PhoneValidate phoneValidate = phoneValidateDao.queryPhoneValidate(appId);
            if(phoneValidate != null && phoneValidate.getPhoneValidate() == 1 && !StringUtils.isBlank(tpMobile) && !(tpMobile.startsWith("1"))){
                //使用的是虚拟手机号，进行分贝通的虚拟手机号的获取
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                virtualPhone = (Long)valueOperations.get(appId+"-"+tpUserId+"-"+tpMobile);
                tpMobile = String.valueOf(virtualPhone);
                log.info("进行获取token时获取的虚拟手机号 {}",virtualPhone);
            }
        }
        AuthDefinition authInfo = authDefinitionDao.getAuthInfoByAppIdAndAppKey(appId, appKey);
        if (authInfo == null) {
            throw new FinhubException(GlobalResponseCodeOld.AppKeyError.getCode(),GlobalResponseCodeOld.AppKeyError.getMessage());
        }
        if(StringUtils.isNotBlank(tpMobile)){
            //需要进行虚拟手机号过滤
            if(tpMobile.startsWith("1")){
                //如果以1开头则进行过滤
                boolean mobile = isMobile(tpMobile);
                if (!mobile) {
                    //手机号格式错误
                    throw new FinhubException(GlobalResponseCodeOld.MOBILE_IS_ERROR.getCode(),GlobalResponseCodeOld.MOBILE_IS_ERROR.getMessage());
                }
            }else{//虚拟手机号，只对位数校验
                if(tpMobile.length() != 11){
                    //手机号格式错误
                    throw new FinhubException(GlobalResponseCodeOld.MOBILE_IS_ERROR.getCode(),GlobalResponseCodeOld.MOBILE_IS_ERROR.getMessage());
                }
            }
        }

        /**
         * @param tpUserId 用户Id
         * @param tpMobile 手机号
         * @param appId 企业ID
         * @param appType 类型，0为分贝用户ID，1为第三方用户ID
         */
        Map<String,Object> param = new HashMap<>();
        param.put("user_id",tpUserId);
        param.put("phone_num",tpMobile);
        param.put("company_id",appId);
        param.put("appType",1);
        param.put("platform", CompanyLoginChannelEnum.CUSTOM_H5.getPlatform());
        param.put("entrance", CompanyLoginChannelEnum.CUSTOM_H5.getEntrance());
        String token = openJavaCommonService.getToken(param, true);
        Map map = new HashMap();
        map.put("token",token);
        return map;
    }


    /**
     * 查询 数据data
     *
     * @param param
     * @return
     */
    private Map<String, Object> getQueryThirdEmployeeData(Map<String, Object> param) {
        Map<String, Object> paramMap = Maps.newHashMap();
        if (ObjectUtils.isNotEmpty(param)) {
            Object queryEmployee = companyEmployeeService.getQueryEmployee(param, null);
            if (ObjectUtils.isNotEmpty(queryEmployee)) {
                paramMap = JsonUtils.toObj(JsonUtils.toJson(queryEmployee), new TypeReference<Map<String, Object>>() {
                });
            }
        }
        return paramMap;
    }


    /**
     * 注册应用授权信息
     */
    @Transactional(value = "api", rollbackFor = Exception.class)
    public AuthDefinition register(AuthDefinition authInfo) {
        // 鉴权信息查询对象
        Map<String, Object> authMap = JsonUtils.toObj(JsonUtils.toJson(authInfo), new TypeReference<Map<String, Object>>() {
        });
        AuthDefinition authInfoExist = authDefinitionDao.getOpenAuthInfo(authMap);
        if (authInfoExist != null) {
            throw new FinhubException(GlobalResponseCodeOld.AppIdAlreadyExist.getCode(), GlobalResponseCodeOld.AppIdAlreadyExist.getMessage());
        }
        String appKey = RandomUtils.bsonId();
        String md5Hex = RandomUtils.randomNum(10) + authInfo.getAppId();
        String signKey = DigestUtils.md5Hex(md5Hex);
        authInfo.setAppKey(appKey);
        authInfo.setSignKey(signKey);
        if (authInfo.getAppStatus() == null) {
            authInfo.setAppStatus(OpenCoreConstant.APP_STATUS_OPS);
        }
        AuthDefinition newAuthInfo = getStringUrl(authInfo);
        log.info("authInfoExist--->authInfoExist={}", JsonUtils.toJson(newAuthInfo));
        AuthDefinition authDefinition = new AuthDefinition();
        BeanUtils.copyProperties(authInfo, authDefinition);
        authDefinitionDao.insertOpenAuthInfo(authDefinition);
        return newAuthInfo;
    }

    /**
     * 生成URL
     *
     * @param authInfo
     * @return OpenAuthInfo
     */
    private AuthDefinition getStringUrl(AuthDefinition authInfo) {
        //生成URL
        String url = webHost +
            "?token=服务器颁发Token";
        authInfo.setAppUrl(url);
        return authInfo;
    }


    private void buildResult(JSONObject resultData) {
        if (null != resultData.get(ORG_UNIT_CODE)) {
            resultData.put("org_unit_code", resultData.get(ORG_UNIT_CODE));
            resultData.remove(ORG_UNIT_CODE);
        }
        if (null != resultData.get(FULL_NAME)) {
            resultData.put("full_name", resultData.get(FULL_NAME));
            resultData.remove(FULL_NAME);
        }
        if (null != resultData.get(ORG_UNIT_FULL_NAME)) {
            resultData.put("org_unit_full_name", resultData.get(ORG_UNIT_FULL_NAME));
            resultData.remove(ORG_UNIT_FULL_NAME);
        }
    }


    @Override
    public Object getCompanyEmployeeInfo(HttpServletRequest httpRequest, ApiRequest request) {
        String companyEmployeeInfoData = request.getData();
        if (ObjectUtils.isEmpty(companyEmployeeInfoData)) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getMessage());
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(companyEmployeeInfoData);
        } catch (Exception e) {
            log.info("数据转换失败-》》》", e);
            throw new FinhubException(GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getMessage());
        }
        String companyId = (String) httpRequest.getAttribute("companyId");
        jsonObject.put("companyId", companyId);
        companyEmployeeInfoData = JsonUtils.toJson(jsonObject);
        log.info("api获取公司所有员工信息请求参数据，companyEmployeeInfoData={}", companyEmployeeInfoData);
        String companyEmployeeInfo = getEmployeeInfo(companyEmployeeInfoData);
        // 校验数据
        JSONObject json = isJson(companyEmployeeInfo);
        JSONObject data = json.getJSONObject("data");
        if (ObjUtils.isEmpty(data)) {
            throw new FinhubException(GlobalResponseCodeOld.HyperloopServiceError.getCode(), GlobalResponseCodeOld.HyperloopServiceError.getMessage());
        }
        return data;
    }


    private String getEmployeeInfo(String param) {
        int pageIndex = 1;
        int pageSize = 100;
        Map<String, Object> map = JsonUtils.toObj(param, Map.class);
        map.put("pageIndex", pageIndex);
        map.put("pageSize", pageSize);
        List companyEmployeeList = Lists.newArrayList();
        List employeeList = loadEmployeeByPage(map);
        // 获取企业信息数据
        while (ObjectUtils.isNotEmpty(employeeList)) {
            companyEmployeeList.addAll(employeeList);
            map.put("pageIndex", ++pageIndex);
            employeeList = loadEmployeeByPage(map);
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("thirdEmployeeList", companyEmployeeList);
        resultMap.put("data", dataMap);
        log.info("根据公司ID查询公司人员信息返回数据 {}", resultMap);
        return JsonUtils.toJson(resultMap);
    }


    private List loadEmployeeByPage(Map<String, Object> param) {
        String newParam = joinParam(param);
        String companyEmployeeInfoUrlV2 = getCompanyEmployeeInfoUrlV2(newParam);
        String companyEmployeeResponse = RestHttpUtils.getString(companyEmployeeInfoUrlV2,null,null);
        if (ObjUtils.isBlank(companyEmployeeResponse)) {
            throw new FinhubException(GlobalResponseCodeOld.HyperloopServiceError.getCode(),GlobalResponseCodeOld.HyperloopServiceError.getMessage());
        }
        Map resultMap = JsonUtils.toObj(companyEmployeeResponse, Map.class);
        Map dataMap = resultMap == null ? null : (Map) resultMap.get("data");
        return dataMap == null ? null : (List) dataMap.get("thirdEmployeeList");
    }




    private Map<String, Object> covertParams(HttpServletRequest httpRequest, ApiRequest request) {
        // 获取入参数据
        HashMap<String, Object> param = (HashMap<String, Object>) JsonUtils.toObj(request.getData(), new TypeReference<Map<String, Object>>() {
        });
        param.put("company_id", String.valueOf(httpRequest.getAttribute("companyId")));
        param.put("user_id", request.getEmployeeId());
        if (ApiConstants.APP_TYPE_FBT.equals(httpRequest.getParameter(ApiConstants.EMPLOYEE_TYPE).trim())) {
            param.put(ApiConstants.APP_TYPE, ApiConstants.APP_TYPE_FBT);
        } else {
            param.put(ApiConstants.APP_TYPE, ApiConstants.APP_TYPE_THIRD);
        }
        String str = OpenJavaOldJsonUtil.convertUnderScoreToCamel(JsonUtils.toJson(param));
        return JsonUtils.toObj(str, new TypeReference<Map<String, Object>>() {
        });
    }


    /**
     * 获取uc 数据
     *
     * @param request
     * @return
     */
    private Object getEmployeeData(OpenEmployeeIDetailRespDTO request) {
        if (request.getUserType().equals(0)) {
            request.setUserType(1);
        } else {
            request.setUserType(2);
        }
        return openJavaProjectService.getEmployeeThird(EmployeeThirdRequestDTO.builder()
            .companyId(request.getCompanyId())
            .employeeId(request.getEmployeeId())
            .type(request.getType())
            .userType(request.getUserType())
            .build(), null);
    }

    public static JSONObject isJson(String data) {
        if (org.apache.commons.lang3.StringUtils.isBlank(data)) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getMessage());
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(data);
        } catch (Exception ex) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_JSON_DATA_IS_ERROR.getMessage());

        }
        return jsonObject;
    }

    /**
     * 解析 HttpServletRequest 获取body数据
     *
     * @param request
     * @return
     */
    private static String getBody(HttpServletRequest request) {
        String body = null;
        try {
            InputStream inputStream = request.getInputStream();
            // body = IOUtils.toString(inputStream,StandardCharsets.UTF_8);
            body = new String(IOUtils.toByteArray(inputStream), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.info("read http request failed.", e);
        }
        return body;
    }


    public static String genSign(long timestamp, String data, String signKey) {
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        param.put("timestamp", timestamp);
        param.put("data", data);
        return genSign(param, signKey);
    }

    private static String genSign(Map<String, Object> param, String signKey) {
        /** 1、生成签名的时候,将颁发的 sign_key 加入到传递的参数中，参与加密 */
        param.put("sign_key", signKey);
        /** 2、传递的参数(包含 sign_key )，已 & 形式连接 k=v，生成小写的 md5 串 */
        String joinParam = joinParam(param);
        /** 3、生成小写的 md5 串 */
        log.info("传递的参数进行加密，joinParam={}" + joinParam);
        byte[] bytes = joinParam.getBytes(StandardCharsets.UTF_8);
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

    private String getCompanyEmployeeInfoUrlV2(String param) {
        return String.format(ucHost + "/uc/inner/employee/third/operate/list/v2?%s", param);
    }

    /**
     * 手机号验证
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$"); // 验证手机号
        m = p.matcher(mobile);
        b = m.matches();
        return b;
    }


}
