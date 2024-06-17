package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.fenbei.settlement.base.enums.order.CostAttributionCategoryEnum;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.OpenApiResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.enums.QueryDepartmentTypeEnum;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenApplyRecordDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.entity.OpenApplyRecord;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenTripApplyService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.project.dto.FetchProjectReqDto;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.plugin.support.util.TripPreProcessUtil;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.sdk.dto.common.TypeEntity;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luastar.swift.base.json.JsonUtils;
import com.luastar.swift.base.net.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * open api 审批接口封装类
 *
 * @author zhaokechun
 * @date 2018/11/27 14:44
 */
@Slf4j
@ServiceAspect
@Service
public class OpenApiProcessServiceImpl {

    @Value("${host.openapi}")
    private String hostOpenapi;

    @Value("${host.saas}")
    private String saas;


    @Autowired
    private RestHttpUtils httpUtils;

    @Autowired
    private OpenApiAuthServiceImpl openApiAuthService;

    @Autowired
    private IOpenTripApplyService openTripApplyService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Autowired
    private OpenProjectService openProjectService;

    @DubboReference(check = false)
    private IOrgUnitService orgUnitService;

    @Autowired
    private OpenApplyRecordDao openApplyRecordDao;

    /**
     * 创建审批单
     *
     * @param processInfo 审批单信息
     * @param companyId   企业ID
     * @return
     */
    public OpenApiResponse create(DingtalkTripApplyProcessInfo processInfo, String companyId, String employeeId) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        String data = gson.toJson(processInfo);
        MultiValueMap params = openApiAuthService.genApiAuthParamsWithEmployee(companyId, data, employeeId, false);
        params.add("data", data);
        log.info("调用开放平台创建审批单接口, 参数: {}", params);
        String jsonText = RestHttpUtils.postFormUrlEncode(hostOpenapi + "/open/api/approve/create", null, params);
        OpenApiResponse response = gson.fromJson(jsonText, OpenApiResponse.class);
        log.info("调用开放平台创建审批单接口完成, 返回结果: {}", response);
        return response;

    }

    /**
     * 创建审批单,使用abstractTripApply,生成行程
     *
     * @param processInfo
     * @param companyId
     * @param employeeId
     * @throws Exception
     */
    public CreateApplyRespDTO createTripApprove(DingtalkTripApplyProcessInfo processInfo, String companyId, String employeeId, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        String ucEmployeeToken = userCenterService.getUcEmployeeToken(companyId, employeeId);
        TripApproveCreateReqDTO req = dingtalkTripApplyProcessInfo2TripApproveCreateReqDTO(processInfo, companyId, employeeId);
        CreateApplyRespDTO createTripApproveRespDTO = null;
        try {
            req = processTripApproveCreateReqDTO(companyId, processInstanceTopVo, req);
            createTripApproveRespDTO = openTripApplyService.createTripApprove(ucEmployeeToken, req);
        } catch (Exception e) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_SYNC_APPLY_FAILED, e.getMessage());
        }
        return createTripApproveRespDTO;
    }

    private TripApproveCreateReqDTO processTripApproveCreateReqDTO(String companyId, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo, TripApproveCreateReqDTO req) {
        try {
            log.info("初始 tripApprove -{}", JsonUtils.toJson(req));
            OpenThirdScriptConfig tripConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.TRIP_APPLY_SYNC);
            if (null != tripConfig) {
                Map processInstanceJson = JsonUtils.toObj(JsonUtils.toJson(processInstanceTopVo), Map.class);
                req = TripPreProcessUtil.tripBeforeSyncFilter(tripConfig, processInstanceJson, req);
                buildCostAttributionInfo(req);
            }
            log.info("处理后的 tripApprove -{}", JsonUtils.toJson(req));
            return req;
        } catch (Exception e) {
            log.error("后处理差旅请求失败 : {} , 返回初始请求 req", e.getMessage());
            return req;
        }
    }

    /**
     * 构建费用归属信息
     *
     * @param req 差旅请求
     */
    private void buildCostAttributionInfo(TripApproveCreateReqDTO req) {
        if (req == null) {
            return;
        }
        if (null == req.getApply()) {
            return;
        }
        TripApproveApply tripApproveApply = req.getApply();
        // 如果名称不为空,但是id为空,那么需要查询项目信息
        List<CostAttributionDTO> costAttributionDTOList = tripApproveApply.getCostAttributionList();
        boolean needUpdate = CollectionUtils.isNotBlank(costAttributionDTOList);
        if (!needUpdate) {
            return;
        }
        String companyId = tripApproveApply.getCompanyId();
        for (CostAttributionDTO costAttributionDTO : costAttributionDTOList) {
            String costAttributionName = costAttributionDTO.getCostAttributionName();
            Integer costAttributionCategory = costAttributionDTO.getCostAttributionCategory();
            if (CostAttributionCategoryEnum.CostCenter.getKey() == costAttributionCategory) {
                // 项目
                FetchProjectReqDto fetchProjectReqDto = FetchProjectReqDto.builder().projectName(costAttributionName).companyId(companyId).build();
                ListThirdProjectRespDTO projectRespDTO = openProjectService.getProjectByCompanyIdAndProjectName(fetchProjectReqDto);
                List<ListThirdProjectRespDTO.ListThirdProjectRespDTOData> dtoDataList = projectRespDTO.getData();
                if (CollectionUtils.isNotBlank(dtoDataList)) {
                    String projectName = StringUtils.isBlank(costAttributionName) ? "" : costAttributionName;
                    dtoDataList = dtoDataList.stream().filter(dto -> projectName.equals(dto.getName())).collect(Collectors.toList());
                    ListThirdProjectRespDTO.ListThirdProjectRespDTOData projectRespDTOData = CollectionUtils.isBlank(dtoDataList) ? new ListThirdProjectRespDTO.ListThirdProjectRespDTOData() : dtoDataList.get(0);
                    String projectId = projectRespDTOData.getId();
                    costAttributionDTO.setCostAttributionId(projectId);
                }
            } else {
                // 部门
                List<String> orgNames = new ArrayList<>();
                orgNames.add(costAttributionName);
                Map<String, String> result = orgUnitService.queryOrgUnitIdByCompanyIdAndOrgNames(companyId, QueryDepartmentTypeEnum.DEPARTMENT_TYPE_NAME.getType(), orgNames);
                if (null != result && null != result.get(costAttributionName)) {
                    costAttributionDTO.setCostAttributionId(result.get(costAttributionName));
                }
            }
        }
        tripApproveApply.setCostAttributionList(costAttributionDTOList);
        req.setApply(tripApproveApply);
    }

    private void buildCostAttribution(String costAttributionName, String projectId, CostAttributionDTO costAttributionDTO, CostAttributionCategoryEnum costAttributionCategoryEnum) {
        costAttributionDTO.setCostAttributionCategory(costAttributionCategoryEnum.getKey());
        costAttributionDTO.setCostAttributionId(projectId);
        costAttributionDTO.setCostAttributionName(costAttributionName);
    }

    /**
     * 撤销审批单
     *
     * @param applyId     审批单ID
     * @param applyIdType 审批单ID类型
     * @param companyId   公司ID
     * @param employeeId  员工ID
     * @return
     */
    public OpenApiResponse cancel(String applyId, Integer applyIdType, String companyId, String employeeId) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        // 撤销当前行程申请生成的用车申请
        try {
            cancelCarApplyIfNeed(applyId, applyIdType, companyId, employeeId, gson);
        } catch (Exception e) {
            log.error("撤销行程审批生成的用车申请失败", e);
        }

        OpenApiResponse response = null;
        try {
            response = cancelTripApply(applyId, applyIdType, companyId, employeeId, gson);
        } catch (Exception e) {
            log.error("行程审批撤销失败", e);
        }
        return response;

    }

    private OpenApiResponse cancelTripApply(String applyId, Integer applyIdType, String companyId, String employeeId, Gson gson) {
        Map<String, Object> dataMap = new HashMap<>(2);
        dataMap.put("apply_id", applyId);
        dataMap.put("third_type", applyIdType);
        String data = gson.toJson(dataMap);
        MultiValueMap params = openApiAuthService.genApiAuthParamsWithEmployee(companyId, data, employeeId, false);
        params.add("data", data);
        log.info("调用开放平台撤销审批单接口, 参数: {}", params);
        String jsonText = RestHttpUtils.postFormUrlEncode(hostOpenapi + "/open/api/approve/cancel", null, params);
        OpenApiResponse response = gson.fromJson(jsonText, OpenApiResponse.class);
        log.info("调用开放平台撤销审批单接口完成, 返回结果: {}", response);
        return response;
    }

    private void cancelCarApplyIfNeed(String applyId, Integer applyIdType, String companyId, String employeeId, Gson gson) {
        // 查询是否存在用当前行程审批生成的用车申请
        OpenApplyRecord openApplyRecord = openApplyRecordDao.getOpenApplyRecord(companyId, applyId + "_car", 12);
        if (openApplyRecord != null) {
            // 存在 需要撤销用车审批
            Map<String, Object> dataMap = new HashMap<>(2);
            dataMap.put("apply_id", openApplyRecord.getThirdId());
            dataMap.put("third_type", applyIdType);
            String data = gson.toJson(dataMap);
            MultiValueMap params = openApiAuthService.genApiAuthParamsWithEmployee(companyId, data, employeeId, false);
            params.add("data", data);
            log.info("存在行程审批生成的用车申请,需要撤销, 参数: {}", params);
            String jsonText = RestHttpUtils.postFormUrlEncode(hostOpenapi + "/open/api/approve/cancel", null, params);
            OpenApiResponse response = gson.fromJson(jsonText, OpenApiResponse.class);
            log.info("撤销行程审批生成的用车申请,返回结果: {}", response);
        }
    }

    /**
     * 创建审批用车申请单 (停用)
     *
     * @param processInfo 审批单信息
     * @param companyId   企业ID
     * @param employeeId  员工ID
     * @return
     */
    public OpenApiResponse createCarApprove(DingtalkTripApplyProcessInfo processInfo, String companyId, String employeeId) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        String data = gson.toJson(processInfo);
        MultiValueMap params = openApiAuthService.genApiAuthParamsWithEmployee(companyId, data, employeeId, false);
        params.add("data", data);
        log.info("调用开放平台创建审批用车申请单接口, 参数: {}", params);
        String jsonText = RestHttpUtils.postFormUrlEncode(hostOpenapi + "/open/api/approve/car/create", null, params);
        OpenApiResponse response = gson.fromJson(jsonText, OpenApiResponse.class);
        log.info("调用开放平台创建审批用车申请单接口完成, 返回结果: {}", response);
        return response;
    }


    /**
     * 用车审批单创建
     *
     * @param token
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    public Object carCreateApprove(String token, String data) throws UnsupportedEncodingException {
        //获取审批单列表接口
        String result = postResult(token, saas.concat("/apply/third/applyTaxi/create"), data);
        if (StringUtils.isBlank(result)) {
            throw new FinhubException(0, "用车审批单创建创建失败");
        }
        Map map = JsonUtils.toObj(result, Map.class);
        Integer code = (Integer) map.get("code");
        if (code != 0) {
            String msg = (String) map.get("msg");
            throw new FinhubException(code, msg);
        }
        log.info("创建审批用车单返回数据:result={}", map.get("data"));
        return map.get("data");
    }

    /**
     * UC 创建审批用车申请单
     */
    public static String postResult(String xAuthToken, String url, String data) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put("X-Auth-Token", xAuthToken);
        headMap.put("Content-Type", "application/json");
        log.info("client Post请求token和数据,{},{},{}", xAuthToken, url, data);
        String result = HttpClientUtils.postBody(url, data, headMap);
        log.info("client Post返回结果，result={},{}", url, result);
        return result;
    }

    private TripApproveCreateReqDTO dingtalkTripApplyProcessInfo2TripApproveCreateReqDTO(DingtalkTripApplyProcessInfo dingtalkTripApplyProcessInfo, String companyId, String employeeId) {
        DingtalkTripApplyProcessInfo.ApplyBean dingApply = dingtalkTripApplyProcessInfo.getApply();
        List<DingtalkTripApplyProcessInfo.CustomField> dingCustomFields = dingtalkTripApplyProcessInfo.getCustomFields();
        List<DingtalkTripApplyProcessInfo.TripListBean> dingTripList = dingtalkTripApplyProcessInfo.getTripList();
        List<DingtalkTripApplyProcessInfo.Guest> dingGuestList = dingtalkTripApplyProcessInfo.getGuestList();
        TripApproveCreateReqDTO tripApproveCreateReqDTO = new TripApproveCreateReqDTO();
        TripApproveApply apply = new TripApproveApply();
        List<TypeEntity> customFields = new ArrayList<>();
        List<TripApproveDetail> tripList = new ArrayList<>();
        List<TripApproveGuest> guestList = new ArrayList<>();
        tripApproveCreateReqDTO.setApply(apply);
        tripApproveCreateReqDTO.setCustomFields(customFields);
        tripApproveCreateReqDTO.setTripList(tripList);
        tripApproveCreateReqDTO.setGuestList(guestList);
        apply.setType(dingApply.getType());
        apply.setFlowType(dingApply.getFlowType());
        apply.setBudget(dingApply.getBudget());
        apply.setThirdId(dingApply.getThirdId());
        apply.setThirdRemark(dingApply.getThirdRemark());
        apply.setApplyReason(dingApply.getApplyReason());
        apply.setCompanyId(companyId);
        apply.setEmployeeId(employeeId);
        if (CollectionUtils.isNotBlank(dingGuestList)) {
            dingGuestList.stream().forEach(dingGuestInfo -> {
                TripApproveGuest tripApproveGuest = new TripApproveGuest();
                tripApproveGuest.setEmployeeType(dingGuestInfo.getEmployeeType());
                tripApproveGuest.setId(dingGuestInfo.getId());
                tripApproveGuest.setIsEmployee(dingGuestInfo.getWhetherEmployee());
                tripApproveGuest.setName(dingGuestInfo.getName());
                guestList.add(tripApproveGuest);
            });
        }
        if (!ObjectUtils.isEmpty(dingCustomFields)) {
            for (DingtalkTripApplyProcessInfo.CustomField dingCustomField : dingCustomFields) {
                TypeEntity typeEntity = new TypeEntity();
                typeEntity.setType(dingCustomField.getType());
                typeEntity.setValue(dingCustomField.getValue());
                customFields.add(typeEntity);
            }
        }
        if (!ObjectUtils.isEmpty(dingTripList)) {
            for (DingtalkTripApplyProcessInfo.TripListBean dingTrip : dingTripList) {
                TripApproveDetail tripApproveDetail = new TripApproveDetail();
                tripApproveDetail.setType(dingTrip.getType());
                tripApproveDetail.setTripType(dingTrip.getTripType());
                tripApproveDetail.setStartCityId(dingTrip.getStartCityId());
                tripApproveDetail.setArrivalCityId(dingTrip.getArrivalCityId());
                tripApproveDetail.setStartTime(DateUtils.toDate(dingTrip.getStartTime()));
                tripApproveDetail.setEndTime(DateUtils.toDate(dingTrip.getEndTime()));
                tripApproveDetail.setBackStartTime(DateUtils.toDate(dingTrip.getBackStartTime()));
                tripApproveDetail.setBackEndTime(DateUtils.toDate(dingTrip.getBackEndTime()));
                tripApproveDetail.setEstimatedAmount(dingTrip.getEstimatedAmount());
                tripList.add(tripApproveDetail);
            }
        }
        return tripApproveCreateReqDTO;
    }
}
