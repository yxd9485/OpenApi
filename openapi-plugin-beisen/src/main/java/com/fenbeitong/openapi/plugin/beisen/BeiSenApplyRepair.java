package com.fenbeitong.openapi.plugin.beisen;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenApplyListDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: Test</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/7/1 8:20 PM
 */
public class BeiSenApplyRepair {

    public static void main(String[] args) {
        String json = "{\n" +
                "    \"companyId\":\"5f12aa2193c6d627858c10b4\",\n" +
                "    \"tenantId\":\"108714\",\n" +
                "    \"appId\":\"909\",\n" +
                "    \"secret\":\"77c3e1f73ba1433fbe14b02133104b2c\",\n" +
                "    \"grantType\":\"client_credentials\",\n" +
                "    \"mine\":\"6\",\n" +
                "    \"startCityCarFlag\":true,\n" +
                "    \"typeList\":[\n" +
                "        \"0\",\"1\",\"5\",\"6\"\n" +
                "    ],\n" +
                "     \"tripType\" : \"1\"\n" +
                "}";
        BeisenParamConfig beisenParamConfig = com.luastar.swift.base.json.JsonUtils.toObj(json, BeisenParamConfig.class);
        String json1 = "{\n" +
                "\t\t\t\t\"StartDateTimePeriod\": 1,\n" +
                "\t\t\t\t\"StaffId\": 143955450,\n" +
                "\t\t\t\t\"CardNumber\": null,\n" +
                "\t\t\t\t\"ApplyUser\": \"尹建强\",\n" +
                "\t\t\t\t\"DepartmentId\": null,\n" +
                "\t\t\t\t\"DocumentType\": \"申请\",\n" +
                "\t\t\t\t\"BusinessMarking\": 1,\n" +
                "\t\t\t\t\"CreatedTime\": \"2021-06-30 20:08:05\",\n" +
                "\t\t\t\t\"SerialNumber\": \"C202106300000301\",\n" +
                "\t\t\t\t\"extxmmc_108714_1925595422\": \"2019年广西柳州市公安局智慧公安人脸识别项目\",\n" +
                "\t\t\t\t\"BusinessDetailsSync\": [{\n" +
                "\t\t\t\t\t\"StartDateTimePeriod\": 1,\n" +
                "\t\t\t\t\t\"StaffId\": 143955450,\n" +
                "\t\t\t\t\t\"CardNumber\": null,\n" +
                "\t\t\t\t\t\"CreatedTime\": \"2021-06-30 20:08:05\",\n" +
                "\t\t\t\t\t\"BusinessMarking\": 1,\n" +
                "\t\t\t\t\t\"BusinessVehicle\": \"1\",\n" +
                "\t\t\t\t\t\"SerialNumber\": null,\n" +
                "\t\t\t\t\t\"Address\": null,\n" +
                "\t\t\t\t\t\"DurationDisplay\": \"2天\",\n" +
                "\t\t\t\t\t\"Remark\": null,\n" +
                "\t\t\t\t\t\"StaffEmail\": \"yin.jianqiang@intellif.com\",\n" +
                "\t\t\t\t\t\"StartDateTime\": \"2021-07-01 00:00:00\",\n" +
                "\t\t\t\t\t\"DeparturePlace\": \"南宁\",\n" +
                "\t\t\t\t\t\"DayValOfDuration\": 2,\n" +
                "\t\t\t\t\t\"Destination\": \"柳州\",\n" +
                "\t\t\t\t\t\"BusinessDuration\": 0,\n" +
                "\t\t\t\t\t\"StopDateTime\": \"2021-07-02 00:00:00\",\n" +
                "\t\t\t\t\t\"JobNumber\": \"1220\",\n" +
                "\t\t\t\t\t\"ModifiedTime\": \"2021-06-30 20:52:05\",\n" +
                "\t\t\t\t\t\"ApproStatus\": \"通过\",\n" +
                "\t\t\t\t\t\"StopDateTimePeriod\": 2\n" +
                "\t\t\t\t}],\n" +
                "\t\t\t\t\"ReservationUser\": null,\n" +
                "\t\t\t\t\"DurationDisplay\": \"2天\",\n" +
                "\t\t\t\t\"Reason\": \"柳州人像大比武保障、人体人体归档确认\",\n" +
                "\t\t\t\t\"StdOrganizationCode\": \"7004001\",\n" +
                "\t\t\t\t\"StaffEmail\": \"yin.jianqiang@intellif.com\",\n" +
                "\t\t\t\t\"StartDateTime\": \"2021-07-01 00:00:00\",\n" +
                "\t\t\t\t\"ApplyTime\": \"2021-06-30 20:08:05\",\n" +
                "\t\t\t\t\"ApproveStatus\": \"通过\",\n" +
                "\t\t\t\t\"extxmjl_108714_1587794709\": 128915438,\n" +
                "\t\t\t\t\"DayValOfDuration\": 2,\n" +
                "\t\t\t\t\"ParentId\": \"798d95d0-cc8e-49c0-96b2-85a81c728444\",\n" +
                "\t\t\t\t\"BusinessDuration\": 0,\n" +
                "\t\t\t\t\"OId\": \"4bf8d46f-8735-418d-9d3f-98aec88f9cfa\",\n" +
                "\t\t\t\t\"StdOrganization\": \"广西\",\n" +
                "\t\t\t\t\"ObjectId\": \"4bf8d46f-8735-418d-9d3f-98aec88f9cfa\",\n" +
                "\t\t\t\t\"StopDateTime\": \"2021-07-02 00:00:00\",\n" +
                "\t\t\t\t\"JobNumber\": \"1220\",\n" +
                "\t\t\t\t\"ModifiedTime\": \"2021-06-30 20:52:05\",\n" +
                "\t\t\t\t\"extxmbm_108714_1642207214\": \"101S201912204\",\n" +
                "\t\t\t\t\"extcclb_108714_510803521\": \"1\",\n" +
                "\t\t\t\t\"StopDateTimePeriod\": 2\n" +
                "\t\t\t}";
        BeisenApplyListDTO.BusinessList a = com.luastar.swift.base.json.JsonUtils.toObj(json1, BeisenApplyListDTO.BusinessList.class);
        Map<String, String> carResult = new HashMap<>();
        List<BeisenApplyListDTO.BusinessDetailsSync> businessDetailsSync = a.getBusinessDetailsSync();
        List<BeisenApplyListDTO.BusinessDetailsSync> collects = businessDetailsSync.stream().filter(e -> beisenParamConfig.getTypeList().contains(e.getBusinessVehicle())).collect(Collectors.toList());
        List<BeisenApplyListDTO.BusinessDetailsSync> resultList = new ArrayList<>();
        for (int i = 0; i < collects.size(); i++) {
            BeisenApplyListDTO.BusinessDetailsSync e = collects.get(i);
            e.setTripType(beisenParamConfig.getTripType());
            resultList.add(e);
            if (beisenParamConfig.getTypeList().contains("5")) {//酒店
                BeisenApplyListDTO.BusinessDetailsSync businessDetailsSyncN = new BeisenApplyListDTO.BusinessDetailsSync();
                BeanUtils.copyProperties(e, businessDetailsSyncN);
                businessDetailsSyncN.setBusinessVehicle("5");
                resultList.add(businessDetailsSyncN);
            }
            if (beisenParamConfig.getTypeList().contains("6")) {
                BeisenApplyListDTO.BusinessDetailsSync businessDetailsSyncDesCar = new BeisenApplyListDTO.BusinessDetailsSync();
                BeanUtils.copyProperties(e, businessDetailsSyncDesCar);
                businessDetailsSyncDesCar.setBusinessVehicle("6");
                if (carResult.containsKey(businessDetailsSyncDesCar.getDestination()) &&
                        carResult.get(businessDetailsSyncDesCar.getDestination()).equals(DateUtils.toStr(businessDetailsSyncDesCar.getStartDateTime(), "yyyyMMdd").concat(DateUtils.toStr(businessDetailsSyncDesCar.getStopDateTime(), "yyyyMMdd")))) {
                } else {
                    carResult.put(businessDetailsSyncDesCar.getDestination(), DateUtils.toStr(businessDetailsSyncDesCar.getStartDateTime(), "yyyyMMdd").concat(DateUtils.toStr(businessDetailsSyncDesCar.getStopDateTime(), "yyyyMMdd")));
                    resultList.add(businessDetailsSyncDesCar);
                }
                if (beisenParamConfig.getStartCityCarFlag() != null && beisenParamConfig.getStartCityCarFlag()) {
                    BeisenApplyListDTO.BusinessDetailsSync businessDetailsSyncStartCar = new BeisenApplyListDTO.BusinessDetailsSync();
                    BeanUtils.copyProperties(e, businessDetailsSyncStartCar);
                    businessDetailsSyncStartCar.setDeparturePlace(e.getDestination());
                    businessDetailsSyncStartCar.setDestination(e.getDeparturePlace());
                    businessDetailsSyncStartCar.setBusinessVehicle("6");
                    if (carResult.containsKey(businessDetailsSyncStartCar.getDestination()) &&
                            carResult.get(businessDetailsSyncStartCar.getDestination()).equals(DateUtils.toStr(businessDetailsSyncStartCar.getStartDateTime(), "yyyyMMdd").concat(DateUtils.toStr(businessDetailsSyncStartCar.getStopDateTime(), "yyyyMMdd")))) {
                    } else {
                        carResult.put(businessDetailsSyncStartCar.getDestination(), DateUtils.toStr(businessDetailsSyncStartCar.getStartDateTime(), "yyyyMMdd").concat(DateUtils.toStr(businessDetailsSyncStartCar.getStopDateTime(), "yyyyMMdd")));
                        resultList.add(businessDetailsSyncStartCar);
                    }
                }
            }
        }
        a.setBusinessDetailsSync(resultList);
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", TaskType.BEISEN_APPROVAL_EVENT_CREATE.getKey());
        eventMsg.put("CorpId", beisenParamConfig.getTenantId());
        eventMsg.put("TimeStamp", System.currentTimeMillis());
        eventMsg.put("DataId", a.getObjectId());
        eventMsg.put("DataContent", JsonUtils.toJson(a));
        System.out.println();
    }
}
