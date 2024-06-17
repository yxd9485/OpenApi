package com.fenbeitong.openapi.plugin.func.deprecated.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.GlobalResponseCodeOld;
import com.fenbeitong.openapi.plugin.func.deprecated.dto.OrgUnitDetailResult;
import com.fenbeitong.openapi.plugin.func.deprecated.dto.employee.CompanySuperAdminDTO;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenApiJavaThirdService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.OpenJavaCommonService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.ValidService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaCompanyService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaOtherService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaProjectService;
import com.fenbeitong.openapi.plugin.support.employee.dto.EmployeeThirdRequestDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.saasplus.api.model.dto.rule.TaxiApproveRuleContract;
import com.fenbeitong.saasplus.api.service.rule.ITaxiApproveRuleService;
import com.fenbeitong.usercenter.api.model.dto.orgunit.OrgUnitResult;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/11 17:41
 */
@Service
@ServiceAspect
@Slf4j
public class OpenApiJavaThirdServiceImpl implements OpenApiJavaThirdService {

    @Autowired
    private OpenJavaOtherService openJavaOtherService;

    @Autowired
    private OpenJavaCommonService openJavaCommonService;

    @Autowired
    private ValidService validService;

    @DubboReference(check = false)
    private IOrgUnitService iOrgUnitService;

    @Autowired
    private OpenJavaCompanyService openJavaCompanyService;

    @DubboReference(check = false)
    private ITaxiApproveRuleService iTaxiApproveRuleService;

    @Autowired
    private OpenJavaProjectService openJavaProjectService;


    @Override
    public Object queryCityList(HttpServletRequest httpRequest, ApiRequest request) {
        String token = request.getAccessToken();
        String cityListData = request.getData();
        JSONObject cityListJSonObject = JSONObject.parseObject(cityListData);
        if (!cityListJSonObject.containsKey("type")
            || ObjectUtils.isEmpty(cityListJSonObject.getInteger("type"))
            || !cityListJSonObject.containsKey("city_key")
            || ObjectUtils.isEmpty(cityListJSonObject.getString("city_key"))
        ) {
            throw new FinhubException(GlobalResponseCodeOld.CITY_LIST_TYPE_DATA_IS_NULL.getCode(),
                GlobalResponseCodeOld.CITY_LIST_TYPE_DATA_IS_NULL.getMessage());
        }
        Integer type = cityListJSonObject.getInteger("type");
        String cityKey = cityListJSonObject.getString("city_key");
        Map<String, Object> map = Maps.newHashMap();
        //火车车站列表
        if (type == 1) {
            map.put("input", cityKey);
            return openJavaOtherService.getTrainStationsByCity(map, token);
        }
        if (type == 2) {
            map.put("key_word", cityKey);
            return openJavaOtherService.getFlightCityList(map, token);
        }
        return map;
    }

    @Override
    public Object queryOrgUnitDetailByIds(HttpServletRequest httpRequest, ApiRequest request) {
        String data = request.getData();
        JSONObject detailThirdOrgJsonObject = openJavaCommonService.isJson(data);
        String companyId = (String) httpRequest.getAttribute("companyId");
        Object thirdIds = detailThirdOrgJsonObject.get("third_ids");
        Object ids = detailThirdOrgJsonObject.get("ids");
        // 添加校验
        openJavaProjectService.getEmployeeThird(EmployeeThirdRequestDTO.builder()
            .companyId(companyId)
            .employeeId(request.getEmployeeId())
            .type(1)
            .userType(request.getEmployeeType() == 0 ? 1 : 2)
            .build(), null);
        if (ObjectUtils.isEmpty(thirdIds) && ObjectUtils.isEmpty(ids)) {
            throw new FinhubException(GlobalResponseCodeOld.ORG_UNIT_ID_DATA_IS_NULL.getCode(), GlobalResponseCodeOld.ORG_UNIT_ID_DATA_IS_NULL.getMessage());
        }
        List<String> thirdOrgIds = JsonUtils.toObj(JsonUtils.toJson(thirdIds), new TypeReference<List<String>>() {
        });
        List<String> orgIds = JsonUtils.toObj(JsonUtils.toJson(ids), new TypeReference<List<String>>() {
        });

        if (!ObjectUtils.isEmpty(thirdOrgIds) && !ObjectUtils.isEmpty(orgIds)) {
            //分贝通和三方的部门id都传时默认只取分贝通的部门id集合
            thirdOrgIds = null;
        }
        List<OrgUnitResult> orgUnitResults = null;
        List<OrgUnitDetailResult> detailResult = new ArrayList<>();
        Integer orgIdsType = validService.parameterTypeValid(orgIds, 0);
        Integer thirdOrgIdsType = validService.parameterTypeValid(thirdOrgIds, 0);
        if ((!ObjectUtils.isEmpty(orgIds) && orgIdsType != 9) || (!ObjectUtils.isEmpty(thirdOrgIds) && thirdOrgIdsType != 9)) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }

        if(!ObjectUtils.isEmpty(orgIds)){
            List<List<String>> partition = Lists.partition(orgIds, 60);
            for(List<String> partOrgIds:partition){
                log.info("第三方部门集合详情请求参数，orgIds={},thirdOrgIds={}", JsonUtils.toJson(partOrgIds),JsonUtils.toJson(thirdOrgIds));
                orgUnitResults = iOrgUnitService.queryOrgUnitResultListByCompanyIdAndOrgUnitIdsOrThirdOrgUnitIds(companyId,partOrgIds,thirdOrgIds);
                log.info("第三方部门集合详情返回，detailOrgIdsResponse={}", JsonUtils.toJson(orgUnitResults));
                convertResult(orgUnitResults, detailResult);
            }
        }
        if(!ObjectUtils.isEmpty(thirdOrgIds)){
            List<List<String>> partition = Lists.partition(thirdOrgIds, 60);
            for(List<String> partThirdOrgIds:partition){
                log.info("第三方部门集合详情请求参数，orgIds={},thirdOrgIds={}", JsonUtils.toJson(orgIds), JsonUtils.toJson(partThirdOrgIds));
                orgUnitResults = iOrgUnitService.queryOrgUnitResultListByCompanyIdAndOrgUnitIdsOrThirdOrgUnitIds(companyId,orgIds,partThirdOrgIds);
                log.info("第三方部门集合详情返回，detailOrgIdsResponse={}", JsonUtils.toJson(orgUnitResults));
                convertResult(orgUnitResults, detailResult);
            }
        }
        return detailResult;
    }

    @Override
    public Object queryAdmin(HttpServletRequest httpRequest, ApiRequest request) {
        String companyId = (String) httpRequest.getAttribute("companyId");
        Map<String, String> companyMap = Maps.newHashMap();
        companyMap.put("company_id", companyId);
        return openJavaCompanyService.getAdminByCompanyId(companyMap, null);
    }

    @Override
    public Object queryCompanyRoleAuth(HttpServletRequest httpRequest, ApiRequest request) {
        String companyRoleAuthInfo = request.getData();
        String[] checkMainKeys = {"operator_role", "empower_type", "source", "type", "delete_history", "role_ids"};
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if (!validService.validate(companyRoleAuthInfo, checkKeys)) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        String companyId = (String) httpRequest.getAttribute("companyId");
        JSONObject json = openJavaCommonService.isJson(companyRoleAuthInfo);
        Object o = this.queryAdmin(httpRequest, request);
        CompanySuperAdminDTO companySuperAdminDTO = JsonUtils.toObj(JsonUtils.toJson(o), new TypeReference<CompanySuperAdminDTO>() {
        });
        json.put("company_id", companyId);
        json.put("operator_id",companySuperAdminDTO.getEmployee().getId());
        log.info("批量授权部门主管和角色请求参数:companyRoleAuthInfo={}", json);
        Map<String, Object> map = JsonUtils.toObj(JSONObject.toJSONString(json), new TypeReference<Map<String, Object>>() {
        });
        return openJavaCompanyService.getCompanyRoleAuth(map, null);
    }

    @Override
    public Object queryCompanyRoles(HttpServletRequest httpRequest, ApiRequest request) {
        String data = request.getData();
        String companyId = (String) httpRequest.getAttribute("companyId");
        JSONObject json = openJavaCommonService.isJson(data);
        log.info("根据公司ID查询公司角色列表信息请求参数:companyRolesInfo={}", json);
        json.put("company_id",companyId);
        return openJavaCompanyService.getCompanyRoleList(json,null);
    }

    @Override
    public Object queryApplyDetailById(HttpServletRequest httpRequest, ApiRequest request) {
        String data = request.getData();
        String[] checkMainKeys = {"rule_id"};
        List<String> checkKeys = new ArrayList<String>(Arrays.asList(checkMainKeys));
        if (!validService.validate(data, checkKeys)) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(),GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        Map<String, Integer> dataMap = JsonUtils.toObj(data, new TypeReference<Map<String, Integer>>() {
        });
        Integer ruleId = dataMap.get("rule_id");
        Map<String, String> aMap = this.parameterValide(httpRequest, request);
        String employeeId = aMap.get("employee_id");
        String employeeType = aMap.get("employee_type");
        String companyId = aMap.get("company_id");
        //openJavaCommonService.covertParams(httpRequest, request);
        log.info("根据规则ID查询审批规则详情请求数据:apiApproveCreateData={}", data);
        log.info("根据规则ID查询审批规则详情人员参数: {}, 类型参数: {}", employeeId, employeeType);
        TaxiApproveRuleContract taxiApproveRuleContract = iTaxiApproveRuleService.queryRuleById(ruleId, companyId);
        log.info("根据申请用车规则ID查询规则详情返回结果 {}", JsonUtils.toJson(taxiApproveRuleContract));
        if (ObjUtils.isNull(taxiApproveRuleContract)) {
            throw new FinhubException(-1, "规则ID不存在");
        }
        return JsonUtils.toObj(JsonUtils.toJson(taxiApproveRuleContract), new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public Object queryCarApproveType(HttpServletRequest httpRequest, ApiRequest request) {
        log.info("创建审批单请求数据:apiApproveCreateData={}", request.getData());
        log.info("创建审批单人员参数: {}, 类型参数: {}", request.getEmployeeId(), request.getEmployeeType());
        String companyId = (String) httpRequest.getAttribute("companyId");
        Map<String, Object> companyMap = Maps.newHashMap();
        companyMap.put("company_id",companyId);
        companyMap.put("user_id",request.getEmployeeId());
        companyMap.put("appType",request.getEmployeeType());
        String token = openJavaCommonService.getToken(companyMap, true);
        Map<String, Object> map = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(request.getData())) {
            map = JsonUtils.toObj(request.getData(), new TypeReference<Map<String, Object>>() {
            });
        }
        return openJavaOtherService.getCarApproveType(map,token);
    }


    private void convertResult(List<OrgUnitResult> orgUnitResults, List<OrgUnitDetailResult> detailResult) {
        if (CollectionUtils.isEmpty(orgUnitResults)) {
            return;
        }
        for (OrgUnitResult orgUnitResult : orgUnitResults) {
            OrgUnitDetailResult newDetail = new OrgUnitDetailResult();
            try {
                BeanUtils.copyProperties(newDetail,  orgUnitResult);
                newDetail.setOrg_unit_code(orgUnitResult.getOrgUnitCode());
                detailResult.add(newDetail);
            } catch (Exception e) {
                log.info("转换部门详情失败 :{}", e.getMessage());
            }
        }
    }


    private Map<String, String> parameterValide(HttpServletRequest httpRequest, ApiRequest request) {
        //验证token
        String employeeId = request.getEmployeeId();
        Integer employeeT = request.getEmployeeType();
        String companyId = (String) httpRequest.getAttribute("companyId");
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", employeeId);
        param.put("company_id", companyId);
        if (employeeT == 0) {
            param.put("appType", 0);
        } else {
            param.put("appType", 1);
        }
        String atoken = openJavaCommonService.getToken(param,true);
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("employee_id", employeeId);
        paramMap.put("employee_type",String.valueOf(employeeT));
        paramMap.put("company_id", companyId);
        paramMap.put("atoken", atoken);
        return paramMap;
    }




}
