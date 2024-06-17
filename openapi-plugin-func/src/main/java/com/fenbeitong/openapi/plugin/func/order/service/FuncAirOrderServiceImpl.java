package com.fenbeitong.openapi.plugin.func.order.service;

import com.alibaba.fastjson.JSON;
import com.fenbeitong.openapi.plugin.support.common.service.OpenIdTranService;
import com.fenbeitong.openapi.plugin.support.order.dto.resp.IntlAirDetailResponseDTO;
import com.fenbeitong.openapi.plugin.support.order.dto.resp.common.BaseOrderResDTO;
import com.fenbeitong.saasplus.api.model.dto.apply.ApplyOrderContract;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.common.FuncIdTypeEnums;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.support.util.FinhubAdminTokenUtil;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderService;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.fenbeitong.openapi.plugin.func.order.dto.FuncThirdInfoExpressDTO.ThirdCommonExpress;
import static com.fenbeitong.openapi.plugin.func.order.dto.FuncThirdInfoExpressDTO.ThirdCostExpress;

/**
 * <p>Title: FuncAirOrderServiceImpl</p>
 * <p>Description: 机票订单服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/17 4:15 PM
 */
@SuppressWarnings("all")
@Slf4j
@ServiceAspect
@Service
public class FuncAirOrderServiceImpl extends AbstractOrderService {

    @Value("${host.air_biz}")
    private String airBizHost;

    @Autowired
    private IEtlService etlService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Autowired
    private FuncThirdInfoServiceImpl thirdInfoService;

    @DubboReference(check = false)
    private IApplyOrderService applyOrderService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private OpenIdTranService openIdTranService;



    public static final String THIRD_INFO = "third_info";
    public static final String CACHE = "cache";

    public Object list(AirOrderListReqDTO req) {
        Map data = listAirOrder(req);
        int totalCount = ObjectUtils.isEmpty(data) || ObjectUtils.isEmpty(data.get("data")) ? 0 : (int) MapUtils.getValueByExpress(data, "data:total_count");
        List orderList = ObjectUtils.isEmpty(data) || ObjectUtils.isEmpty(data.get("data")) ? Lists.newArrayList() : (List) MapUtils.getValueByExpress(data, "data:results");
        BaseOrderListRespDTO resp = new BaseOrderListRespDTO();
        resp.setResults(Lists.newArrayList());
        resp.setTotalCount(totalCount);
        resp.setPageIndex(req.getPageIndex());
        resp.setPageSize(req.getPageSize());
        if (!ObjectUtils.isEmpty(orderList)) {
            List transferList = etlService.transform(2260L, orderList);
            resp.setResults(transferList);
            return resp;
        }

        return Maps.newHashMap();
    }

    @SuppressWarnings("unchecked")
    private Map listAirOrder(AirOrderListReqDTO req) {
        // 审批单号
        Integer applyType = req.getApplyType();
        String applyId = req.getApplyId();
        if (!ObjectUtils.isEmpty(applyType) && !StringUtils.isBlank(applyId) && FuncIdTypeEnums.THIRD_ID.getKey() == applyType) {
            applyId = applyOrderService.queryApplyId(req.getCompanyId(), applyId);
            if (ObjectUtils.isEmpty(applyId)) {
                log.info("三方审批单不存在，直接返回");
                return MapUtils.obj2map(FuncResponseUtils.success(null), false);
            }
            req.setApplyId(applyId);
        }
        //下单人id
        String userId = req.getUserId();
        Integer userType = req.getUserType();
        if (!StringUtils.isBlank(userId) && !ObjectUtils.isEmpty(userType) && FuncIdTypeEnums.THIRD_ID.getKey() == userType) {
            ThirdEmployeeRes employeeByThirdId = openEmployeeService.getEmployeeByThirdId(req.getCompanyId(), userId);
            if (employeeByThirdId == null) {
                log.info("三方人员不存在");
                return MapUtils.obj2map(FuncResponseUtils.success(new HashMap<>()), false);
            }
            req.setUserId(employeeByThirdId.getEmployee().getId());
        }
        //构造请求参数
        AirOrderListRequest orderListRequest = buildAirOrderListRequest(req);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        String result = RestHttpUtils.get(airBizHost + "/airticket/intl/stereo/orders", headers, JsonUtils.toObj(JsonUtils.toJson(orderListRequest), Map.class));
        return JsonUtils.toObj(result, Map.class);
    }

    private AirOrderListRequest buildAirOrderListRequest(AirOrderListReqDTO req) {
        AirOrderListRequest request = new AirOrderListRequest();
        request.setOrderId(req.getOrderId());
        request.setCreateDateFrom(req.getCreateTimeBegin());
        request.setCreateDateTo(req.getCreateTimeEnd());
        request.setStartingDateFrom(req.getStartingDateFrom());
        request.setStartingDateTo(req.getStartingDateTo());
        request.setCompanyId(req.getCompanyId());
        request.setPassengerName(req.getPassengerName());
        request.setBookingPersonName(req.getUserName());
        request.setBookingPersonPhone(req.getUserPhone());
        request.setIntlFlag(req.getIsIntl() == null ? null : req.getIsIntl() ? 2 : 1);
        request.setPageIndex(req.getPageIndex());
        request.setPageSize(req.getPageSize());
        request.setOrderType(req.getOrderType());
        request.setOrderStatus(req.getStatus());
        request.setApplyId(req.getApplyId());
        request.setBookingPersonId(req.getUserId());
        return request;
    }

    public Object detail(AirOrderDetailReqDTO req) {
        Map<String, Object> cache = Maps.newHashMap();
        Map data = getAirOrder(req,cache);
        long etlId = req.getIsIntl() ? 2280L : 2270L;
        Map transformMap = etlService.transform(etlId, data);
        if (!ObjectUtils.isEmpty(transformMap)) {
            Map userInfo = (Map) MapUtils.getValueByExpress(transformMap, "user_info");
            EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo((String) userInfo.get("id"), req.getCompanyId());
            if (employeeContract != null) {
                userInfo.put("unit_id", employeeContract.getOrg_id());
                userInfo.put("employee_number", employeeContract.getEmployee_number());
            }
            try {
                setThirdInfo(req.getCompanyId(), transformMap);
            } catch (Exception e) {
            }
            thirdInfoService.setCostAttribution(req.getCompanyId(), transformMap, (Map) MapUtils.getValueByExpress(data, "data:cost_detail"));
        }
        setPriceInfo(req, data, transformMap);
        if (req.getIsIntl() && !ObjectUtils.isEmpty(cache.get(CACHE))) {
            IntlAirDetailResponseDTO intlAirDetailResponseDTO = JsonUtils.toObj(JsonUtils.toJson(cache.get(CACHE)), IntlAirDetailResponseDTO.class);
            AirThirdDTO airThirdDTO = this.setInitAirThirdInfo(req,intlAirDetailResponseDTO);
            transformMap.put(THIRD_INFO, JSON.parseObject(JSON.toJSONString(airThirdDTO)));
        }
        return transformMap;
    }

    /**
     * 兼容国际机票三方信息
     * @param data
     * @return
     */
    private AirThirdDTO setInitAirThirdInfo(AirOrderDetailReqDTO req,IntlAirDetailResponseDTO outResult){
        AirThirdDTO airThirdDTO = new AirThirdDTO();
        String id = outResult.getBookingPerson().getId();
        String userId = openIdTranService.fbIdToThirdId(req.getCompanyId(), id, IdBusinessTypeEnums.EMPLOYEE.getKey(), true);
        airThirdDTO.setUserId(userId);
       // airThirdDTO.setUnit_id(null);
        List<BaseOrderResDTO.CommonCostAttributionGroupList> costAttributionGroupList = outResult.getCostDetail().getCostAttributionGroupList();
        if (!ObjectUtils.isEmpty(costAttributionGroupList)){
            BaseOrderResDTO.CommonCostAttributionGroupList commonCostAttributionGroupList = costAttributionGroupList.get(0);
            List<BaseOrderResDTO.CommonCostAttributionList> costAttributionList = commonCostAttributionGroupList.getCostAttributionList();
            if (!ObjectUtils.isEmpty(costAttributionList)) {
                StringBuilder costDeptIds = new StringBuilder();
                costAttributionList.forEach(co->{
                    if (StringUtils.isNotBlank(co.getCode())) {
                        String code = co.getCode();
                        String costDeptId = openIdTranService.fbIdToThirdId(req.getCompanyId(), code,IdBusinessTypeEnums.ORG.getKey(), true);
                        costDeptIds.append(costDeptId).append(" ");
                    }
                });
                airThirdDTO.setCostDeptId(costDeptIds.toString());
            }
        }
        // airThirdDTO.setCost_project_id(null);
        // 三方审批单id
        String applyID = outResult.getSaasApplyInfo().getApplyID();
        airThirdDTO.setApplyId(this.tranApplyId(req.getCompanyId(),applyID));
        // 三方超规审批单id
        String duringApplyId = outResult.getDuringApplyId();
        airThirdDTO.setDuringApplyId(this.tranApplyId(req.getCompanyId(),duringApplyId));
       // 三方乘机人id
        String selectEmployeeId = outResult.getPassenger().getSelectEmployeeId();
        String passengerUserId = openIdTranService.fbIdToThirdId(req.getCompanyId(), selectEmployeeId,IdBusinessTypeEnums.EMPLOYEE.getKey(), true);
        airThirdDTO.setPassengerUnitId(passengerUserId);
       // 没有部门
       // airThirdDTO.setPassenger_unit_id(null);
        return airThirdDTO;
    }

    private String tranApplyId(String companyId,String applyId){
        String thirdApplyId = null;
        if (StringUtils.isNotBlank(companyId) && StringUtils.isNotBlank(applyId)) {
            try {
                ApplyOrderContract applyOrderContract = applyOrderService.queryApplyOrderDetail(companyId, applyId);
                if (!ObjectUtils.isEmpty(applyOrderContract)) {
                  thirdApplyId = applyOrderContract.getThird_id();
                }
            } catch (Exception e) {
               log.info("调用saas审批单id转换异常 异常信息->>>>",e);
            }
        }
        return thirdApplyId;
    }


    protected void setPriceInfo(AirOrderDetailReqDTO req, Map data, Map transformMap) {
        if (!ObjectUtils.isEmpty(transformMap)) {
            Map orderInfo = (Map) transformMap.get("order_info");
            if (ObjectUtils.isEmpty(orderInfo)) {
                return;
            }
            //是否国际机票
            boolean isIntl = req.getIsIntl();
            orderInfo.put("is_intl", isIntl);
            //金额代码里重新计算不走配置
            transformMap.remove("price_info");
            //订单状态
            Integer status = (Integer) orderInfo.get("status");
            //是否改签单
            boolean isChangeAirOrder = isChangeAirOrder(status);
            //是否退款单
            boolean isRefundAirOrder = isRefundAirOrder(status);
            //价格信息
            AirOrderPirceInfo pirceInfo;
            Map orderData = (Map) MapUtils.getValueByExpress(data, "data");

            //国际机票
            if (isIntl) {
                Map intlAirPirceInfo = (Map) MapUtils.getValueByExpress(data, "data:price_info");
                pirceInfo = new AirOrderPirceInfo();
                pirceInfo.setTotalPrice(BigDecimalUtils.obj2big(intlAirPirceInfo.get("total_price"), BigDecimal.ZERO));
                pirceInfo.setOrderPrice(BigDecimalUtils.obj2big(intlAirPirceInfo.get("order_total_price"), BigDecimal.ZERO));
                pirceInfo.setCompanyTotalPay(BigDecimalUtils.obj2big(orderData.get("company_total_pay"), BigDecimal.ZERO));
                pirceInfo.setPersonalTotalPay(BigDecimal.ZERO);
                pirceInfo.setTaxes(BigDecimalUtils.obj2big(intlAirPirceInfo.get("taxes"), BigDecimal.ZERO));
                pirceInfo.setTicketPrice(BigDecimalUtils.obj2big(intlAirPirceInfo.get("ticket_total_price"), BigDecimal.ZERO));
                pirceInfo.setRedEnvelope(BigDecimalUtils.obj2big(orderData.get("amount_coupon"), BigDecimal.ZERO));
                pirceInfo.setCouponAmount(BigDecimalUtils.obj2big(intlAirPirceInfo.get("coupon_price"), BigDecimal.ZERO));
                pirceInfo.setInsurancePrice(BigDecimalUtils.obj2big(intlAirPirceInfo.get("insurance_price"), BigDecimal.ZERO));
                if (isChangeAirOrder) {
                    pirceInfo.setChangeFee(BigDecimalUtils.obj2big(intlAirPirceInfo.get("endorse_fee"), BigDecimal.ZERO));
                    pirceInfo.setChangeServiceFee(BigDecimalUtils.obj2big(intlAirPirceInfo.get("service_fee"), BigDecimal.ZERO));
                    pirceInfo.setChangeUpgradePrice(BigDecimalUtils.obj2big(intlAirPirceInfo.get("upgrade_price"), BigDecimal.ZERO));
                }
                if (isRefundAirOrder) {
                    pirceInfo.setRefundFee(BigDecimalUtils.obj2big(intlAirPirceInfo.get("refund_fee"), BigDecimal.ZERO));
                    pirceInfo.setRefundServiceFee(BigDecimal.ZERO);
                }
                pirceInfo.setDiscount(BigDecimalUtils.obj2big(intlAirPirceInfo.get("discount"), BigDecimal.ONE));
            }
            //国内机票
            else {
                Map AirPirceInfo = (Map) MapUtils.getValueByExpress(data, "data:airInfo:priceInfo");
                pirceInfo = new AirOrderPirceInfo();
                pirceInfo.setTotalPrice(BigDecimalUtils.obj2big(AirPirceInfo.get("totalPrice"), BigDecimal.ZERO));
                pirceInfo.setOrderPrice(BigDecimalUtils.obj2big(AirPirceInfo.get("totalPrice"), BigDecimal.ZERO));
                pirceInfo.setCompanyTotalPay(BigDecimalUtils.obj2big(orderData.get("company_total_pay"), BigDecimal.ZERO));
                pirceInfo.setPersonalTotalPay(BigDecimalUtils.obj2big(orderData.get("amount_personal"), BigDecimal.ZERO));
                pirceInfo.setTicketPrice(BigDecimalUtils.obj2big(AirPirceInfo.get("salePrice"), BigDecimal.ZERO));
                pirceInfo.setRedEnvelope(BigDecimalUtils.obj2big(orderData.get("amount_coupon"), BigDecimal.ZERO));
                pirceInfo.setCouponAmount(BigDecimalUtils.obj2big(AirPirceInfo.get("couponAmount"), BigDecimal.ZERO));
                pirceInfo.setInsurancePrice(BigDecimalUtils.obj2big(AirPirceInfo.get("insurancePrice"), BigDecimal.ZERO));
                pirceInfo.setAirPortFuelTax(BigDecimalUtils.obj2big(AirPirceInfo.get("airPortAndFuelTax"), BigDecimal.ZERO));
                pirceInfo.setAirPortFee(null);
                pirceInfo.setFuelFee(null);
                if (isChangeAirOrder) {
                    pirceInfo.setChangeFee(BigDecimalUtils.obj2big(AirPirceInfo.get("endorseFee"), BigDecimal.ZERO));
                    pirceInfo.setChangeServiceFee(BigDecimalUtils.obj2big(AirPirceInfo.get("agencyFee"), BigDecimal.ZERO));
                    pirceInfo.setChangeUpgradePrice(BigDecimalUtils.obj2big(AirPirceInfo.get("upgradePrice"), BigDecimal.ZERO));
                }
                if (isRefundAirOrder) {
                    pirceInfo.setRefundFee(BigDecimalUtils.obj2big(AirPirceInfo.get("serviceFee"), BigDecimal.ZERO));
                    pirceInfo.setRefundServiceFee(BigDecimal.ZERO);
                }
                pirceInfo.setDiscount(BigDecimalUtils.obj2big(AirPirceInfo.get("discount"), BigDecimal.ONE));
            }
            transformMap.put("price_info", MapUtils.request2map2(pirceInfo));
        }
    }

    private boolean isRefundAirOrder(Integer status) {
        return status == 1811;
    }

    private boolean isChangeAirOrder(Integer status) {
        return status == 1821 || status == 1823;
    }

    protected void setThirdInfo(String companyId, Map transformMap) {
        FuncThirdInfoExpressDTO express = new FuncThirdInfoExpressDTO();
        express.setUserExpressList(Lists.newArrayList(ThirdCommonExpress.builder().express("user_info:id").tgtField("user_id").build()));
        express.setDeptExpressList(Lists.newArrayList(ThirdCommonExpress.builder().express("user_info:unit_id").tgtField("unit_id").build()));
        express.setApplyExpressList(Lists.newArrayList(ThirdCommonExpress.builder().express("saas_info:apply_id").tgtField("apply_id").group("apply").build(),
                ThirdCommonExpress.builder().express("saas_info:during_apply_id").tgtField("during_apply_id").group("during_apply").build()));
        List<Map> costList = (List<Map>) MapUtils.getValueByExpress(transformMap, "saas_info:cost_attribution_list");
        if (!ObjectUtils.isEmpty(costList)) {
            List<ThirdCostExpress> thirdCostExpresses = costList.stream()
                    .map(cost -> {
                        int costCategory = (Integer) cost.get("cost_attribution_category");
                        return ThirdCostExpress.builder().id((String) cost.get("cost_attribution_id")).costCategory(costCategory).tgtField(costCategory == 1 ? "cost_dept_id" : "cost_project_id").build();
                    }).collect(Collectors.toList());
            express.setCostExpressList(thirdCostExpresses);
        }
        express.setUserPhoneExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdUserPhoneExpress.builder()
                .phone((String) MapUtils.getValueByExpress(transformMap, "passenger_info:phone"))
                .tgtDept("passenger_unit_id")
                .tgtUser("passenger_user_id")
                .build()));
        thirdInfoService.setThirdInfoMap(companyId, transformMap, express);
    }

    private Map getAirOrder(AirOrderDetailReqDTO req) {
        return this.getAirOrder(req,null);
    }

    private Map getAirOrder(AirOrderDetailReqDTO req,Map<String,Object> cache) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        String url = null;
        if (req.getIsIntl()) {
            url = airBizHost + String.format("/airticket/intl/stereo/orders/%s/%s", req.getOrderId(), req.getTicketId());
        } else {
            url = airBizHost + String.format("/v2/companies/orders/airs/%s", req.getTicketId());
        }
        String resultStr = RestHttpUtils.get(url, headers, Maps.newHashMap());
        log.info("查询机票订单详情返回数据:{}",resultStr);
        Map data = JsonUtils.toObj(resultStr, Map.class);
        Map result = data.get("data") != null ? (Map) data.get("data") : new HashMap<>();
        if (ObjectUtils.isEmpty(result)) {
            return Maps.newHashMap();
        } else {
            if (req.getIsIntl()) {
                if (Objects.nonNull(cache)) {
                    cache.put(CACHE,result);
                }
                Map goFlightData = (Map) MapUtils.getValueByExpress(data, "data:segment:go_segment");
                if (!ObjectUtils.isEmpty(goFlightData)) {
                    List goFlightDetailList = (List) MapUtils.getValueByExpress(data, "data:segment_detail:go_segment_detail_list");
                    List<String> goFlightNo = Lists.newArrayList();
                    List<String> goCabinMsg = Lists.newArrayList();
                    goFlightDetailList.forEach(detail -> {
                        Map detailMap = ((Map) detail);
                        goFlightNo.add((String) detailMap.get("flight_no"));
                        goCabinMsg.add((String) detailMap.get("cabin_msg"));
                    });
                    goFlightData.put("flight_no", String.join("/", goFlightNo));
                    goFlightData.put("cabin_msg", String.join("/", goCabinMsg));
                }
                Map backFlightData = (Map) MapUtils.getValueByExpress(data, "data:segment:back_segment");
                if (!ObjectUtils.isEmpty(backFlightData)) {
                    List flightDetailList = (List) MapUtils.getValueByExpress(data, "data:segment_detail:back_segment_detail_list");
                    List<String> backFlightNo = Lists.newArrayList();
                    List<String> backCabinMsg = Lists.newArrayList();
                    flightDetailList.forEach(detail -> {
                        Map detailMap = ((Map) detail);
                        backFlightNo.add((String) detailMap.get("flight_no"));
                        backCabinMsg.add((String) detailMap.get("cabin_msg"));
                    });
                    backFlightData.put("flight_no", String.join("/", backFlightNo));
                    backFlightData.put("cabin_msg", String.join("/", backCabinMsg));
                }
            } else {
                Map flightData = (Map) MapUtils.getValueByExpress(data, "data:airInfo:flightInfo");
                long arrivedTimestamp = (long) flightData.get("arrived_timestamp");
                flightData.put("arrived_date", DateUtils.toSimpleStr(new Date(arrivedTimestamp), true));
                //出发时间 到达时间
                Lists.newArrayList("departure_time", "arrived_time").forEach(key -> {
                    String value = (String) flightData.get(key);
                    if (value.length() == 3) {
                        value = "0" + value;
                    }
                    if (!StringUtils.isBlank(value) && !value.contains(":")) {
                        String time = value.substring(0, 2) + ":" + value.substring(2);
                        flightData.put(key, time);
                    }
                });
            }
        }
        return data;
    }


    public Object export(AirOrderExportReqDTO req) {
        Map data = exportAirOrder(req);
        int totalCount = ObjectUtils.isEmpty(data) || ObjectUtils.isEmpty(data.get("data")) ? 0 : (int) MapUtils.getValueByExpress(data, "data:total_count");
        List orderList = ObjectUtils.isEmpty(data) || ObjectUtils.isEmpty(data.get("data")) ? Lists.newArrayList() : (List) MapUtils.getValueByExpress(data, "data:results");
        BaseOrderListRespDTO resp = new BaseOrderListRespDTO();
        resp.setResults(Lists.newArrayList());
        resp.setTotalCount(totalCount);
        resp.setPageIndex(req.getPageIndex());
        resp.setPageSize(req.getPageSize());
        if (!ObjectUtils.isEmpty(orderList)) {
            List transferList = etlService.transform(2370L, orderList);
            resp.setResults(transferList);
        }
        return resp;
    }

    @SuppressWarnings("unchecked")
    private Map exportAirOrder(AirOrderExportReqDTO req) {
        // 审批单号
        Integer applyType = req.getApplyType();
        String applyId = req.getApplyId();
        if (!ObjectUtils.isEmpty(applyType) && !StringUtils.isBlank(applyId) && FuncIdTypeEnums.THIRD_ID.getKey() == applyType) {
            applyId = applyOrderService.queryApplyId(req.getCompanyId(), applyId);
            if (ObjectUtils.isEmpty(applyId)) {
                log.info("三方审批单不存在，直接返回");
                return MapUtils.obj2map(FuncResponseUtils.success(null), false);
            }
            req.setApplyId(applyId);
        }
        //下单人id
        String userId = req.getUserId();
        Integer userType = req.getUserType();
        if (!StringUtils.isBlank(userId) && !ObjectUtils.isEmpty(userType) && FuncIdTypeEnums.THIRD_ID.getKey() == userType) {
            ThirdEmployeeRes employeeByThirdId = openEmployeeService.getEmployeeByThirdId(req.getCompanyId(), userId);
            if (employeeByThirdId == null) {
                log.info("三方人员不存在");
                return MapUtils.obj2map(FuncResponseUtils.success(new HashMap<>()), false);
            }
            req.setUserId(employeeByThirdId.getEmployee().getId());
        }
        //构造请求参数
        AirOrderExportRequest orderExportRequest = buildAirOrderExportRequest(req);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        String result = RestHttpUtils.get(airBizHost + "/airticket/intl/stereo/orders/export", headers, JsonUtils.toObj(JsonUtils.toJson(orderExportRequest), Map.class));
        return JsonUtils.toObj(result, Map.class);
    }

    private AirOrderExportRequest buildAirOrderExportRequest(AirOrderExportReqDTO req) {
        AirOrderExportRequest request = new AirOrderExportRequest();
        request.setOrderId(req.getOrderId());
        request.setCreateDateFrom(req.getCreateTimeBegin());
        request.setCreateDateTo(req.getCreateTimeEnd());
        request.setStartingDateFrom(req.getStartingDateFrom());
        request.setStartingDateTo(req.getStartingDateTo());
        request.setCompanyId(req.getCompanyId());
        request.setPassengerName(req.getPassengerName());
        request.setBookingPersonName(req.getUserName());
        request.setBookingPersonPhone(req.getUserPhone());
        request.setIntlFlag(req.getIsIntl() == null ? null : req.getIsIntl() ? 2 : 1);
        request.setPageIndex(req.getPageIndex());
        request.setPageSize(req.getPageSize());
        request.setOrderType(1);
        request.setOrderStatus(req.getStatus());
        request.setApplyId(req.getApplyId());
        request.setBookingPersonId(req.getUserId());
        return request;
    }


    @Data
    public static class AirOrderPirceInfo {

        /**
         * 公司支付总金额
         */
        @JsonProperty("total_price")
        private BigDecimal totalPrice;

        /**
         * 订单总价
         */
        @JsonProperty("order_price")
        private BigDecimal orderPrice;

        /**
         * 公司支付金额
         */
        @JsonProperty("company_total_pay")
        private BigDecimal companyTotalPay;

        /**
         * 个人支付金额
         */
        @JsonProperty("personal_total_pay")
        private BigDecimal personalTotalPay;

        /**
         * 手续费
         */
        @JsonProperty("fee")
        private BigDecimal fee;

        /**
         * 国际机票税费
         */
        @JsonProperty("taxes")
        private BigDecimal taxes;

        /**
         * 票价
         */
        @JsonProperty("ticket_price")
        private BigDecimal ticketPrice;

        /**
         * 红包券支付金额
         */
        @JsonProperty("red_envelope")
        private BigDecimal redEnvelope;

        /**
         * 机建燃油费
         */
        @JsonProperty("air_port_fuel_tax")
        private BigDecimal airPortFuelTax;

        /**
         * 优惠券金额
         */
        @JsonProperty("coupon_amount")
        private BigDecimal couponAmount;

        /**
         * 机建费
         */
        @JsonProperty("air_port_fee")
        private BigDecimal airPortFee;

        /**
         * 燃油费
         */
        @JsonProperty("fuel_fee")
        private BigDecimal fuelFee;

        /**
         * 保险费
         */
        @JsonProperty("insurance_price")
        private BigDecimal insurancePrice;

        /**
         * 改签费
         */
        @JsonProperty("change_fee")
        private BigDecimal changeFee;

        /**
         * 改签服务费
         */
        @JsonProperty("change_service_fee")
        private BigDecimal changeServiceFee;

        /**
         * 改签升舱费
         */
        @JsonProperty("change_upgrade_price")
        private BigDecimal changeUpgradePrice;

        /**
         * 退票费
         */
        @JsonProperty("refund_fee")
        private BigDecimal refundFee;

        /**
         * 退票手续费
         */
        @JsonProperty("refund_service_fee")
        private BigDecimal refundServiceFee;

        /**
         * 折扣
         */
        private BigDecimal discount;
    }
}
