package com.fenbeitong.openapi.plugin.func.order.service;


import com.fenbeitong.finhub.common.exception.FinhubException;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.RefundOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.TakeawayOrderListReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.SaasApplyCustomFieldRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.support.util.FinhubAdminTokenUtil;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.saasplus.api.model.dto.finance.CostInfoResult;
import com.fenbeitong.saasplus.api.service.finance.IOrderCostService;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoReqDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoResDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.json.JsonUtils;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncTakeawayOrderServiceImpl</p>
 * <p>Description: 外卖订单开放</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/18 6:58 PM
 */
@Slf4j
@ServiceAspect
@Service
public class FuncTakeawayOrderServiceImpl extends AbstractOrderService {

    @Value("${host.takeaway_biz}")
    private String takeawayUrl;

    private static final Long TAKEAWAY_LIST_CONFIGID = 2290l;
    private static final Long TAKEAWAY_DETAIL_CONFIGID = 2300l;
    private static final Long TAKEAWAY_REFUND_DETAIL_CONFIGID = 2310l;

    @Autowired
    private IEtlService etlService;
    @Autowired
    private RestHttpUtils restHttpUtils;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @DubboReference(check = false)
    private ICommonService commonService;

    @DubboReference(check = false)
    private IOrderCostService orderCostService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private FuncThirdInfoServiceImpl thirdInfoService;

    public Object list(TakeawayOrderListReqDTO req)  {
        Map<String, Object> takeawayData = new HashMap<>();
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Integer count = 0;
        dateValidate(req);
        try {
            takeawayData = getTakeawayListData(req);
            if (ObjUtils.isNotBlank(takeawayData.get("data"))) {
                data = (Map<String, Object>) takeawayData.get("data");
                count = ObjUtils.toInteger(data.get("totalSize"), 0);
            }
            List<Map<String, Object>> resultList= (List<Map<String, Object>>) data.get("content");
            if (count > 0) {
                List<Map> contentList = etlService.transform(TAKEAWAY_LIST_CONFIGID, resultList);
                Map saasInfo = new HashMap<>();
                for(Map result:contentList){
                    Map userInfo = (Map) MapUtils.getValueByExpress(result, "user_info");
                    Map orderInfo = (Map) MapUtils.getValueByExpress(result, "order_info");
                    for(Map content:resultList){
                        if(content.get("orderId").equals(orderInfo.get("order_id"))){
                            String applyId = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(content.get("applyId"));
                            saasInfo.put("applyId",applyId);
                        }
                    }
                    setThirdInfo(result, req.getCompanyId(), userInfo, saasInfo);

                }
                resp.put("results",contentList);
                Integer pageIndex = req.getPageIndex();
                Integer pageSize = req.getPageSize();
                resp.put("total_count", count);
                resp.put("total_pages", (count + pageSize - 1) / pageSize);
                resp.put("page_index", pageIndex);
                resp.put("page_size", pageSize);
            }
        } catch (Exception e) {
            log.warn(">>>外卖正向查询列表查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return resp;
    }

    public Object refundList(TakeawayOrderListReqDTO req) {
        Map<String, Object> takeawayData = new HashMap<>();
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Integer count = 0;
        dateValidate(req);
        try {
            takeawayData = getTakeawayRefundListData(req);
            if (ObjUtils.isNotBlank(takeawayData.get("data"))) {
                data = (Map<String, Object>) takeawayData.get("data");
                count = ObjUtils.toInteger(data.get("totalSize"), 0);
            }
            if (count > 0) {
                List<Map> contentList = etlService.transform(TAKEAWAY_LIST_CONFIGID, (List<Map<String, Object>>) data.get("content"));
                Map saasInfo = new HashMap<>();
                List<Map<String, Object>> resultList=  (List<Map<String, Object>>) data.get("content");
                for(Map result:contentList) {
                    Map userInfo = (Map) MapUtils.getValueByExpress(result, "user_info");
                    Map orderInfo = (Map) MapUtils.getValueByExpress(result, "order_info");
                    for (Map content : resultList) {
                        if (content.get("orderId").equals(orderInfo.get("order_id"))) {
                            String applyId = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(content.get("applyId"));
                            saasInfo.put("applyId", applyId);
                        }
                    }
                    setThirdInfo(result, req.getCompanyId(), userInfo, saasInfo);
                }
                resp.put("results", contentList);
                Integer pageIndex = req.getPageIndex();
                Integer pageSize = req.getPageSize();
                resp.put("total_count", count);
                resp.put("total_pages", (count + pageSize - 1) / pageSize);
                resp.put("page_index", pageIndex);
                resp.put("page_size", pageSize);

            }
        } catch (Exception e) {
            log.warn(">>>外卖逆向查询列表查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return resp;
    }


    public Object detail(OrderDetailReqDTO req) {
        Map<String, Object> takeawayData = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            takeawayData = getTakeawayDetailData(req);
            if (ObjUtils.isNotBlank(takeawayData.get("data"))) {
                data = (Map<String, Object>) takeawayData.get("data");
                if (data != null && data.size() > 0) {
                    result = etlService.transform(TAKEAWAY_DETAIL_CONFIGID, data);

                    Map userInfo = (Map) MapUtils.getValueByExpress(result, "user_info");
                    EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo((String) userInfo.get("id"),req.getCompanyId());
                    if (employeeContract != null) {
                        userInfo.put("employee_number", employeeContract.getEmployee_number());
                    }
                    setThirdInfo(result, req.getCompanyId(), userInfo, (Map)data.get("saasInfo"));
                }
                thirdInfoService.setCostAttribution(req.getCompanyId(), result, (Map) MapUtils.getValueByExpress(data, "data:costDetail"));
            }
        } catch (Exception e) {
            log.warn(">>>外卖正向查询详情查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return result;
    }

    public Object refundDetail(RefundOrderDetailReqDTO req) {
        Map<String, Object> takeawayData = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {
            takeawayData = getTakeawayRefundDetailData(req);
            if (ObjUtils.isNotBlank(takeawayData.get("data"))) {
                Map<String,Object> orderInfo = getCostDetailByOrderId(req.getRefundOrderId());
                data = (Map<String, Object>) takeawayData.get("data");
                data.putAll(orderInfo);

                if (data != null && data.size() > 0) {
                    result = etlService.transform(TAKEAWAY_REFUND_DETAIL_CONFIGID, data);

                    Map userInfo = (Map) MapUtils.getValueByExpress(result, "user_info");
                    EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo((String) userInfo.get("id"),req.getCompanyId());
                    if (employeeContract != null) {
                        userInfo.put("employee_number", employeeContract.getEmployee_number());
                    }
                    setThirdInfo(result, req.getCompanyId(), userInfo, (Map)data.get("saasInfo"));
                }
                thirdInfoService.setCostAttribution(req.getCompanyId(), result, (Map) MapUtils.getValueByExpress(data, "data:costDetail"));
            }
        } catch (Exception e) {
            log.warn(">>>外卖逆向查询详情查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return result;
    }


    /**
     * 组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getTakeawayRefundListData(TakeawayOrderListReqDTO request) throws Exception {
        Integer pageIndex = ObjUtils.toInteger(request.getPageIndex(), 1);
        Integer pageSize = ObjUtils.toInteger(request.getPageSize(), 10);
        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        StringBuilder url = new StringBuilder(takeawayUrl);
        url.append("/noc/search/takeaway/stereo/refund/order/list/v2");
        log.info("外卖逆向订单列表查询开始，{} param, {}", url, JsonUtils.toJson(request));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        MultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add("companyId", request.getCompanyId());
        multiValueMap.add("pageIndex", request.getPageIndex());
        multiValueMap.add("pageSize", request.getPageSize());
        multiValueMap.add("orderType", request.getOrderType());
        if (StringUtils.isNotBlank(request.getUserName())) {
            multiValueMap.add("username", request.getUserName());
        }
        if (StringUtils.isNotBlank(request.getUserPhone())) {
            multiValueMap.add("userPhone", request.getUserPhone());
        }
        if (StringUtils.isNotBlank(request.getConsigneeName())) {
            multiValueMap.add("consigneeName", request.getConsigneeName());
        }
        if (StringUtils.isNotBlank(request.getCreateTimeBegin())) {
            multiValueMap.add("createTimeBegin", request.getCreateTimeBegin());
        }
        if (StringUtils.isNotBlank(request.getCreateTimeEnd())) {
            multiValueMap.add("createTimeEnd", request.getCreateTimeEnd());
        }
        if (StringUtils.isNotBlank(request.getRefundOrderId())) {
            multiValueMap.add("refundOrderId", request.getRefundOrderId());
        }
        String responseBody = restHttpUtils.postForm(url.toString(), httpHeaders, multiValueMap);
        log.info("外卖逆向订单列表查询结束，返回数据为{}", responseBody);
        return analyzeResponse(responseBody);
    }

    /**
     * 组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getTakeawayListData(TakeawayOrderListReqDTO request) throws Exception {
        Integer pageIndex = ObjUtils.toInteger(request.getPageIndex(), 1);
        Integer pageSize = ObjUtils.toInteger(request.getPageSize(), 10);
        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        StringBuilder url = new StringBuilder(takeawayUrl);
        url.append("/noc/search/takeaway/stereo/order/list/v2");
        log.info("外卖正向订单列表查询开始，{} param, {}", url, JsonUtils.toJson(request));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        MultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add("companyId", request.getCompanyId());
        multiValueMap.add("pageIndex", request.getPageIndex());
        multiValueMap.add("pageSize", request.getPageSize());
        multiValueMap.add("orderType", request.getOrderType());
        if (StringUtils.isNotBlank(request.getUserName())) {
            multiValueMap.add("username", request.getUserName());
        }
        if (request.getStatus() != null) {
            multiValueMap.add("orderStatus", request.getStatus());
        }
        if (StringUtils.isNotBlank(request.getUserPhone())) {
            multiValueMap.add("userPhone", request.getUserPhone());
        }
        if (StringUtils.isNotBlank(request.getOrderId())) {
            multiValueMap.add("orderId", request.getOrderId());
        }
        if (StringUtils.isNotBlank(request.getConsigneeName())) {
            multiValueMap.add("consigneeName", request.getConsigneeName());
        }
        if (StringUtils.isNotBlank(request.getCreateTimeBegin())) {
            multiValueMap.add("createTimeBegin", request.getCreateTimeBegin());
        }
        if (StringUtils.isNotBlank(request.getCreateTimeEnd())) {
            multiValueMap.add("createTimeEnd", request.getCreateTimeEnd());
        }
        String responseBody = restHttpUtils.postForm(url.toString(), httpHeaders, multiValueMap);
        log.info("外卖正向订单列表查询结束，返回数据为{}", responseBody);
        return analyzeResponse(responseBody);
    }

    /**
     * 组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getTakeawayRefundDetailData(RefundOrderDetailReqDTO request) throws Exception {
        StringBuilder url = new StringBuilder(takeawayUrl);
        url.append("/noc/search/takeaway/stereo/refund/order/").append(request.getRefundOrderId()).append("/detail/v2");
        log.info("外卖逆向查询订单详情查询开始，{}", url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        String responseBody = restHttpUtils.get(url.toString(), httpHeaders, new HashMap<>());
        log.info("外卖逆向查询订单详情结束，返回数据为{}", responseBody);
        return analyzeResponse(responseBody);
    }

    /**
     * 组装参数
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Map<String, Object> getTakeawayDetailData(OrderDetailReqDTO request) throws Exception {
        StringBuilder url = new StringBuilder(takeawayUrl);
        url.append("/noc/search/takeaway/stereo/order/").append(request.getOrderId()).append("/detail/v2");
        log.info("外卖正向查询订单详情查询开始，{}", url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", FinhubAdminTokenUtil.getStereoAdminToken());
        String responseBody = restHttpUtils.get(url.toString(), httpHeaders, new HashMap<>());
        log.info("外卖正向查询订单详情结束，返回数据为{}", responseBody);
        return analyzeResponse(responseBody);
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
        Integer code = ObjUtils.toInteger(obj.get("code"));
        if (code != 0) {
            throw new FinhubException(code, ObjUtils.toString(obj.get("msg")));
        }
        return obj;
    }

    private void setThirdInfo(Map<String, Object> result, String companyId, Map userInfo, Map saasInfo) {
        try {
            Map<String, Object> thirdInfo = Maps.newHashMap();
            String userId = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(userInfo.get("id"));
            CommonInfoReqDTO req = new CommonInfoReqDTO();
            req.setCompanyId(companyId);
            req.setType(IdTypeEnums.FB_ID.getKey());
            req.setBusinessType(IdBusinessTypeEnums.EMPLOYEE.getKey());
            req.setIdList(Lists.newArrayList(userId));
            List<CommonInfoResDTO> employeeList = commonService.queryCommonInfoByType(req);
            if (!ObjectUtils.isEmpty(employeeList)) {
                thirdInfo.put("user_id", employeeList.get(0).getThirdId());
            }

            if(!ObjectUtils.isEmpty(userInfo.get("unit_id"))){
                String deptId = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(userInfo.get("unit_id"));
                req.setType(IdTypeEnums.FB_ID.getKey());
                req.setBusinessType(IdBusinessTypeEnums.ORG.getKey());
                req.setIdList(Lists.newArrayList(deptId));
                List<CommonInfoResDTO> orgUnitList = commonService.queryCommonInfoByType(req);
                if (!ObjectUtils.isEmpty(orgUnitList)) {
                    thirdInfo.put("dept_id", orgUnitList.get(0).getThirdId());
                }
            }

            String applyId = saasInfo == null ? null : com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(saasInfo.get("applyId"));
            if (!ObjectUtils.isEmpty(applyId)) {
                //加载审批单信息
                SaasApplyCustomFieldRespDTO applyInfo = commonApplyService.getApplyCustomFields(companyId, applyId);
                String thirdId = applyInfo.getThirdId();
                thirdInfo.put("apply_id", thirdId);
            }

            //设置三方费用归属信息
            List<Map<String,Object>> costAttributionList =saasInfo == null ? null :JsonUtils.toObj(com.fenbeitong.openapi.plugin.util.JsonUtils.toJson(saasInfo.get("costAttributionList")),List.class);
            if(!ObjectUtils.isEmpty(costAttributionList)){
                Map<Integer, List<Map<String, Object>>> categoryMap = costAttributionList.stream().collect(Collectors.groupingBy(c -> NumericUtils.obj2int(c.get("costAttributionType"))));
                for(Map.Entry<Integer, List<Map<String, Object>>> entry:categoryMap.entrySet()){
                    Integer category = entry.getKey();
                    String  costCategoryKey = category== 1 ? "cost_dept_id" : "cost_project_id";
                    List<Map<String, Object>> costListByGroup = entry.getValue();
                    List<String> idList = costListByGroup.stream().map(c -> com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(c.get("costAttributionId"))).collect(Collectors.toList());
                    List<CommonIdDTO> idDtoList = ObjectUtils.isEmpty(idList) ? null : commonService.queryIdDTO(companyId, idList, 1, category);
                    Map<String, CommonIdDTO> commonIdDtoMap = ObjectUtils.isEmpty(idDtoList) ? null : idDtoList.stream().collect(Collectors.toMap(CommonIdDTO::getId, Function.identity()));
                    if(!ObjectUtils.isEmpty(commonIdDtoMap)){
                        idList.forEach(id->{
                            CommonIdDTO commonIdDto = commonIdDtoMap.get(id);
                            String thirdId = commonIdDto == null ? null : commonIdDto.getThirdId();
                            if(!StringUtils.isBlank(thirdId)){
                                thirdInfo.put(costCategoryKey,thirdId);
                            }
                        });
                    }
                }
            }
            if(!ObjectUtils.isEmpty(thirdInfo)){
                result.put("third_info", thirdInfo);
            }
        } catch (Exception e) {
            log.info(">>>外卖订单获取第三方信息接口调用时异常:{}>>>", e);
        }
    }
    private void dateValidate(TakeawayOrderListReqDTO req){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //对外卖的日期格式进行校验
            if (!StringUtils.isEmpty(req.getCreateTimeBegin())) {
                if(!ObjectUtils.isEmpty(sdf.parse(req.getCreateTimeBegin()))){
                    req.setCreateTimeBegin(sdf.format(sdf.parse(req.getCreateTimeBegin())));
                }else{
                    throw new FinhubException(-999, "日期格式错误");
                }
            }
            if(!StringUtils.isEmpty(req.getCreateTimeEnd())){
                if(!ObjectUtils.isEmpty(sdf.parse(req.getCreateTimeEnd()))){
                    req.setCreateTimeEnd(sdf.format(sdf.parse(req.getCreateTimeEnd())));
                } else{
                    throw new FinhubException(-999, "日期格式错误");
                }
            }
        }catch(Exception e){
            log.warn("外卖日期格式校验错误");
            throw new FinhubException(-999, "日期格式错误");
        }
    }

    private Map<String,Object> getCostDetailByOrderId( String orderId ){
        Map<String,Object> result = new HashMap<>();
        try {
            List<CostInfoResult> costInfoResultList = orderCostService.queryCostInfoListByOrderId(orderId);
            if (CollectionUtils.isNotBlank(costInfoResultList)){
                String costCategory = costInfoResultList.get(0).getCostCategory();
                result.put("costCategory",costCategory);
            }
        } catch (Exception e){
            log.info("获取订单费控信息失败 {}",e.getMessage());
        }
        return result;
    }

}
