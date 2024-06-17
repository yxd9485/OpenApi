package com.fenbeitong.openapi.plugin.customize.rendajincang.service.impl;

import com.fenbeitong.common.utils.basis.StringUtil;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.rendajincang.constant.RdjcConstant;
import com.fenbeitong.openapi.plugin.customize.rendajincang.dto.CustformApplyRequestDTO;
import com.fenbeitong.openapi.plugin.customize.rendajincang.dto.RdjcCustformApplyBillDTO;
import com.fenbeitong.openapi.plugin.customize.rendajincang.service.AbstractCustformApplyServiceImpl;
import com.fenbeitong.openapi.plugin.customize.rendajincang.service.IRdjcCustformApplyService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustformApplyDetailDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustformApplyFormDetailDTO;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackConfDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackConf;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.support.common.service.OpenIdTranService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName RdjcCustformApplyServiceImpl
 * @Description 人大金仓自定义申请单实现类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/10/12
 **/
@Service
@Slf4j
public class RdjcCustformApplyServiceImpl implements IRdjcCustformApplyService {

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;
    @Autowired
    private AbstractCustformApplyServiceImpl custformApplyService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ThirdCallbackConfDao thirdCallbackConfDao;
    @Autowired
    private OpenIdTranService openIdTranService;

    @Override
    public void pushExpiredData(HttpServletRequest request, String companyId) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        CustformApplyRequestDTO custformApplyRequestDTO = JsonUtils.toObj(requestBody, CustformApplyRequestDTO.class);
        log.info("自定义申请单推送开始,公司:{},custformApplyRequestDTO:{}", companyId, custformApplyRequestDTO);
        long start = System.currentTimeMillis();
        //查询推送配置
        OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, RdjcConstant.PUSH_CUSTFORM_APPLY);
        //已过期状态单据推送（strValue1为1且过期状态为已过期时推送）
        if (openMsgSetup != null && !StringUtils.isBlank(openMsgSetup.getStrVal1()) && RdjcConstant.IS_PUSH.equals(openMsgSetup.getStrVal1()) && RdjcConstant.STATE_EXPIRE == custformApplyRequestDTO.getApplyState()) {
            //查询企业权限信息
            ThirdCallbackConf callbackConf = thirdCallbackConfDao.queryByCompanyIdAndCallBackType(companyId, CallbackType.APPLY_CUSTOM_BEFOREHAND_REVERSE.getType());
            if (ObjectUtils.isEmpty(callbackConf) || StringUtils.isBlank(callbackConf.getJsonParam())) {
                log.info("查询企业配置失败,公司:{}", companyId);
                throw new OpenApiCustomizeException(SupportRespCode.COMPANY_SETTING_NOT_EXIST, "查询企业配置失败,公司:{}", companyId);
            }
            //查询自定义申请单明细
            CustformApplyFormDetailDTO custformApplyDetail = custformApplyService.getCustformApplyDetail(custformApplyRequestDTO.getApplyId(), companyId);
            if (ObjectUtils.isEmpty(custformApplyDetail)) {
                log.info("查询自定义申请单详情失败,公司:{},申请单号:{}", companyId, custformApplyRequestDTO.getApplyId());
                throw new OpenApiCustomizeException(SupportRespCode.GET_APPLY_DETAIL_FAILED, "查询自定义申请单详情失败,公司:{},审批单号:{}", companyId, custformApplyRequestDTO.getApplyId());
            }
            //参数封装
            Map<String, String> jsonMap = JsonUtils.toObj(callbackConf.getJsonParam(), Map.class);
            RdjcCustformApplyBillDTO applyBillDTO = buildApplyDetail(custformApplyDetail, jsonMap);
            //推送申请单到人大金仓
            pushData(applyBillDTO, jsonMap);
        } else {
            log.info("自定义申请单无需推送,公司:{},申请单id:{}", companyId, custformApplyRequestDTO.getApplyId());
        }
        long end = System.currentTimeMillis();
        log.info("公司:{},自定义申请单推送结束，用时{}分钟{}秒...", companyId, (end - start) / 60000L, (end - start) % 60000L / 1000L);
    }

    /**
     * 参数封装
     *
     * @param applyFormDetailDTO 自定义申请单详情
     * @param jsonMap            配置参数
     * @return applyBillDTO 人大金仓自定义申请单表单参数
     */
    private RdjcCustformApplyBillDTO buildApplyDetail(CustformApplyFormDetailDTO applyFormDetailDTO, Map<String, String> jsonMap) {
        RdjcCustformApplyBillDTO applyBillDTO = new RdjcCustformApplyBillDTO();
        if (applyFormDetailDTO != null && applyFormDetailDTO.getFormInfo() != null && applyFormDetailDTO.getObjectData() != null) {
            applyBillDTO.setApplyId(applyFormDetailDTO.getApply().getApplyId());
            //三方人员id
            String thirdEmployeeId = commonService.getThirdEmployeeId(applyFormDetailDTO.getFormInfo().getCompanyId(), applyFormDetailDTO.getApply().getEmployeeId());
            applyBillDTO.setThirdEmployeeId(StringUtils.isBlank(thirdEmployeeId) ? "" : thirdEmployeeId);
            //三方部门id
            Map<String, Object> dataMap = (Map) applyFormDetailDTO.getObjectData();
            List<Map<String, Object>> deptOrProjectList = (List) dataMap.get("dept_or_project");
            if (CollectionUtils.isNotBlank(deptOrProjectList)) {
                Map<String, Object> deptOrProjectMap = deptOrProjectList.get(0);
                if (RdjcConstant.COST_TYPE_DEPT.equals(StringUtils.obj2str(deptOrProjectMap.get("type")))) {
                    String thirdDeptId = openIdTranService.fbIdToThirdId(applyFormDetailDTO.getFormInfo().getCompanyId(), (String) deptOrProjectMap.get("id"), IdBusinessTypeEnums.ORG.getKey());
                    applyBillDTO.setThirdDepartmentId(StringUtil.isBlank(thirdDeptId) ? "" : thirdDeptId);
                }
            }
            //交通方式
            List<Map<String, Object>> transportationList = (List) dataMap.get(jsonMap.get("transportationKey"));
            if (CollectionUtils.isNotBlank(transportationList)) {
                StringBuilder transportation = new StringBuilder();
                for (Map<String, Object> trans : transportationList) {
                    transportation.append(trans.get("value")).append("、");
                }
                transportation.deleteCharAt(transportation.length() - 1);
                applyBillDTO.setTransportation(transportation.toString());
            }
            //出差事由
            Map<String, Object> appReason = (Map<String, Object>) dataMap.get("app_reason");
            if (!ObjectUtils.isEmpty(appReason)) {
                applyBillDTO.setAppReason((String) appReason.get("value"));
            }
            //补充说明
            String appReasonAddDesc = (String) dataMap.get("app_reason_add_desc");
            if (!ObjectUtils.isEmpty(appReasonAddDesc)) {
                applyBillDTO.setAppReasonAddDesc(appReasonAddDesc);
            }
            //申请项
            List<Map<String, Object>> tripMapList = (List<Map<String, Object>>) dataMap.get("app_trip_list");
            List<RdjcCustformApplyBillDTO.RdjcCustformApplyDetailDTO> applyDetailDTOList = Lists.newArrayList();
            if (CollectionUtils.isNotBlank(tripMapList)) {
                //行程信息
                List<CustformApplyDetailDTO.Trip> tripList = tripMapList.stream().map(RdjcCustformApplyServiceImpl::convertTrip).collect(Collectors.toList());
                if (CollectionUtils.isNotBlank(tripList)) {
                    for (CustformApplyDetailDTO.Trip trip : tripList) {
                        RdjcCustformApplyBillDTO.RdjcCustformApplyDetailDTO custformApplyDetailDTO = new RdjcCustformApplyBillDTO.RdjcCustformApplyDetailDTO();
                        if (StringUtils.isNotBlank(trip.getStartTime()) && StringUtils.isNotBlank(trip.getEndTime())) {
                            custformApplyDetailDTO.setBusinessTime(trip.getStartTime() + "~" + trip.getEndTime());
                        }
                        List<CustformApplyDetailDTO.City> startCities = trip.getStartCities();
                        if (CollectionUtils.isNotBlank(startCities)) {
                            StringBuilder cityBuilder = new StringBuilder();
                            for (CustformApplyDetailDTO.City city : startCities) {
                                cityBuilder.append(city.getValue()).append(" ");
                            }
                            cityBuilder.deleteCharAt(cityBuilder.length() - 1);
                            custformApplyDetailDTO.setBusinessCity(cityBuilder.toString());
                        }
                        applyDetailDTOList.add(custformApplyDetailDTO);
                    }
                }
            }
            applyBillDTO.setApplyDetailDTOList(applyDetailDTOList);
            return applyBillDTO;
        } else {
            throw new OpenApiCustomizeException(-9999, "自定义申请单明细为空");
        }
    }

    /**
     * 推送过期自定义申请单
     *
     * @param applyBillDTO 自定义申请单表单参数
     * @param jsonMap      接口url等信息
     */
    public void pushData(RdjcCustformApplyBillDTO applyBillDTO, Map<String, String> jsonMap) {
        //域名
        String host = jsonMap.get("host");
        //获取token
        String token = RestHttpUtils.get(host + jsonMap.get("tokenUrl"), null, Maps.newHashMap());
        if (StringUtils.isBlank(token)) {
            throw new OpenApiCustomizeException(-9999, "获取到的token为空");
        }

        //表单明细
        StringBuilder businessList = new StringBuilder();
        if (CollectionUtils.isNotBlank(applyBillDTO.getApplyDetailDTOList())) {
            for (RdjcCustformApplyBillDTO.RdjcCustformApplyDetailDTO detailDTO : applyBillDTO.getApplyDetailDTOList()) {
                businessList.append("<row>" +
                    "<column name=\"出差明细-城市\">" +
                    "<value>" + detailDTO.getBusinessCity() + "</value>" +
                    "</column>" +
                    "<column name=\"出差明细-时间\">" +
                    "<value>" + detailDTO.getBusinessTime() + "</value>" +
                    "</column>" +
                    "</row>");
            }
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("token", token);
        Map<String, String> json = Maps.newHashMap();
        json.put("loginName", jsonMap.get("loginName"));
        json.put("dataXml", "<forms version=\"2.1\">" +
            "<formExport>" +
            "<summary id=\"\" name=\"\"/>" +
            "<definitions></definitions>" +
            "<values>" +
            "<column name=\"单据编号\">" +
            "<value>" + applyBillDTO.getApplyId() + "</value>" +
            "</column>" +
            "<column name=\"申请人\">" +
            "<value>" + applyBillDTO.getThirdEmployeeId() + "</value>" +
            "</column>" +
            "<column name=\"申请部门\">" +
            "<value>" + applyBillDTO.getThirdDepartmentId() + "</value>" +
            "</column>" +
            "<column name=\"出差事由\">" +
            "<value>" + applyBillDTO.getAppReason() + "</value>" +
            "</column>" +
            "<column name=\"补充说明\">" +
            "<value>" + applyBillDTO.getAppReasonAddDesc() + "</value>" +
            "</column>" +
            "<column name=\"交通方式\">" +
            "<value>" + applyBillDTO.getTransportation() + "</value>" +
            "</column>" +
            "</values>" +
            "<subForms>" +
            "<subForm>" +
            "<definitions></definitions>" +
            "<values>" + businessList + "</values>" +
            "</subForm>" +
            "</subForms>" +
            "</formExport>" +
            "</forms>");
        String result = RestHttpUtils.postJson(host + jsonMap.get("pushApplyUrl"), httpHeaders, JsonUtils.toJson(json));
        if (!ObjectUtils.nullSafeEquals(RdjcConstant.IS_SUCCESS, result)) {
            log.info("自定义申请单推送人大金仓返回失败,单号:{},,result:{}", applyBillDTO.getApplyId(), result);
            throw new OpenApiCustomizeException(-9999, "自定义申请单推送人大金仓返回失败");
        }
    }

    /**
     * 转换行程信息
     *
     * @param tripMap 行程信息
     * @return detailTrip 转换好的行程信息
     */
    private static CustformApplyDetailDTO.Trip convertTrip(Map<String, Object> tripMap) {
        CustformApplyFormDetailDTO.Trip formTrip = JsonUtils.toObj(JsonUtils.toJson(tripMap), CustformApplyFormDetailDTO.Trip.class);
        if (formTrip == null || formTrip.getModuleInfo() == null || formTrip.getObjectData() == null) {
            return null;
        }
        //兼容新发布的startTime和endTime
        compatibilityHandleTime(formTrip);
        CustformApplyDetailDTO.Trip detailTrip = new CustformApplyDetailDTO.Trip();
        detailTrip.setType(formTrip.getModuleInfo().getModuleCategory());
        detailTrip.setAirType(formTrip.getObjectData().getTripType() == null ? null : formTrip.getObjectData().getTripType().getKey());
        detailTrip.setStartTime(Objects.nonNull(formTrip.getObjectData().getStartTime()) ? formTrip.getObjectData().getStartTime().toString() : null);
        detailTrip.setEndTime(Objects.nonNull(formTrip.getObjectData().getEndTime()) ? formTrip.getObjectData().getEndTime().toString() : null);

        CustformApplyFormDetailDTO.TypeObject sourceStartCity = formTrip.getObjectData().getStartCity();
        List<CustformApplyFormDetailDTO.TypeObject> sourceStartCities = formTrip.getObjectData().getStartCities();
        if (sourceStartCity != null) {
            CustformApplyDetailDTO.City startCity = new CustformApplyDetailDTO.City();
            BeanUtils.copyProperties(sourceStartCity, startCity);
            List<CustformApplyDetailDTO.City> startCities = new ArrayList<>();
            startCities.add(startCity);
            detailTrip.setStartCities(startCities);
        } else if (CollectionUtils.isNotBlank(sourceStartCities)) {
            //用车场景，startCity是list
            detailTrip.setStartCities(sourceStartCities.stream().map(city -> {
                CustformApplyDetailDTO.City targetCity = new CustformApplyDetailDTO.City();
                BeanUtils.copyProperties(city, targetCity);
                return targetCity;
            })
                .collect(Collectors.toList()));
        }


        CustformApplyFormDetailDTO.TypeObject sourceArrivalCity = formTrip.getObjectData().getArrivalCity();
        if (sourceArrivalCity != null) {
            CustformApplyDetailDTO.City arrivalCity = new CustformApplyDetailDTO.City();
            BeanUtils.copyProperties(sourceArrivalCity, arrivalCity);
            detailTrip.setArrivalCity(arrivalCity);
        }

        detailTrip.setEstimatedAmount(formTrip.getObjectData().getEstimatedAmount());

        CustformApplyFormDetailDTO.TimeDuring sourceTimeDuring = formTrip.getObjectData().getTimeQuantum();
        if (sourceTimeDuring != null) {
            CustformApplyDetailDTO.TimeDuring timeDuring = new CustformApplyDetailDTO.TimeDuring();
            BeanUtils.copyProperties(sourceTimeDuring, timeDuring);
            detailTrip.setTimeDuring(timeDuring);
        }

        if (formTrip.getObjectData().getAddress() != null) {
            detailTrip.setUserAddress(JsonUtils.toObj(formTrip.getObjectData().getAddress(), CustformApplyDetailDTO.UserAddress.class));
        }

        CustformApplyDetailDTO.CountLimit countLimitInfo = JsonUtils.toObj(formTrip.getObjectData().getPersonCountLimit(), CustformApplyDetailDTO.CountLimit.class);
        if (countLimitInfo != null) {
            detailTrip.setUseCount(countLimitInfo.getTimesLimit());
        }
        return detailTrip;

    }

    /**
     * 兼容日期格式处理
     *
     * @param formTrip
     */
    private static void compatibilityHandleTime(CustformApplyFormDetailDTO.Trip formTrip) {
        Object startTime = formTrip.getObjectData().getStartTime();
        Object endTime = formTrip.getObjectData().getEndTime();
        try {
            CustformApplyFormDetailDTO.TimeItem startTimeItem = JsonUtils.toObj(JsonUtils.toJson(startTime), CustformApplyFormDetailDTO.TimeItem.class);
            if (Objects.nonNull(startTimeItem)) {
                formTrip.getObjectData().setStartTime(startTimeItem.getDStartTime());
                formTrip.getObjectData().setEndTime(startTimeItem.getDEndTime());
            }
        } catch (Exception e) {
            //ignore NullPointException and FormatException
        }
        try {
            CustformApplyFormDetailDTO.TimeItem endTimeItem = JsonUtils.toObj(JsonUtils.toJson(endTime), CustformApplyFormDetailDTO.TimeItem.class);
            if (Objects.nonNull(endTimeItem)) {
                formTrip.getObjectData().setEndTime(endTimeItem.getDEndTime());
            }
        } catch (Exception e) {
            //ignore and FormatException
        }
    }
}
