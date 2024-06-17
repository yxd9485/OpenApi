package com.fenbeitong.openapi.plugin.func.order.service;


import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.common.FuncIdTypeEnums;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.order.dto.CarOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.FuncThirdInfoExpressDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.support.util.FinhubAdminTokenUtil;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderService;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
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
 * <p>Title: FuncCarOrderServiceImpl</p>
 * <p>Description: 用车订单开放</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/17 4:58 PM
 */
@Slf4j
@ServiceAspect
@Service
public class FuncCarOrderServiceImpl extends AbstractOrderService {

    @Value("${host.car_biz}")
    private String carUrl;

    private static final Long CAR_LIST_CONFIGID = 2250L;

    private static final Long CAR_DETAIL_CONFIGID = 2250L;

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

    public Object list(CarOrderListReqDTO req) {
        Map<String, Object> carData = new HashMap<>();
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Integer count = 0;
        try {
            carData = getCarListData(req);
            if (!ObjectUtils.isEmpty(carData.get("data"))) {
                data = (Map<String, Object>) carData.get("data");
                count = NumericUtils.obj2int(data.get("totalCount"), 0);
            }
            if (count > 0) {
                dataList = (List<Map<String, Object>>) data.get("results");
                resp.put("results", etlService.transform(CAR_LIST_CONFIGID, dataList));
                Integer pageIndex = req.getPageIndex();
                Integer pageSize = req.getPageSize();
                resp.put("total_count", count);
                resp.put("total_pages", (count + pageSize - 1) / pageSize);
                resp.put("page_index", pageIndex);
                resp.put("page_size", pageSize);
            }
        } catch (Exception e) {
            log.warn(">>>用车查询列表查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return resp;
    }

    public Object detail(OrderDetailReqDTO req) {
        Map<String, Object> carData = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            carData = getCarDetailData(req);
            if (!ObjectUtils.isEmpty(carData.get("data"))) {
                data = (Map<String, Object>) carData.get("data");
                if (data != null && data.size() > 0) {
                    result = etlService.transform(CAR_DETAIL_CONFIGID, data);
                }
                if (!ObjectUtils.isEmpty(result)) {
                    try{
                        setThirdInfo(req.getCompanyId(), result);
                    }catch (Exception e){}

                    Map saasInfo = (Map) MapUtils.getValueByExpress(result, "saas_info");
                    Object customRemark =  data.get("customRemark");
                    if(!ObjectUtils.isEmpty(customRemark)){
                        List<Map<String,Object>> costAttrList  = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(result, "saas_info:cost_attribution_list")),List.class);
                        if(!ObjectUtils.isEmpty(costAttrList)){
                            costAttrList.stream().forEach(c->c.put("cost_attribution_custom_ext", customRemark));
                            saasInfo.put("cost_attribution_list",costAttrList);
                        }
                    }
                    Map userInfo = (Map) MapUtils.getValueByExpress(result, "user_info");
                    EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo((String) userInfo.get("id"),req.getCompanyId());
                    if (employeeContract != null) {
                        userInfo.put("employee_number", employeeContract.getEmployee_number());
                    }
                    Map srcPriceInfo = (Map) MapUtils.getValueByExpress(carData, "data:orderPriceInfo");
                    Map tarPriceInfo = (Map) MapUtils.getValueByExpress(result, "price_info");

                    if (!ObjectUtils.isEmpty(srcPriceInfo) && !ObjectUtils.isEmpty(tarPriceInfo)) {
                        BigDecimal amountCompany = BigDecimalUtils.obj2big(srcPriceInfo.get("amountCompany"), BigDecimal.ZERO);
                        BigDecimal amountRedcoupon = BigDecimalUtils.obj2big(srcPriceInfo.get("amountRedcoupon"), BigDecimal.ZERO);
                        BigDecimal companyTotalPay = amountCompany.add(amountRedcoupon);
                        tarPriceInfo.put("company_total_pay", companyTotalPay);
                    }
                }
            }
        } catch (Exception e) {
            log.warn(">>>用车查询详情查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return result;
    }

    @SuppressWarnings("all")
    private void setThirdInfo(String companyId, Map transformMap) {
        FuncThirdInfoExpressDTO express = new FuncThirdInfoExpressDTO();
        express.setUserExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("user_info:id").tgtField("user_id").build()));
        express.setDeptExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("user_info:unit_id").tgtField("unit_id").build()));
        express.setApplyExpressList(Lists.newArrayList(FuncThirdInfoExpressDTO.ThirdCommonExpress.builder().express("saas_info:apply_id").tgtField("apply_id").group("apply").build()));
        List<Map> costList = (List<Map>) com.fenbeitong.openapi.plugin.util.MapUtils.getValueByExpress(transformMap, "saas_info:cost_attribution_list");
        if (!ObjectUtils.isEmpty(costList)) {
            List<FuncThirdInfoExpressDTO.ThirdCostExpress> thirdCostExpresses = costList.stream()
                    .map(cost -> {
                        int costCategory = (Integer) cost.get("cost_attribution_category");
                        return FuncThirdInfoExpressDTO.ThirdCostExpress.builder().id((String) cost.get("cost_attribution_id")).costCategory(costCategory).tgtField(costCategory == 1 ? "cost_dept_id" : "cost_project_id").build();
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


    /**
     * 组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getCarListData(CarOrderListReqDTO request) throws Exception {
        Integer pageIndex = NumericUtils.obj2int(request.getPageIndex(), 1);
        Integer pageSize = NumericUtils.obj2int(request.getPageSize(), 10);
        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageIndex", pageIndex);
        paramMap.put("pageSize", pageSize);
        paramMap.put("orderId", request.getOrderId());
        paramMap.put("orderType", request.getOrderType());
        paramMap.put("createBegin", request.getCreateTimeBegin());
        paramMap.put("createEnd", request.getCreateTimeEnd());
        paramMap.put("companyId", request.getCompanyId());
        paramMap.put("bookingPerson", request.getUserName());
        paramMap.put("bookingPhone", request.getUserPhone());
        paramMap.put("passengerName", request.getPassengerName());
        if (request.getStatus() != null) {
            paramMap.put("orderStatus", com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(request.getStatus()));
        }
        // 审批单号
        Integer applyType = request.getApplyType();
        String applyId = request.getApplyId();
        if (!ObjectUtils.isEmpty(applyType) && !StringUtils.isBlank(applyId) && FuncIdTypeEnums.THIRD_ID.getKey() == applyType) {
            applyId = applyOrderService.queryApplyId(request.getCompanyId(), applyId);
            if (ObjectUtils.isEmpty(applyId)) {
                log.info("三方审批单不存在，直接返回");
                return MapUtils.obj2map(FuncResponseUtils.success(new HashMap<>()), false);
            }
        }
        paramMap.put("applyId", applyId);
        //下单人id
        String userId = request.getUserId();
        Integer userType = request.getUserType();
        if (!StringUtils.isBlank(userId) && !ObjectUtils.isEmpty(userType) && FuncIdTypeEnums.THIRD_ID.getKey() == userType) {
            ThirdEmployeeRes employeeByThirdId = openEmployeeService.getEmployeeByThirdId(request.getCompanyId(), userId);
            if (employeeByThirdId == null) {
                log.info("三方人员不存在");
                return MapUtils.obj2map(FuncResponseUtils.success(new HashMap<>()), false);
            }
            userId = employeeByThirdId.getEmployee().getId();
        }
        paramMap.put("employeeId", userId);
        StringBuilder url = new StringBuilder(carUrl);
        url.append("/car_biz/v2/companies/orders/taxies");
        log.info("用车订单列表查询开始，{}", url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        String responseBody = restHttpUtils.get(url.toString(), httpHeaders, paramMap);
        log.info("用车订单列表查询结束，返回数据为{}", responseBody);
        return analyzeResponse(responseBody);
    }

    /**
     * 组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getCarDetailData(OrderDetailReqDTO request) throws Exception {
        StringBuilder url = new StringBuilder(carUrl);
        url.append("/car_biz/v2/companies/orders/taxies/").append(request.getOrderId());
        log.info("用车订单详情查询开始，{}", url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        String responseBody = restHttpUtils.get(url.toString(), httpHeaders, new HashMap<>());
        log.info("用车订单详情查询结束，返回数据为{}", responseBody);
        return analyzeResponse(responseBody);
    }

    /**
     * 校验接口返回数据
     *
     * @param result
     * @return
     */
    public Map<String, Object> analyzeResponse(String result) {
        if (ObjectUtils.isEmpty(result)) {
            log.info("[调用外部接口返回值为NULL]");
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_ERROR));
        }
        Map<String, Object> obj = JsonUtils.toObj(result, HashMap.class);
        if (ObjectUtils.isEmpty(obj)) {
            log.info("[调用外部接口返回值的结果集为]{}", result);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_DATA_ERROR));
        }
        Integer code = NumericUtils.obj2int(obj.get("code"));
        if (code != 0) {
            throw new FinhubException(code, StringUtils.obj2str(obj.get("msg")));
        }
        return obj;
    }


}
