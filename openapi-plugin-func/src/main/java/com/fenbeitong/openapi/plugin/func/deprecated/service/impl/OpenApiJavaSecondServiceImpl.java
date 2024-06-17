package com.fenbeitong.openapi.plugin.func.deprecated.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.deprecated.common.constant.GlobalResponseCodeOld;
import com.fenbeitong.openapi.plugin.func.deprecated.common.service.OpenJavaDataService;
import com.fenbeitong.openapi.plugin.func.deprecated.service.OpenApiJavaSecondService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.OpenJavaCommonService;
import com.fenbeitong.openapi.plugin.func.deprecated.valid.ValidService;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaCompanyService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaOrderService;
import com.fenbeitong.openapi.plugin.support.deprecat.service.OpenJavaOtherService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * module: 迁移openapi-java项目二期<br/>
 * <p>
 * description: 迁移openapi-java项目二期<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/8/3 17:59
 * @since 1.0
 */
@Service
@ServiceAspect
@Slf4j
public class OpenApiJavaSecondServiceImpl implements OpenApiJavaSecondService {

    @Value("${fe.sign.key}")
    private String signKey;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private OpenJavaCommonService openJavaCommonService;

    @Autowired
    private OpenJavaOrderService openJavaOrderService;

    @Autowired
    private OpenJavaDataService openJavaDataService;

    @Autowired
    private OpenJavaCompanyService openJavaCompanyService;

    @Autowired
    private OpenJavaOtherService openJavaOtherService;

    @Autowired
    private ValidService validService;


    @Override
    public Object queryTrainOrderDetail(HttpServletRequest httpRequest, ApiRequest request) {
        Integer employeeType = request.getEmployeeType();
        String employeeId = request.getEmployeeId();
        String trainOrderDetailData = request.getData();
        log.info("api火车票订单详情查询人员参数: {}, 类型参数: {}", employeeId, employeeType);
        log.info("api火车票获取火车票订单列表请求参数，trainOrderDetailData={}", trainOrderDetailData);
        String companyId = (String) httpRequest.getAttribute("companyId");
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", employeeId);
        param.put("company_id", companyId);
        if (employeeType == 0) {
            param.put("appType", 0);
        } else {
            param.put("appType", 1);
        }
        AuthDefinition authInfoByAppId = authDefinitionDao.getAuthInfoByAppId(companyId);
        long timestamp = System.currentTimeMillis();
        // 获取签名
        String sign = openJavaCommonService.genSign(timestamp, companyId, signKey);
        if (ObjectUtils.isEmpty(authInfoByAppId)) {
            throw new FinhubException(GlobalResponseCodeOld.TOKEN_INFO_IS_ERROR.getCode(), GlobalResponseCodeOld.TOKEN_INFO_IS_ERROR.getMessage());
        }
        String appType = authInfoByAppId.getAppType();
        if (appType == null) {
            appType = "0";
        }
        param.put("timestamp", timestamp);
        param.put("sign", sign);
        String token = openJavaCommonService.getToken(param);
        if (appType.equals("0")) {
            Map<String, Object> map = JsonUtils.toObj(trainOrderDetailData, new TypeReference<Map<String, Object>>() {
            });
            return getTrainOrderDetail(companyId, token, map);
        } else {
            JSONObject trainOrderDetailJsonData = openJavaCommonService.isJson(trainOrderDetailData);
            String orderno = trainOrderDetailJsonData.getString("orderno");
            String source = trainOrderDetailJsonData.getString("source");
            String supplier = trainOrderDetailJsonData.getString("supplier");
            JSONObject orderJsonObject = new JSONObject();
            orderJsonObject.put("order_id", orderno);
            JSONObject finalResponse = new JSONObject();
            log.info("api火车票获取火车票订单列表请求参数，orderJsonObject={}", orderJsonObject.toJSONString());
            Object orderTrainDetail = null;
            String hlResponse = null;
            try {
                orderTrainDetail = openJavaOrderService.getOrderTrainDetail(orderJsonObject, null);
                hlResponse = JsonUtils.toJson(orderJsonObject);
            } catch (NullPointerException e) {
                finalResponse.put("successcode", "F");
                finalResponse.put("Info", "返回数据为空");
                finalResponse.put("source", source);
                finalResponse.put("supplier", supplier);
                return finalResponse;
            } catch (Exception e) {
                Map<String, Object> map = JsonUtils.toObj(JsonUtils.toJson(orderTrainDetail), new TypeReference<Map<String, Object>>() {
                });
                Object msg = null;
                if (ObjectUtils.isEmpty(map)) {
                    msg = null;
                } else if (!map.containsKey("msg")) {
                    msg = null;
                } else {
                    msg = map.get("msg");
                }
                finalResponse.put("successcode", "F");
                finalResponse.put("Info", msg);
                finalResponse.put("source", source);
                finalResponse.put("supplier", supplier);
                return finalResponse;
            }
            JSONObject dataJsonObject = openJavaCommonService.isJson(hlResponse);
            JSONObject orderBasicInfoJsonObject = dataJsonObject.getJSONObject("order_basic_info");
            JSONObject routeInfoJsonObject = dataJsonObject.getJSONObject("route_info");
            String TrainNo = routeInfoJsonObject.getString("train_code");
            String DepartPort = routeInfoJsonObject.getString("from_station_name");
            String DepartTime = routeInfoJsonObject.getString("start_time");
            String ArrivePort = routeInfoJsonObject.getString("to_station_name");
            String ArriveTime = routeInfoJsonObject.getString("arrive_time");
            JSONObject contactInfoJsonObject = dataJsonObject.getJSONObject("contact_info");
            String contactName = contactInfoJsonObject.getString("contact_name");
            String contactPhone = contactInfoJsonObject.getString("contact_phone");
            JSONArray insuranceInfoJsonArray = dataJsonObject.getJSONArray("insurance_info");
            String orderId = orderBasicInfoJsonObject.getString("order_id");
            Long creatTime = orderBasicInfoJsonObject.getLong("create_time");
            Double totalPrice = orderBasicInfoJsonObject.getDouble("total_price");
            JSONObject orderStatus = orderBasicInfoJsonObject.getJSONObject("order_status");
            Integer orderStatusKey = orderStatus.getInteger("key");
            String orderStatusValue = orderStatus.getString("value");
            JSONArray finalPassengerJsonArray = new JSONArray();
            JSONArray finalTicketJsonArray = new JSONArray();
            JSONArray passerngerJsonArray = dataJsonObject.getJSONArray("passengers_info");
            for (Object passenger : passerngerJsonArray) {
                JSONObject passengerTicketJsonObject = (JSONObject) passenger;
                JSONObject passengerJsonObject = passengerTicketJsonObject.getJSONObject("passenger_info");
                String passengerName = passengerJsonObject.getString("name");
                String mobilePhone = passengerJsonObject.getString("mobile_no");
                String identityNo = passengerJsonObject.getString("identity_no");
                JSONObject identityTypetyJsonObject = passengerJsonObject.getJSONObject("identity_type");
                Integer identityKey = identityTypetyJsonObject.getInteger("key");
                String identityValue = identityTypetyJsonObject.getString("value");
                JSONObject ticketInfoJsonObject = passengerTicketJsonObject.getJSONObject("ticket_info");
                String seatType = ticketInfoJsonObject.getString("seat_type");
                String seatNo = ticketInfoJsonObject.getString("seat_no");
                //需要取出车厢和座位号
                String seatLocation = ticketInfoJsonObject.getString("seat_location");
                String productId = ticketInfoJsonObject.getString("product_id");
                JSONObject ticketStatusJsonObject = ticketInfoJsonObject.getJSONObject("ticket_status");
                Integer ticketStatusKey = ticketStatusJsonObject.getInteger("key");
                String ticketStatusValue = ticketStatusJsonObject.getString("value");
                JSONObject finalPassengerJsonObject = new JSONObject();
                JSONObject finalTicketJsonObject = new JSONObject();
                finalPassengerJsonObject.put("PassengerName", passengerName);
                //乘客类型 TODO 乘客类型，成年和儿童
                finalPassengerJsonObject.put("PassengerType", passengerName);
                if (identityKey == 1) {
                    finalPassengerJsonObject.put("IdCardType", 1);
                }
                if (identityKey == 2) {
                    finalPassengerJsonObject.put("IdCardType", 2);
                }
                if (identityKey == 4) {
                    finalPassengerJsonObject.put("IdCardType", 5);
                }
                if (identityKey == 0) {
                    finalPassengerJsonObject.put("IdCardType", 6);
                }

                finalPassengerJsonObject.put("IdCardNumber", identityNo);
                finalTicketJsonObject.put("DepartPort", DepartPort);
                finalTicketJsonObject.put("DepartTime", DepartTime);
                finalTicketJsonObject.put("ArrivePort", ArrivePort);
                finalTicketJsonObject.put("ArriveTime", ArriveTime);
                finalTicketJsonObject.put("TrainNo", TrainNo);
                finalTicketJsonObject.put("SeatType", seatType);
                finalTicketJsonObject.put("SeatNo", seatLocation);
                finalTicketJsonObject.put("Coach", seatLocation);
                finalTicketJsonObject.put("Price", totalPrice);
                finalTicketJsonObject.put("InsuranceNum", insuranceInfoJsonArray.size());
                finalTicketJsonObject.put("TicketNo", productId);
                finalPassengerJsonArray.add(finalPassengerJsonObject);
                finalTicketJsonArray.add(finalTicketJsonObject);

            }

            finalResponse.put("passengers", finalPassengerJsonArray);
            finalResponse.put("tickets", finalTicketJsonArray);
            // TODO 订单详情中的总数没有
            finalResponse.put("intpagecount", 1);
            finalResponse.put("intrecordcount", 0);
            finalResponse.put("Id", 1);
            finalResponse.put("OrderNumber", orderId);
            finalResponse.put("ContactName", contactName);
            finalResponse.put("ContactMobile", contactPhone);
            finalResponse.put("TotalPayPrice", totalPrice);
            finalResponse.put("PayTime", creatTime);
            if (orderStatusKey == 3101) {
                finalResponse.put("OrderStatus", 8);
            } else if (orderStatusKey == 3100) {
                finalResponse.put("OrderStatus", 1);
            } else if (orderStatusKey == 3801) {
                finalResponse.put("OrderStatus", 6);
            } else if (orderStatusKey == 3202 || orderStatusKey == 3400) {
                finalResponse.put("OrderStatus", 3);
            } else if (orderStatusKey == 3201) {
                finalResponse.put("OrderStatus", 2);
            } else if (orderStatusKey == 3802) {
                finalResponse.put("OrderStatus", 7);
            }
            finalResponse.put("successcode", "T");
            finalResponse.put("source", source);
            finalResponse.put("supplier", supplier);
            return finalResponse;
        }
    }

    @Override
    public Object queryOrderParam(HttpServletRequest httpRequest, ApiRequest request) {
        String companyId = (String) httpRequest.getAttribute("companyId");
        Integer employeeT = request.getEmployeeType();
        String orderParamData = request.getData();
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", request.getEmployeeId());
        param.put("company_id", companyId);
        if (employeeT == 0) {
            param.put("appType", 0);
        } else {
            param.put("appType", 1);
        }
        log.info("根据订单ID查询自定义字段请求数据:orderParam={}", orderParamData);
        JSONObject orderParamJsonObject = openJavaCommonService.isJson(orderParamData);
        String orderId = orderParamJsonObject.getString("order_id");
        String orderParam = openJavaDataService.getOrderParam(orderId);
        return JSONObject.parseArray(orderParam);
    }

    @Override
    public Object queryCompanyRule(HttpServletRequest httpRequest, ApiRequest request) {
        String companyId = (String) httpRequest.getAttribute("companyId");
        Map<String, Object> param = Maps.newHashMap();
        param.put("companyId", companyId);
        log.info("api获取公司规则信息返回数据，apiCompanyRuleData={}", JsonUtils.toJson(openJavaCompanyService.getCompanyRule(param, null)));
        return openJavaCompanyService.getCompanyRule(param, null);
    }

    @Override
    public Object queryCompanyRole(HttpServletRequest httpRequest, ApiRequest request) {
        String data = request.getData();
        Map<String, Object> param = Maps.newHashMap();
        Map<String, Object> map = JsonUtils.toObj(data, new TypeReference<Map<String, Object>>() {
        });
        param.put("company_id", httpRequest.getAttribute("companyId"));
        if (map.containsKey("type") || !ObjectUtils.isEmpty(map.get("type"))) {
            param.put("type", map.get("type"));
        }
        Object companyRole = openJavaCompanyService.getCompanyRole(param, null);
        log.info("api获取公司权限信息返回数据，apiCompanyRoleData={}", companyRole);
        return companyRole;
    }

    @Override
    public Object queryNationality(HttpServletRequest httpRequest) {
        return openJavaOtherService.getNational(null, null);
    }

    @Override
    public Object queryCityCode(HttpServletRequest httpRequest, ApiRequest request) {
        String data = request.getData();
        JSONObject json = openJavaCommonService.isJson(data);
        Integer employeeType = request.getEmployeeType();
        if (ObjectUtils.isEmpty(employeeType)) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        String employeeId = request.getEmployeeId();
        if (!json.containsKey("type") || validService.checkParameterType(json.get("type")) != 1) {
            throw new FinhubException(GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getCode(), GlobalResponseCodeOld.COMMON_DATA_TYPE_DATA_IS_ERROR.getMessage());
        }
        String type = (String) json.get("type");
        String companyId = (String) httpRequest.getAttribute("companyId");
        Map<String, Object> param = Maps.newHashMap();
        param.put("user_id", employeeId);
        param.put("company_id", companyId);
        if (employeeType == 0) {
            param.put("appType", 0);
        } else {
            param.put("appType", 1);
        }
        String token = openJavaCommonService.getToken(param, true);
        Object cityCode = openJavaOtherService.getCityCode(type, token);
        log.info("城市id数据{}",cityCode);
        String numberStr = "6";
        if (type.equals(numberStr)) {
            JSONObject cityListJosnObject = (JSONObject) JSONObject.parse(JsonUtils.toJson(cityCode));
            JSONArray cityListJsonArray = cityListJosnObject.getJSONArray("city_list");
            return buildCityData(cityListJsonArray);
        }
        return cityCode;
    }


    private JSONArray buildCityData(JSONArray cityListJsonArray) {
        if (ObjectUtils.isEmpty(cityListJsonArray)) {
            return cityListJsonArray;
        }
        // 城市常量
        Set<String> cityNames = Sets.newHashSet();
        cityNames.add("南宁市");
        cityNames.add("广州市");
        cityNames.add("海口市");
        cityNames.add("昆明市");
        cityNames.add("贵阳市");
        cityNames.add("拉萨市");
        cityNames.add("西宁市");
        cityNames.add("成都市");
        cityNames.add("乌鲁木齐市");
        cityNames.add("呼和浩特市");
        cityNames.add("太原市");
        cityNames.add("银川市");
        cityNames.add("兰州市");
        cityNames.add("郑州市");
        cityNames.add("武汉市");
        cityNames.add("长沙市");
        cityNames.add("福州市");
        cityNames.add("南昌市");
        cityNames.add("杭州市");
        cityNames.add("南京市");
        cityNames.add("济南市");
        cityNames.add("西安市");
        cityNames.add("石家庄市");
        cityNames.add("沈阳市");
        cityNames.add("长春市");
        cityNames.add("合肥市");
        cityNames.add("哈尔滨市");
        for (int i = 0; i < cityListJsonArray.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            //每个城市列表信息
            JSONObject unitCityList = (JSONObject) cityListJsonArray.get(i);
            if (!unitCityList.containsKey("fullCityName") || ObjectUtils.isEmpty(unitCityList.getString("fullCityName"))) {
                continue;
            }
            String fullCityName = unitCityList.getString("fullCityName");
            String provinceName = fullCityName.split(",")[1];
            String provinceCode = unitCityList.getString("parent_id");
            //海南省
            if (cityNames.contains(unitCityList.getString("city_name"))) {
                jsonObject.put("city_name", provinceName);
                jsonObject.put("city_code", provinceCode);
                cityListJsonArray.add(jsonObject);
            }
        }
        return cityListJsonArray;
    }


    /**
     * 获取火车详情
     *
     * @param companyId
     * @param token
     * @param data
     * @return
     */
    private JSONObject getTrainOrderDetail(String companyId, String token, Map<String,Object> data) {
        Object orderTrainDetail = openJavaOrderService.getOrderTrainDetail(data, token);
        if (ObjectUtils.isEmpty(orderTrainDetail)) {
            throw new FinhubException(GlobalResponseCodeOld.HyperloopServiceError.getCode(), GlobalResponseCodeOld.HyperloopServiceError.getMessage());
        }
        JSONObject dataJsonObject = openJavaCommonService.isJson(JsonUtils.toJson(orderTrainDetail));
        String costAttributionId = (String) dataJsonObject.get("cost_attribution_id");
        Integer costAttributionType = (Integer) dataJsonObject.get("cost_attribution_category");
        Map<String, Object> aMap = Maps.newHashMap();
        aMap.put("company_id", companyId);
        aMap.put("operatorId", "OpenApi");
        aMap.put("cost_attribution_id", costAttributionId);
        aMap.put("type", costAttributionType);
        aMap.put("token", token);
        Map<String, Object> costAttributionInfoMap = openJavaCommonService.getCostAttributionInfo(aMap);
        String thirdCostAttributionId = (String) costAttributionInfoMap.get("third_cost_attribution_id");
        //第三方费用归属ID信息
        dataJsonObject.put("third_cost_attribution_id", thirdCostAttributionId);
        //第三方费用归属ID类型,1:部门，2:项目
        dataJsonObject.put("third_cost_attribution_type", costAttributionType);
        JSONObject orderBasicInfo = dataJsonObject.getJSONObject("order_basic_info");
        String orderOwnerId = orderBasicInfo.getString("employee_id");
        HashMap<String, String> params = Maps.newHashMap();
        params.put("company_id", companyId);
        params.put("order_owner_id", orderOwnerId);
        String thirdEmployeeId = openJavaCommonService.getEmployeeThirdInfoByFbId(params);
        dataJsonObject.put("third_employee_id", StringUtils.isBlank(thirdEmployeeId) ? "" : thirdEmployeeId);
        String fbOrgUnitId = orderBasicInfo.getString("org_unit_id");
        HashMap<String, String> orgParams = Maps.newHashMap();
        orgParams.put("company_id", companyId);
        orgParams.put("org_id", fbOrgUnitId);
        return openJavaCommonService.getOrgThirdInfoByFbId(orgParams, dataJsonObject);
    }


}
