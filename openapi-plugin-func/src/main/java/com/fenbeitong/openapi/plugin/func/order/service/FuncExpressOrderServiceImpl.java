package com.fenbeitong.openapi.plugin.func.order.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.validation.dto.BaseTimeRangeDTO;
import com.fenbeitong.noc.api.service.base.BasePageResDTO;
import com.fenbeitong.noc.api.service.base.BasePageVO;
import com.fenbeitong.noc.api.service.express.model.dto.req.SearchExpressMainReqRpcDTO;
import com.fenbeitong.noc.api.service.express.model.dto.req.SearchExpressReqRpcDTO;
import com.fenbeitong.noc.api.service.express.model.dto.resq.OrderStereoBaseReqRpcDTO;
import com.fenbeitong.noc.api.service.express.model.dto.resq.OrderStereoDetailReqRpcDTO;
import com.fenbeitong.noc.api.service.express.model.dto.resq.OrderStereoMainDetailRespRpcDTO;
import com.fenbeitong.noc.api.service.express.service.IExpressOrderSearchService;
import com.fenbeitong.noc.api.service.meishi.model.vo.stereo.OrderStereoVO;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.order.dto.ExpressDeliveryOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.ExpressFlashSendOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.ExpressOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.SaasApplyCustomFieldRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.util.*;
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
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncExpressOrderServiceImpl</p>
 * <p>Description: 快递订单开放</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/7/3 4:58 PM
 */
@Slf4j
@ServiceAspect
@Service
public class FuncExpressOrderServiceImpl extends AbstractOrderService {

    private static final Long EXPRESS_MAIN_LIST = 2170l;
    private static final Long EXPRESS_LIST = 2160l;
    private static final Long EXPRESS_MAIN_DETAIL = 2190l;
    private static final Long EXPRESS_DETAIL = 2180l;


    @DubboReference(check = false)
    private IExpressOrderSearchService expressOrderSearchService;

    @Autowired
    private IEtlService etlService;

    @Autowired
    private OpenMsgSetupDao setupDao;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @DubboReference(check = false)
    private ICommonService commonService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private FuncThirdInfoServiceImpl thirdInfoService;

    public Object deliveryListOrder(ExpressDeliveryOrderListReqDTO req) {
        Map<String, Object> results = new HashMap<>();
        try {
            SearchExpressMainReqRpcDTO queryReq = new SearchExpressMainReqRpcDTO();
            queryReq.setOrderId(req.getOrderId());
            queryReq.setCompanyId(req.getCompanyId());
            queryReq.setUserName(req.getUserName());
            queryReq.setUserPhone(req.getUserPhone());
            queryReq.setReceiverPhone(req.getReceiverPhone());
            queryReq.setSenderPhone(req.getSenderPhone());
            queryReq.setStatusList(req.getStatusList());
            if (!ObjectUtils.isEmpty(req.getCreateTimeBegin()) || !ObjectUtils.isEmpty(req.getCreateTimeEnd())) {
                BaseTimeRangeDTO timeRange = getBaseTimeRange(req.getCreateTimeBegin(), req.getCreateTimeEnd());
                queryReq.setCreateTime(timeRange);
            }
            if (!ObjectUtils.isEmpty(req.getReceiverTimeBegin()) || !ObjectUtils.isEmpty(req.getReceiverTimeEnd())) {
                BaseTimeRangeDTO timeRange = getBaseTimeRange(req.getReceiverTimeBegin(), req.getReceiverTimeEnd());
                queryReq.setReceiveTime(timeRange);
            }
            BasePageVO basePageVO = new BasePageVO();
            basePageVO.setPage(ObjUtils.toInteger(req.getPageIndex(), 1));
            basePageVO.setPageSize(ObjUtils.toInteger(req.getPageSize(), 10));
            queryReq.setPageInfo(basePageVO);
            BasePageResDTO<OrderStereoMainDetailRespRpcDTO> resultMainData = expressOrderSearchService.pageMainSearch4Export(queryReq);
            if (resultMainData != null && resultMainData.getList() != null && resultMainData.getList().size() > 0) {
                List<Map> transform = etlService.transform(EXPRESS_MAIN_LIST, JsonUtils.toObj(JsonUtils.toJson(resultMainData.getList()), new TypeReference<List<Map<String, Object>>>() {
                }));
                results.put("results", transform);
                results.put("page_index", resultMainData.getPageInfo().getCurrentPage());
                results.put("page_size", resultMainData.getPageInfo().getPageSize());
                results.put("total_pages", resultMainData.getPageInfo().getTotalPages());
                results.put("total_count", resultMainData.getPageInfo().getTotalSize());
            }
        } catch (Exception e) {
            log.warn(">>>快递查询列表查询接口>>>{}调用时异常", e);
        }
        return results;
    }

    public Object flashSendListOrder(ExpressFlashSendOrderListReqDTO req) {
        Map<String, Object> results = new HashMap<>();
        try {
            SearchExpressReqRpcDTO queryReq = new SearchExpressReqRpcDTO();
            queryReq.setOrderId(req.getOrderId());
            queryReq.setCompanyId(req.getCompanyId());
            queryReq.setUserName(req.getUserName());
            queryReq.setUserPhone(req.getUserPhone());
            queryReq.setStatusList(req.getStatusList());
            if (!ObjectUtils.isEmpty(req.getCreateTimeBegin()) || !ObjectUtils.isEmpty(req.getCreateTimeEnd())) {
                BaseTimeRangeDTO timeRange = getBaseTimeRange(req.getCreateTimeBegin(), req.getCreateTimeEnd());
                queryReq.setBuyTime(timeRange);
            }
            BasePageVO basePageVO = new BasePageVO();
            basePageVO.setPage(ObjUtils.toInteger(req.getPageIndex(), 1));
            basePageVO.setPageSize(ObjUtils.toInteger(req.getPageSize(), 10));
            queryReq.setPageInfo(basePageVO);
            BasePageResDTO<OrderStereoVO> result = null;
            queryReq.getPageInfo().getPageSize();
            BasePageResDTO<OrderStereoBaseReqRpcDTO> resultData = expressOrderSearchService.pageSearch(queryReq);
            if (resultData != null && resultData.getList() != null && resultData.getList().size() > 0) {
                List<Map> transform = etlService.transform(EXPRESS_LIST, JsonUtils.toObj(JsonUtils.toJson(resultData.getList()), new TypeReference<List<Map<String, Object>>>() {
                }));
                results.put("results", transform);
                results.put("page_index", resultData.getPageInfo().getCurrentPage());
                results.put("page_size", resultData.getPageInfo().getPageSize());
                results.put("total_pages", resultData.getPageInfo().getTotalPages());
                results.put("total_count", resultData.getPageInfo().getTotalSize());
            }
        } catch (Exception e) {
            log.warn(">>>闪送查询列表查询接口>>>{}调用时异常", e);
        }
        return results;
    }


    public Object deliveryDetailOrder(ExpressOrderDetailReqDTO req,String companyId) {
        try {
            OrderStereoMainDetailRespRpcDTO mainOrderDetail = expressOrderSearchService.getMainOrderDetail(req.getOrderId());
            log.info("查询快递订单详情返回数据:{}",JsonUtils.toJson(mainOrderDetail));
            Map map= etlService.transform(EXPRESS_MAIN_DETAIL, JsonUtils.toObj(JsonUtils.toJson(mainOrderDetail), Map.class));
            Map<String, Object> orderInfo = null;
            if(map!=null) {
                EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(mainOrderDetail.getUserId(), mainOrderDetail.getCompanyId());
                if (employeeContract != null) {
                    orderInfo = (Map) map.get("user_info");
                    if (orderInfo != null) {
                        orderInfo.put("employee_number", employeeContract.getEmployee_number());
                    }
                }
                Map<String, Object> saasInfo = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(map, "saas_info")),Map.class);
                Map<String, Object> userInfo = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(map, "user_info")),Map.class);
                saasInfo.put("cost_attribution_list",saasInfo.get("attribution_list"));
                setThirdInfo(map,companyId,userInfo,saasInfo);
                thirdInfoService.setCostAttribution(companyId, map, (Map) MapUtils.getValueByExpress(JsonUtils.toObj(JsonUtils.toJson(mainOrderDetail),Map.class), "data:costDetail"));
                Map<String, Object> priceInfo = (Map) map.get("price_info");
                if(!ObjectUtils.isEmpty(priceInfo)){
                    priceInfo.put("company_total_pay",BigDecimalUtils.add(mainOrderDetail.getCompanyTotalPay(),mainOrderDetail.getCompanyRedcoupon()));
                }
            }
            return map;
        } catch (Exception e) {
            log.warn(">>>快递查询详情查询接口>>>{}调用时异常", e);
        }
        return null;
    }

    public Object flashSendDetailOrder(ExpressOrderDetailReqDTO req,String companyId) {
        try {
            OrderStereoDetailReqRpcDTO orderDetail = expressOrderSearchService.getOrderDetail(req.getOrderId());
            log.info("查询闪送订单详情数据：{}",JsonUtils.toJson(orderDetail));
            Map map=etlService.transform(EXPRESS_DETAIL, JsonUtils.toObj(JsonUtils.toJson(orderDetail), Map.class));
            Map<String, Object> orderInfo = null;
            if(map!=null) {
                EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(orderDetail.getUserId(),
                        orderDetail.getCompanyId());
                if (employeeContract != null) {
                    orderInfo = (Map) map.get("user_info");
                    if (orderInfo != null) {
                        orderInfo.put("employee_number", employeeContract.getEmployee_number());
                    }
                }
                Map<String, Object> saasInfo = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(map, "saas_info")),Map.class);
                Map<String, Object> userInfo = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(map, "user_info")),Map.class);
                setThirdInfo(map,companyId,userInfo,saasInfo);

                thirdInfoService.setCostAttribution(companyId, map, (Map) MapUtils.getValueByExpress(JsonUtils.toObj(JsonUtils.toJson(orderDetail),Map.class), "data:costDetail"));
                Map<String, Object> priceInfo = (Map) map.get("price_info");
                if(!ObjectUtils.isEmpty(priceInfo)){
                    // 计算因公-企业支付总金额
                    priceInfo.put("company_total_pay",BigDecimalUtils.add(orderDetail.getCompanyAccountPayTrue(),orderDetail.getCompanyRedcoupon()));
                }
            }
            return map;
        } catch (Exception e) {
            log.warn(">>>闪送查询详情查询接口>>>{}调用时异常", e);
        }
        return null;
    }


    private BaseTimeRangeDTO getBaseTimeRange(String createTimeBegin, String createTimeEnd) {
        BaseTimeRangeDTO timeRange = new BaseTimeRangeDTO();
        timeRange.setStart(DateUtils.toDate(createTimeBegin));
        Date endDate = DateUtils.toDate(createTimeEnd);
        if (endDate != null) {
            endDate = DateUtils.addDay(endDate, 1);
            endDate = new Date(endDate.getTime() - 1000);
            timeRange.setEnd(endDate);
        }
        return timeRange;
    }
    private void setThirdInfo(Map<String, Object> result, String companyId,Map<String,Object> userInfo,Map<String,Object> saasInfo) {
        try {
            Map<String, Object> thirdInfo = Maps.newHashMap();
            String userId = StringUtils.obj2str(userInfo.get("id"));
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
                String deptId = StringUtils.obj2str(userInfo.get("unit_id"));
                req.setType(IdTypeEnums.FB_ID.getKey());
                req.setBusinessType(IdBusinessTypeEnums.ORG.getKey());
                req.setIdList(Lists.newArrayList(deptId));
                List<CommonInfoResDTO> orgUnitList = commonService.queryCommonInfoByType(req);
                if (!ObjectUtils.isEmpty(orgUnitList)) {
                    thirdInfo.put("dept_id", orgUnitList.get(0).getThirdId());
                }
            }

            String applyId = saasInfo == null ? null : StringUtils.obj2str(saasInfo.get("apply_id"));
            if (!ObjectUtils.isEmpty(applyId)) {
                //加载审批单信息
                SaasApplyCustomFieldRespDTO applyInfo = commonApplyService.getApplyCustomFields(companyId, applyId);
                String thirdId = applyInfo.getThirdId();
                thirdInfo.put("apply_id", thirdId);
            }

            //设置三方费用归属信息
            List<Map<String,Object>> costAttributionList =saasInfo == null ? null : JsonUtils.toObj(JsonUtils.toJson(saasInfo.get("cost_attribution_list")),List.class);
            if(!ObjectUtils.isEmpty(costAttributionList)){
                Map<Integer, List<Map<String, Object>>> categoryMap = costAttributionList.stream().collect(Collectors.groupingBy(c -> NumericUtils.obj2int(c.get("cost_attribution_category"))));
                for(Map.Entry<Integer, List<Map<String, Object>>> entry:categoryMap.entrySet()){
                    Integer category = entry.getKey();
                    String  costCategoryKey = category== 1 ? "cost_dept_id" : "cost_project_id";
                    List<Map<String, Object>> costListByGroup = entry.getValue();
                    List<String> idList = costListByGroup.stream().map(c -> StringUtils.obj2str(c.get("cost_attribution_id"))).collect(Collectors.toList());
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
            log.info(">>>闪送订单获取第三方信息接口调用时异常:{}>>>", e);
        }
    }

}
