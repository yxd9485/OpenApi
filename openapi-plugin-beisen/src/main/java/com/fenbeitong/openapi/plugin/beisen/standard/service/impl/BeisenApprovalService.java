package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenApplyListDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenOutwardApplyListDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyGuest;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.support.city.constants.CityRelationType;
import com.fenbeitong.openapi.plugin.support.citycode.service.CityCodeService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.*;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ServiceAspect
@Service
@Slf4j
public class BeisenApprovalService {

    @Autowired
    private CityCodeService cityCodeService;
    @Value("${host.appgate}")
    private String appgateHost;
    @Value("${host.usercenter}")
    private String userCenter;

    @Autowired
    private RestHttpUtils httpUtils;

    public List<CommonApplyReqDTO> parseBeisenApprovalForm(BeisenApplyListDTO.BusinessList data, List<BeisenApplyListDTO.BusinessDetailsSync> businessDetailsSync, String token, String companyId) {
        //北森只有0机票与1火车2汽车3轮船4其他           5酒店6用车
        List<BeisenApplyListDTO.BusinessDetailsSync> carListData = businessDetailsSync.stream().filter(e -> "6".equals(e.getBusinessVehicle())).collect(Collectors.toList());
        List<BeisenApplyListDTO.BusinessDetailsSync> tripListData = businessDetailsSync.stream().filter(e -> !"6".equals(e.getBusinessVehicle())).collect(Collectors.toList());
        List<CommonApplyReqDTO> commonApplyReqDTOList = new ArrayList<>();
        int departureDate = queryApplyDepartureDate(token);
        List<String> vehicleTypes = Arrays.asList("0", "1", "5", "6");
        //行程的数据
        if (tripListData != null && tripListData.size() > 0) {
            List<CommonApplyTrip> tripList = new ArrayList();
            CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
            CommonApply commonApply = new CommonApply();
            commonApply.setCompanyId(companyId);
            commonApply.setEmployeeId(data.getStaffId());
            commonApply.setApplyReason(data.getReason());
            commonApply.setThirdRemark(StringUtils.isBlank(data.getReason()) ? "北森三方行程审批" : data.getReason());
            commonApply.setThirdId(data.getObjectId());
            commonApply.setFlowType(4);
            commonApply.setBudget(0);
            commonApplyReqDTO.setApply(commonApply);
            //获取同行人信息
            List<CommonApplyGuest> guests = new ArrayList<>();
            for (BeisenApplyListDTO.BusinessDetailsSync e : tripListData) {
                CommonApplyTrip commonApplyFromTrip = new CommonApplyTrip();
                CommonApplyTrip commonApplyBackTrip = new CommonApplyTrip();
                //具体交通工具类型
                String tripType = null;
                if (e.getBusinessVehicle().equals("0")) { //机票
                    tripType = "7";
                    commonApply.setType(1);
                } else if (e.getBusinessVehicle().equals("1")) { //火车
                    tripType = "15";
                    commonApply.setType(1);
                } else if (e.getBusinessVehicle().equals("5")) { //酒店
                    tripType = "11";
                    commonApply.setType(1);
                } else if (e.getBusinessVehicle().equals("6")) { //用车
                    tripType = "3";
                    commonApply.setType(12);
                }
                // 交通工具
                String vehicleType = e.getBusinessVehicle();
                if (!vehicleTypes.contains(vehicleType)){
                    log.info("过滤当前交通工具类型不属于机酒车火 , 生成酒店 : {}",vehicleType);
                    // 酒店
                    tripType = "11"; int type = 1;
                    buildTripDTO(e,tripType,type,tripList,departureDate);
                    continue;
                }
                commonApplyFromTrip.setType(Integer.valueOf(tripType));
                commonApplyFromTrip.setTripType(1);
                commonApplyFromTrip.setEstimatedAmount(0);
                if ("11".equals(tripType) || "3".equals(tripType)) {
                    commonApplyFromTrip.setStartCityName(e.getDestination());
                    commonApplyFromTrip.setArrivalCityName(e.getDeparturePlace());
                } else {
                    commonApplyFromTrip.setStartCityName(e.getDeparturePlace());
                    commonApplyFromTrip.setArrivalCityName(e.getDestination());
                }
                commonApplyFromTrip.setStartTime(DateUtils.toSimpleStr(e.getStartDateTime(), true));
                commonApplyFromTrip.setEndTime(DateUtils.toSimpleStr(e.getStopDateTime(), true));
                commonApplyFromTrip.setEstimatedAmount(0);


                commonApplyBackTrip.setType(Integer.valueOf(tripType));
                commonApplyBackTrip.setTripType(1);
                commonApplyBackTrip.setEstimatedAmount(0);
                commonApplyBackTrip.setStartCityName(e.getDestination());
                commonApplyBackTrip.setArrivalCityName(e.getDeparturePlace());
                if (departureDate == 0) {
                    commonApplyBackTrip.setStartTime(DateUtils.toSimpleStr(e.getStopDateTime(), true));
                    commonApplyBackTrip.setEndTime(DateUtils.toSimpleStr(e.getStopDateTime(), true));
                } else {
                    commonApplyBackTrip.setStartTime(DateUtils.toSimpleStr(e.getStartDateTime(), true));
                    commonApplyBackTrip.setEndTime(DateUtils.toSimpleStr(e.getStopDateTime(), true));
                }
                commonApplyBackTrip.setEstimatedAmount(0);
                if ("11".equals(tripType) || "3".equals(tripType)) {
                    tripList.add(commonApplyFromTrip);
                } else {
                    tripList.add(commonApplyFromTrip);
                    if (StringUtils.isBlank(e.getTripType()) || "1".equals(e.getTripType())) {
                        tripList.add(commonApplyBackTrip);
                    }
                }
            }
            tripList = tripList.stream().distinct().collect(Collectors.toList());
            commonApplyReqDTO.setTripList(tripList);
            commonApplyReqDTO.setGuestList(guests);
            commonApplyReqDTOList.add(commonApplyReqDTO);
        }
        //用车的数据
        if (carListData != null && carListData.size() > 0) {
            for (int i = 0; i < carListData.size(); i++) {
                BeisenApplyListDTO.BusinessDetailsSync cardata = carListData.get(i);
                List<CommonApplyTrip> carList = new ArrayList();
                CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
                CommonApply commonApply = new CommonApply();
                commonApply.setCompanyId(companyId);
                commonApply.setEmployeeId(data.getStaffId());
                commonApply.setApplyReason(data.getReason());
                commonApply.setThirdRemark(StringUtils.isBlank(data.getReason()) ? "北森三方用车审批" : data.getReason());
                if (carListData.size() > 1) {
                    commonApply.setThirdId(data.getObjectId().concat("&").concat(String.valueOf(i + 1)));
                } else {
                    commonApply.setThirdId(data.getObjectId());
                }
                commonApply.setFlowType(4);
                commonApply.setBudget(0);
                commonApplyReqDTO.setApply(commonApply);
                //获取同行人信息
                List<CommonApplyGuest> guests = new ArrayList<>();
                CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
                //具体交通工具类型
                String tripType = null;
                if (cardata.getBusinessVehicle().equals("0")) {
                    tripType = "7";
                    commonApply.setType(1);
                } else if (cardata.getBusinessVehicle().equals("1")) {
                    tripType = "15";
                    commonApply.setType(1);
                } else if (cardata.getBusinessVehicle().equals("5")) {
                    tripType = "11";
                    commonApply.setType(1);
                } else if (cardata.getBusinessVehicle().equals("6")) {
                    tripType = "3";
                    commonApply.setType(12);
                }
                commonApplyTrip.setType(Integer.valueOf(tripType));
                commonApplyTrip.setTripType(1);
                commonApplyTrip.setEstimatedAmount(0);
                if ("11".equals(tripType) || "3".equals(tripType)) {
                    commonApplyTrip.setStartCityName(cardata.getDestination());
                    commonApplyTrip.setArrivalCityName(cardata.getDeparturePlace());
                } else {
                    commonApplyTrip.setStartCityName(cardata.getDeparturePlace());
                    commonApplyTrip.setArrivalCityName(cardata.getDestination());
                }
                commonApplyTrip.setStartTime(DateUtils.toSimpleStr(cardata.getStartDateTime(), true));
                commonApplyTrip.setEndTime(DateUtils.toSimpleStr(cardata.getStopDateTime(), true));
                commonApplyTrip.setEstimatedAmount(0);
                carList.add(commonApplyTrip);
                commonApplyReqDTO.setTripList(carList);
                commonApplyReqDTO.setGuestList(guests);
                commonApplyReqDTOList.add(commonApplyReqDTO);
            }
        }
        log.info("转换后的北森审批单 : {}",JsonUtils.toJson(commonApplyReqDTOList));
        commonApplyReqDTOList = commonApplyReqDTOList.stream().distinct().collect(Collectors.toList());
        log.info("对用车去重后的北森审批单 : {}",JsonUtils.toJson(commonApplyReqDTOList));
        return commonApplyReqDTOList;
    }

    public void buildTripDTO(BeisenApplyListDTO.BusinessDetailsSync e , String tripType , Integer type , List<CommonApplyTrip> tripList , int departureDate ){
        CommonApplyTrip commonApplyFromTrip = new CommonApplyTrip();
        CommonApplyTrip commonApplyBackTrip = new CommonApplyTrip();
        // 交通工具
        CommonApply commonApply = new CommonApply();
        commonApply.setType(type);
        commonApplyFromTrip.setType(Integer.valueOf(tripType));
        commonApplyFromTrip.setTripType(1);
        commonApplyFromTrip.setEstimatedAmount(0);
        if ("11".equals(tripType) || "3".equals(tripType)) {
            commonApplyFromTrip.setStartCityName(e.getDestination());
            commonApplyFromTrip.setArrivalCityName(e.getDeparturePlace());
        } else {
            commonApplyFromTrip.setStartCityName(e.getDeparturePlace());
            commonApplyFromTrip.setArrivalCityName(e.getDestination());
        }
        commonApplyFromTrip.setStartTime(DateUtils.toSimpleStr(e.getStartDateTime(), true));
        commonApplyFromTrip.setEndTime(DateUtils.toSimpleStr(e.getStopDateTime(), true));
        commonApplyFromTrip.setEstimatedAmount(0);

        commonApplyBackTrip.setType(Integer.valueOf(tripType));
        commonApplyBackTrip.setTripType(1);
        commonApplyBackTrip.setEstimatedAmount(0);
        commonApplyBackTrip.setStartCityName(e.getDestination());
        commonApplyBackTrip.setArrivalCityName(e.getDeparturePlace());
        if (departureDate == 0) {
            commonApplyBackTrip.setStartTime(DateUtils.toSimpleStr(e.getStopDateTime(), true));
            commonApplyBackTrip.setEndTime(DateUtils.toSimpleStr(e.getStopDateTime(), true));
        } else {
            commonApplyBackTrip.setStartTime(DateUtils.toSimpleStr(e.getStartDateTime(), true));
            commonApplyBackTrip.setEndTime(DateUtils.toSimpleStr(e.getStopDateTime(), true));
        }
        commonApplyBackTrip.setEstimatedAmount(0);
        log.info("由非机酒车火生成的 commonApplyFromTrip : {}",JsonUtils.toJson(commonApplyFromTrip));
        if ("11".equals(tripType) || "3".equals(tripType)) {
            tripList.add(commonApplyFromTrip);
        } else {
            tripList.add(commonApplyFromTrip);
            if (StringUtils.isBlank(e.getTripType()) || "1".equals(e.getTripType())) {
                tripList.add(commonApplyBackTrip);
            }
        }
    }

    public List<CommonApplyReqDTO> parseBeisenOutwardApprovalForm(BeisenOutwardApplyListDTO.OutwardInfo data, String token) {
        //北森公出单直接生成用车审批单
        List<CommonApplyReqDTO> commonApplyReqDTOList = new ArrayList<>();
        int departureDate = queryApplyDepartureDate(token);
        //用车的数据
        List<CommonApplyTrip> carList = new ArrayList();
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        CommonApply commonApply = new CommonApply();
        commonApply.setApplyReason(data.getOutwardReason());
        commonApply.setApplyReasonDesc(data.getOutwardReason());
        commonApply.setThirdRemark(StringUtils.isBlank(data.getOutwardReason()) ? "北森三方公出用车审批" : data.getOutwardReason());
        commonApply.setThirdId(data.getOId());
        commonApply.setFlowType(4);
        commonApply.setBudget(0);
        commonApplyReqDTO.setApply(commonApply);
        //获取同行人信息
        List<CommonApplyGuest> guests = new ArrayList<>();
        CommonApplyTrip commonApplyTrip = new CommonApplyTrip();
        //具体交通工具类型
        String tripType = "3";
        commonApply.setType(12);
        commonApplyTrip.setType(Integer.valueOf(tripType));
        commonApplyTrip.setTripType(1);
        commonApplyTrip.setEstimatedAmount(0);
        commonApplyTrip.setStartCityId(data.getCityId());
        commonApplyTrip.setCityRelationType(CityRelationType.BEISEN.getCode());
        commonApplyTrip.setStartTime(DateUtils.toSimpleStr(data.getOutwardStartDateTime(), true));
        commonApplyTrip.setEndTime(DateUtils.toSimpleStr(data.getOutwardStopDateTime(), true));
        commonApplyTrip.setEstimatedAmount(0);
        carList.add(commonApplyTrip);
        commonApplyReqDTO.setTripList(carList);
        commonApplyReqDTO.setGuestList(guests);
        commonApplyReqDTOList.add(commonApplyReqDTO);
        return commonApplyReqDTOList;
    }


    private int queryApplyDepartureDate(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String result = httpUtils.get(appgateHost + "/saas/message/setup/apply_config/query", httpHeaders, Maps.newHashMap());
        Map map = JsonUtils.toObj(result, Map.class);
        return map == null ? 1 : map.get("data") == null ? 1 : NumericUtils.obj2int(((Map) map.get("data")).get("apply_departure_date"));
    }

}
