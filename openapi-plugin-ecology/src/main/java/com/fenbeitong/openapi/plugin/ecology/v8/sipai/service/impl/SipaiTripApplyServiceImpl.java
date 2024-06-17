package com.fenbeitong.openapi.plugin.ecology.v8.sipai.service.impl;

import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.CertDTO;
import org.apache.dubbo.config.annotation.DubboReference;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto.SipaiTripApplyDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto.SipaiTripApplyDetailDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.TripApproveDetail;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTripApplyService;
import com.fenbeitong.openapi.plugin.support.citycode.dto.CityBaseInfo;
import com.fenbeitong.openapi.plugin.support.citycode.service.CityCodeService;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeUpdateDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportUpdateEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.util.EmployeeUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: SipaiTripApplyServiceImpl</p>
 * <p>Description: 思派行程审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/7 10:57 AM
 */
@ServiceAspect
@Service
public class SipaiTripApplyServiceImpl extends AbstractTripApplyService {

    @Value("${host.appgate}")
    private String appgateUrl;

    @Autowired
    private SipaiEmployeeServiceImpl employeeService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Autowired
    private CityCodeService cityCodeService;

    @Autowired
    private OpenEcologyWorkflowDao workflowDao;

    @Autowired
    private RestHttpUtils httpUtils;

    @Autowired
    private EmployeeUtils employeeUtils;

    public void createTripApply(String companyId, OpenEcologyWorkflow workflow, SipaiTripApplyDTO sipaiTripApplyDto, List<SipaiTripApplyDetailDTO> tripDetailList, String transportType) {
        //更新身份证号
        String idCard = updateIdCardNum(companyId, workflow);
        if (!IdCardUtils.isIDCard(idCard)) {
            OpenEcologyWorkflow updateWf = new OpenEcologyWorkflow();
            updateWf.setId(workflow.getId());
            updateWf.setState(1);
            Map<String, Object> extInfo = Maps.newLinkedHashMap();
            extInfo.put("error_info", "身份证号为空或者错误:" + StringUtils.obj2str(idCard));
            updateWf.setExtInfo(JsonUtils.toJson(extInfo));
            workflowDao.updateById(updateWf);
            return;
        }
        //用户token
        String token = employeeService.getEmployeeFbToken(companyId, workflow.getEmployeeId(), "0");
        //出发时间配置 0-精确时间（天） 1-范围时间
        int applyDepartureDate = queryApplyDepartureDate(token);
        //按同行人分组生成审批单
        Map<String, List<SipaiTripApplyDetailDTO>> tripDetailMap = tripDetailList.stream().collect(Collectors.groupingBy(SipaiTripApplyDetailDTO::getDetailCompanion));
        //申请单信息
        Map<String, TripApproveCreateReqDTO> applyMap = Maps.newLinkedHashMap();
        AtomicInteger index = new AtomicInteger(1000);
        tripDetailMap.keySet().forEach(guest -> {
            List<SipaiTripApplyDetailDTO> tripApplyDetailList = tripDetailMap.get(guest);
            TripApproveCreateReqDTO createReq = new TripApproveCreateReqDTO();
            TripApproveApply tripApproveApply = new TripApproveApply();
            tripApproveApply.setBudget(0);
            tripApproveApply.setThirdId(workflow.getRequestId() + "_" + index.getAndIncrement());
            tripApproveApply.setThirdRemark(ObjectUtils.isEmpty(sipaiTripApplyDto.getCcsy()) ? "无" : sipaiTripApplyDto.getCcsy());
            tripApproveApply.setApplyReasonDesc(sipaiTripApplyDto.getCcsy());
            createReq.setApply(tripApproveApply);
            createReq.setGuestList(tripApplyDetailList.get(0).getGuestList());
            List<TripApproveDetail> totalTrip = Lists.newArrayList();
            tripApplyDetailList.forEach(trip -> {
                List<TripApproveDetail> tripList = getTripList(createReq, applyDepartureDate, transportType, trip);
                totalTrip.addAll(tripList);
            });
            createReq.setTripList(totalTrip);
            createReq.setUncheckTrip(true);
            try {
                CreateApplyRespDTO tripApprove = createTripApprove(token, createReq);
                applyMap.put(tripApprove.getId(), createReq);
            } catch (Exception e) {
            }
        });
        updateWorkFlow(workflow, applyMap, transportType);
    }

    @SuppressWarnings("all")
    private String updateIdCardNum(String companyId, OpenEcologyWorkflow workflow) {
        EmployeeContract employeeContract = employeeUtils.queryEmployees(Lists.newArrayList(workflow.getEmployeeId()), companyId).get(0);
        if (!ObjectUtils.isEmpty(employeeContract.getId_number())) {
            return employeeContract.getId_number();
        }
        String detailForm3 = workflow.getDetailForm3();
        List data = JsonUtils.toObj(detailForm3, List.class);
        if (ObjectUtils.isEmpty(data)) {
            return null;
        }
        Object filedData = data.stream().map(rowData -> {
            List filedList = (List) rowData;
            return filedList.stream().collect(Collectors.toMap(f -> (String) ((Map) f).get("field_name"), Function.identity()));
        }).collect(Collectors.toList());
        //手机号
        String phoneNum = employeeContract.getPhone_num();
        Map matchData = ((List<Map>) filedData).stream()
                .filter(rowMap -> ((Map) rowMap.get("sjhm")) != null && ((Map) rowMap.get("sjhm")).get("field_value").equals(phoneNum)
                        && ((Map) rowMap.get("sfzhm")) != null && ((Map) rowMap.get("sfzhm")).get("field_value") != null)
                .findFirst().orElse(null);
        if (ObjectUtils.isEmpty(matchData)) {
            return null;
        }
        //身份证号码
        Map sfzhmField = (Map) matchData.get("sfzhm");
        String sfzhm = sfzhmField == null ? null : (String) sfzhmField.get("field_value");
        if (IdCardUtils.isIDCard(sfzhm)) {
            SupportUpdateEmployeeReqDTO updateEmployeeReq = new SupportUpdateEmployeeReqDTO();
            updateEmployeeReq.setCompanyId(companyId);
            SupportEmployeeUpdateDTO employeeUpdate = new SupportEmployeeUpdateDTO();
            employeeUpdate.setUpdateFlag(false);
            employeeUpdate.setThirdEmployeeId(employeeContract.getThird_employee_id());
            employeeUpdate.setThirdOrgUnitId(employeeContract.getThird_org_id());
            CertDTO cert = new CertDTO();
            cert.setCertNo(sfzhm);
            cert.setCertType(1);
            employeeUpdate.setCertList(Lists.newArrayList(cert));
            updateEmployeeReq.setEmployeeList(Lists.newArrayList(employeeUpdate));
            updateEmployeeReq.setOperatorId(employeeService.superAdmin(companyId));
            employeeService.updateUser(updateEmployeeReq);
        }
        return sfzhm;
    }

    private void updateWorkFlow(OpenEcologyWorkflow workflow, Map<String, TripApproveCreateReqDTO> applyMap, String transportType) {
        if (!ObjectUtils.isEmpty(applyMap)) {
            OpenEcologyWorkflow updateWf = new OpenEcologyWorkflow();
            updateWf.setId(workflow.getId());
            updateWf.setState(1);
            Map<String, Object> extInfo = Maps.newLinkedHashMap();
            extInfo.put("apply_info", applyMap);
            extInfo.put("type", 1);
            extInfo.put("type_name", "差旅审批");
            extInfo.put("transport", "0".equals(transportType) ? "飞机" : "火车");
            updateWf.setExtInfo(JsonUtils.toJson(extInfo));
            workflowDao.updateById(updateWf);
        }
    }

    private List<TripApproveDetail> getTripList(TripApproveCreateReqDTO createReq, int applyDepartureDate, String transportType, SipaiTripApplyDetailDTO tripApplyDetail) {
        Date startDate = DateUtils.toDate(tripApplyDetail.getDetailFromdate());
        Date endDate = DateUtils.toDate(tripApplyDetail.getDetailToDate());
        //飞机
        if ("0".equals(transportType)) {
            return getAirTripList(startDate, endDate, applyDepartureDate, tripApplyDetail);
        }
        //火车
        else if ("1".equals(transportType)) {
            return getTrainTripList(startDate, endDate, applyDepartureDate, tripApplyDetail);
        }
        return Lists.newArrayList();
//        //酒店
//        else {
//            return getHotelTripList(startDate, endDate);
//        }
    }

//    private List<TripApproveDetail> getHotelTripList(Date startDate, Date endDate) {
//        TripApproveDetail hotel = new TripApproveDetail();
//        hotel.setType(11);
//        hotel.setStartTime(startDate);
//        hotel.setEndTime(endDate);
//        return Lists.newArrayList(hotel);
//    }

    private List<TripApproveDetail> getTrainTripList(Date startDate, Date endDate, int applyDepartureDate, SipaiTripApplyDetailDTO tripApplyDetail) {
        String startCityName = tripApplyDetail.getCfcs();
        String endCityName = tripApplyDetail.getCcdd();
        Map<String, CityBaseInfo> cityMap = cityCodeService.getTrainCode(Lists.newArrayList(startCityName, endCityName));
        String startCityId = cityMap.get(startCityName).getId();
        String endCityId = cityMap.get(endCityName).getId();
        TripApproveDetail trainFrom = new TripApproveDetail();
        trainFrom.setType(15);
        trainFrom.setStartCityId(startCityId);
        trainFrom.setArrivalCityId(endCityId);
        trainFrom.setStartTime(startDate);
        trainFrom.setEndTime(applyDepartureDate == 0 ? startDate : endDate);
        TripApproveDetail trainBack = new TripApproveDetail();
        trainBack.setType(15);
        trainBack.setStartCityId(endCityId);
        trainBack.setArrivalCityId(startCityId);
        trainBack.setStartTime(applyDepartureDate == 0 ? endDate : startDate);
        trainBack.setEndTime(endDate);
        return Lists.newArrayList(trainFrom, trainBack);
    }

    private List<TripApproveDetail> getAirTripList(Date startDate, Date endDate, int applyDepartureDate, SipaiTripApplyDetailDTO tripApplyDetail) {
        String startCityName = tripApplyDetail.getCfcs();
        String endCityName = tripApplyDetail.getCcdd();
        Map<String, CityBaseInfo> cityMap = cityCodeService.getAirCode(Lists.newArrayList(startCityName, endCityName));
        String startCityId = cityMap.get(startCityName).getId();
        String endCityId = cityMap.get(endCityName).getId();
        TripApproveDetail airFrom = new TripApproveDetail();
        airFrom.setType(7);
        airFrom.setStartCityId(startCityId);
        airFrom.setArrivalCityId(endCityId);
        airFrom.setStartTime(startDate);
        airFrom.setEndTime(applyDepartureDate == 0 ? startDate : endDate);
        TripApproveDetail airBack = new TripApproveDetail();
        airBack.setType(7);
        airBack.setStartCityId(endCityId);
        airBack.setArrivalCityId(startCityId);
        airBack.setStartTime(applyDepartureDate == 0 ? endDate : startDate);
        airBack.setEndTime(endDate);
        return Lists.newArrayList(airFrom, airBack);
    }

    private int queryApplyDepartureDate(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(appgateUrl + "/saas/message/setup/apply_config/query", httpHeaders, Maps.newHashMap());
        JSONObject jsonObject = JsonUtils.toObj(result, JSONObject.class);
        return jsonObject == null ? 1 : jsonObject.get("data") == null ? 1 : NumericUtils.obj2int(((Map) jsonObject.get("data")).get("apply_departure_date"));
    }

}
