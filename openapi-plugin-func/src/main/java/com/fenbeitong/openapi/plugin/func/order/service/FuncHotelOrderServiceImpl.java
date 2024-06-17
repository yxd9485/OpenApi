package com.fenbeitong.openapi.plugin.func.order.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.hotel.api.model.vo.StereoHotelOrderDetailResponse;
import com.fenbeitong.hotel.api.service.HotelStereoOrderService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.common.FuncIdTypeEnums;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.order.dto.FuncThirdInfoExpressDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.HotelOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.support.util.FinhubAdminTokenUtil;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderService;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncHotelOrderServiceImpl</p>
 * <p>Description: 酒店订单开放</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/15 4:58 PM
 */
@Slf4j
@ServiceAspect
@Service
public class FuncHotelOrderServiceImpl extends AbstractOrderService {

    @Value("${host.hotel_biz}")
    private String hotelUrl;

    private static final Long HOTEL_LIST_CONFIGID = 2230l;
    private static final Long HOTEL_DETAIL_CONFIGID = 2340l;

    @Autowired
    private IEtlService etlService;

    @Autowired
    private FuncThirdInfoServiceImpl thirdInfoService;

    @Autowired
    private RestHttpUtils restHttpUtils;

    @DubboReference(check = false)
    private IApplyOrderService applyOrderService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @DubboReference(check = false)
    HotelStereoOrderService hotelStereoOrderService;

    public Object list(HotelOrderListReqDTO req) {
        Map<String, Object> hotelData = new HashMap<>();
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Integer count = 0;
        try {
            hotelData = getHotelListData(req);
            if (ObjUtils.isNotBlank(hotelData.get("data"))) {
                data = (Map<String, Object>) hotelData.get("data");
                count = ObjUtils.toInteger(data.get("totalCount"), 0);
            }
            if (count > 0) {
                dataList = (List<Map<String, Object>>) data.get("results");
                resp.put("results", etlService.transform(HOTEL_LIST_CONFIGID, dataList));
                Integer pageIndex = req.getPageIndex();
                Integer pageSize = req.getPageSize();
                resp.put("total_count", count);
                resp.put("total_pages", (count + pageSize - 1) / pageSize);
                resp.put("page_index", pageIndex);
                resp.put("page_size", pageSize);
            }
        } catch (Exception e) {
            log.warn(">>>酒店查询列表查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return resp;
    }

    public Object detail(OrderDetailReqDTO req) {
        Map<String, Object> hotelData = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            hotelData = getHotelDetailData(req);
            if (ObjUtils.isNotBlank(hotelData)) {
//                data = (Map<String, Object>) hotelData.get("data");
                data = covertResultData(hotelData);
                if (dataList != null) {
                    result = etlService.transform(HOTEL_DETAIL_CONFIGID, data);
                }
                if (!ObjUtils.isEmpty(result)) {
                    try {
                        setThirdInfo(req.getCompanyId(), result);
                    } catch (Exception e) {
                    }
                    Map saas_info = (Map) MapUtils.getValueByExpress(result, "saas_info");
                    if (data.get("applyInfo") != null) {
                        JSONArray jSONArray=JSONArray.parseArray(JsonUtils.toJson(data.get("applyInfo")));
                        HashMap applyInfo = JSON.parseObject(jSONArray.get(0).toString(), HashMap.class);
                        if (applyInfo.get("exceed_info") != null) {
                            Map exceedInfo = JSON.parseObject(applyInfo.get("exceed_info").toString(), HashMap.class);
                            saas_info.put("is_exceed", true);
                            saas_info.put("exceed_reason", exceedInfo.get("reason"));
                        } else {
                            saas_info.put("is_exceed", false);
                        }
                        if(applyInfo.get("apply_center_info")!=null) {
                            Map applyCenterInfo = JSON.parseObject(applyInfo.get("apply_center_info").toString(), HashMap.class);
                            if (!MapUtils.isBlank(applyCenterInfo)) {
                                saas_info.put("exceed_item", applyCenterInfo.get("exceed_buy_desc_list"));
                            }
                        }
                    }
                    Object customRemark =  data.get("customRemark");
                    if(!ObjectUtils.isEmpty(customRemark)){
                        List<Map<String,Object>> costAttrList  = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(result, "saas_info:cost_attribution_list")),List.class);
                        if(!ObjectUtils.isEmpty(costAttrList)){
                            costAttrList.stream().forEach(c->c.put("cost_attribution_custom_ext", customRemark));
                            saas_info.put("cost_attribution_list",costAttrList);
                        }
                    }
                    Map userInfo = (Map) MapUtils.getValueByExpress(result, "user_info");
                    EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo((String) userInfo.get("id"),req.getCompanyId());
                    if (employeeContract != null) {
                        userInfo.put("employee_number", employeeContract.getEmployee_number());
                    }
                    thirdInfoService.setCostAttribution(req.getCompanyId(), result, (Map) MapUtils.getValueByExpress(data, "data:costDetail"));
                }
            }
        } catch (Exception e) {
            log.warn(">>>酒店查询列表查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        setHotelPrice(hotelData, result);
        return result;
    }

    private void setHotelPrice(Map<String, Object> hotelData, Map<String, Object> result) {
        if (!ObjectUtils.isEmpty(result)) {
            Map hotelPriceMap = (Map) MapUtils.getValueByExpress(hotelData, "data:hotelPriceInfo");
            if (ObjectUtils.isEmpty(hotelPriceMap)) {
                return;
            }
            //是否退款单
            boolean isRefundHotelOrder = false;
            Map orderStatus = (Map) MapUtils.getValueByExpress(hotelData, "data:orderStatus");
            if(!ObjectUtils.isEmpty(orderStatus) && !ObjectUtils.isEmpty(orderStatus.get("key"))){
              Integer state = (Integer) orderStatus.get("key");
                isRefundHotelOrder = isRefundHotelOrder(state);
            }
            //金额代码里重新计算不走配置
            result.remove("price_info");
            //酒店价格信息
            HotelOrderPriceInfo priceInfo = new HotelOrderPriceInfo();
            Map payDetailMap = (Map) MapUtils.getValueByExpress(hotelData, "data:payDetailVO");
            if (!ObjectUtils.isEmpty(payDetailMap)) {
                priceInfo.setCompanyTotalPay(BigDecimalUtils.obj2big(payDetailMap.get("companyTotalPay"), BigDecimal.ZERO));
                priceInfo.setPersonalTotalPay(BigDecimalUtils.obj2big(payDetailMap.get("amountPersonal"), BigDecimal.ZERO));
                priceInfo.setRedEnvelope(BigDecimalUtils.obj2big(payDetailMap.get("amountCoupon"), BigDecimal.ZERO));
                if(isRefundHotelOrder){
                    priceInfo.setCompanyTotalPay(BigDecimalUtils.obj2big(payDetailMap.get("companyTotalPay"), BigDecimal.ZERO).negate());
                }
            }
            priceInfo.setTotalPrice(BigDecimalUtils.obj2big(hotelPriceMap.get("totalPrice"), BigDecimal.ZERO));
            priceInfo.setOrderPrice(BigDecimalUtils.obj2big(hotelPriceMap.get("totalPayPrice"), BigDecimal.ZERO));
            priceInfo.setCouponAmount(BigDecimalUtils.obj2big(hotelPriceMap.get("couponAmount"), BigDecimal.ZERO));
            List<Map> insuranceList = (List<Map>) MapUtils.getValueByExpress(hotelData, "data:insuranceInfo");
            if (!ObjectUtils.isEmpty(insuranceList)) {
                BigDecimal insurancePrice = insuranceList.stream().map(insurance -> BigDecimalUtils.obj2big(insurance.get("unit_price"))).reduce(BigDecimal.ZERO, BigDecimal::add);
                priceInfo.setInsurancePrice(insurancePrice);
            }
            result.put("price_info", priceInfo);
        }
    }

    @SuppressWarnings("all")
    private void setThirdInfo(String companyId, Map transformMap) {
        FuncThirdInfoExpressDTO express = new FuncThirdInfoExpressDTO();
        express.setUserExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("user_info:id").tgtField("user_id").build()));
        express.setDeptExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("user_info:unit_id").tgtField("unit_id").build()));
        express.setApplyExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("saas_info:apply_id").tgtField("apply_id").group("apply").build(),
                FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("saas_info:during_apply_id").tgtField("during_apply_id").group("during_apply").build()));
        List<Map> costList = (List<Map>) MapUtils.getValueByExpress(transformMap, "saas_info:cost_attribution_list");
        if (!ObjectUtils.isEmpty(costList)) {
            List<FuncThirdInfoExpressDTO.ThirdCostExpress> thirdCostExpresses = costList.stream()
                    .map(cost -> {
                        int costCategory = (Integer) cost.get("cost_attribution_category");
                        return FuncThirdInfoExpressDTO.ThirdCostExpress.builder().id((String) cost.get("cost_attribution_id")).costCategory(costCategory).tgtField(costCategory == 1 ? "cost_dept_id" : "cost_project_id").build();
                    }).collect(Collectors.toList());
            express.setCostExpressList(thirdCostExpresses);
        }
        List<Map> guestList = (List) MapUtils.getValueByExpress(transformMap, "guest_info");
        if (!ObjectUtils.isEmpty(guestList)) {
            List<FuncThirdInfoExpressDTO.ThirdUserPhoneExpress> userPhoneExpressList = guestList.stream().map(guest -> {
                return FuncThirdInfoExpressDTO.ThirdUserPhoneExpress.builder()
                        .phone((String) guest.get("phone"))
                        .tgtDept("guest_unit_id")
                        .tgtUser("guest_user_id")
                        .build();
            }).collect(Collectors.toList());
            express.setUserPhoneExpressList(userPhoneExpressList);
        }
        thirdInfoService.setThirdInfoMap(companyId, transformMap, express);
    }


    /**
     * 组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getHotelListData(HotelOrderListReqDTO request) throws Exception {
        Integer pageIndex = ObjUtils.toInteger(request.getPageIndex(), 1);
        Integer pageSize = ObjUtils.toInteger(request.getPageSize(), 10);
        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        String createBegin = request.getCreateTimeBegin();
        String createEnd = request.getCreateTimeEnd();
        String companyId = request.getCompanyId();
        String orderId = request.getOrderId();
        String bookingPerson = request.getUserName();
        String bookingPhone = request.getUserPhone();
        String guestName = request.getGuestName();
        Integer status = request.getStatus();
        Integer applyType = request.getApplyType();
        Integer orderType = request.getOrderType();
        String applyId = request.getApplyId();
        StringBuilder url = new StringBuilder(hotelUrl);
        url.append("/v2/companies/orders/hotels?")
                .append("page_index=").append(pageIndex).append("&page_size=").append(pageSize);
        if (StringUtils.isNotBlank(orderId)) {
            url.append("&order_id=").append(StringUtils.deleteWhitespace(orderId));
        }
        if (status != null) {
            url.append("&order_status=").append(status);
        }
        if (StringUtils.isNotBlank(companyId)) {
            url.append("&company_id=").append(StringUtils.deleteWhitespace(companyId));
        }
        if (StringUtils.isNotBlank(bookingPerson)) {
            url.append("&booking_person=").append(StringUtils.deleteWhitespace(bookingPerson));
        }
        if (StringUtils.isNotBlank(bookingPhone)) {
            url.append("&booking_phone=").append(StringUtils.deleteWhitespace(bookingPhone));
        }
        if (StringUtils.isNotBlank(guestName)) {
            url.append("&customer_name=").append(StringUtils.deleteWhitespace(guestName));
        }
        if (StringUtils.isNotBlank(createBegin)) {
            url.append("&create_time_begin=").append(StringUtils.deleteWhitespace(createBegin));
        }
        if (StringUtils.isNotBlank(createEnd)) {
            url.append("&create_time_end=").append(StringUtils.deleteWhitespace(createEnd));
        }
        if (!ObjectUtils.isEmpty(orderType)) {
            url.append("&order_type=").append(orderType);
        }
        if (StringUtils.isNotBlank(applyId) && !ObjectUtils.isEmpty(applyType)) {
            if (FuncIdTypeEnums.THIRD_ID.getKey() == applyType) {
                applyId = applyOrderService.queryApplyId(request.getCompanyId(), applyId);
                if (ObjectUtils.isEmpty(applyId)) {
                    log.info("三方审批单不存在，直接返回");
                    return MapUtils.obj2map(FuncResponseUtils.success(new HashMap<>()), false);
                }
            }
            url.append("&apply_id=").append(StringUtils.deleteWhitespace(applyId));
        }
        //下单人id
        String userId = request.getUserId();
        Integer userType = request.getUserType();
        if (!ObjectUtils.isEmpty(userId) && !ObjectUtils.isEmpty(userType)) {
            if (FuncIdTypeEnums.THIRD_ID.getKey() == userType) {
                ThirdEmployeeRes employeeByThirdId = openEmployeeService.getEmployeeByThirdId(request.getCompanyId(), userId);
                if (employeeByThirdId == null) {
                    log.info("三方人员不存在");
                    return MapUtils.obj2map(FuncResponseUtils.success(new HashMap<>()), false);
                }
                userId = employeeByThirdId.getEmployee().getId();
            }
            url.append("&employee_id=").append(StringUtils.deleteWhitespace(userId));
        }
        url.append("&timestamp=").append(System.currentTimeMillis());
        log.info("酒店订单列表查询开始，{}", url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        String responseBody = RestHttpUtils.get(url.toString(), httpHeaders, new HashMap<>());
        log.info("酒店订单列表查询结束，返回数据为{}", responseBody);
        return analyzeResponse(responseBody);
    }

    /**
     * 组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getHotelDetailData(OrderDetailReqDTO request) throws Exception {
        String orderId = request.getOrderId();
        log.info("酒店订单详情查询开始，orderId:{}", orderId);
        StereoHotelOrderDetailResponse responseBody = hotelStereoOrderService.stereoQueryOrderDetail(orderId);
        log.info("酒店订单详情查询结束，返回数据为{}", JsonUtils.toJson(responseBody));
        String companyIdRes = (String)MapUtils.getValueByExpress(JsonUtils.toObj(JsonUtils.toJson(responseBody), HashMap.class), "company:id");
        if(!request.getCompanyId().equals(companyIdRes)){
            log.info("[公司id不一致]，原企业id{}，返回企业id{}",request.getCompanyId(), companyIdRes);
            throw new FinhubException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HOTEL_COMPANY_ERROR), "只能查询本企业下的酒店订单数据");
        };
        return analyzeResponse(JsonUtils.toJson(responseBody));
    }

    /**
     * 校验接口返回数据
     *
     * @param result
     * @return
     */
    public Map<String, Object> analyzeResponse(String result) {
        if (StringUtils.isEmpty(result)) {
            log.info("[调用外部接口返回值为NULL]");
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_ERROR));
        }
        Map<String, Object> obj = JsonUtils.toObj(result, HashMap.class);
        if (ObjectUtils.isEmpty(obj)) {
            log.info("[调用外部接口返回值的结果集为]{}", result);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_DATA_ERROR));
        }
        return obj;
    }

    /**
     * 处理审批单数据
     *
     * @param result
     * @return
     */
    public Map<String, Object> covertResultData(Map<String, Object> result) {
        if (ObjectUtils.isEmpty(result)) {
            return null;
        }
        JSONArray applyInfoNewArr = JSON.parseArray(JSON.toJSONString(result.get("applyInfoNew")));
        if (applyInfoNewArr != null && applyInfoNewArr.size() >0) {
            JSONObject jsonObject = applyInfoNewArr.getJSONObject(0);
            if (jsonObject.get("apply_center_info") != null) {
                JSONObject apply_center_info = JSON.parseObject(JSON.toJSONString(jsonObject.get("apply_center_info")));
                result.put("during_apply_id", apply_center_info.get("apply_id"));
            }
            if (jsonObject.get("apply_info") != null) {
                JSONObject apply_info = JSON.parseObject(JSON.toJSONString(jsonObject.get("apply_info")));
                result.put("apply_id", apply_info.get("apply_id"));
            }
        }
        return result;
    }

    @Data
    private static class HotelOrderPriceInfo {

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
         * 红包券支付金额
         */
        @JsonProperty("red_envelope")
        private BigDecimal redEnvelope;

        /**
         * 优惠券金额
         */
        @JsonProperty("coupon_amount")
        private BigDecimal couponAmount;

        /**
         * 保险费
         */
        @JsonProperty("insurance_price")
        private BigDecimal insurancePrice;

    }

    private boolean isRefundHotelOrder(Integer status) {
        return status == 2800;
    }


}
