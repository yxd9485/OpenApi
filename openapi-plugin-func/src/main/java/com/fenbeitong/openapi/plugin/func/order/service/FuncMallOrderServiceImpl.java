package com.fenbeitong.openapi.plugin.func.order.service;

import com.fenbeitong.openapi.plugin.support.apply.dto.SaasApplyCustomFieldRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenCostAttributionDTO;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoReqDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonInfoResDTO;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.noc.api.service.mall.model.dto.req.MallOrderStereoAfterReqRpcDTO;
import com.fenbeitong.noc.api.service.mall.model.dto.req.MallOrderStereoReqRpcDTO;
import com.fenbeitong.noc.api.service.mall.model.dto.resp.MallOrderStereoAfterListResRpcDTO;
import com.fenbeitong.noc.api.service.mall.model.dto.resp.MallOrderStereoListResRpcDTO;
import com.fenbeitong.noc.api.service.mall.model.vo.MallOrderStereoAfterListBaseVO;
import com.fenbeitong.noc.api.service.mall.model.vo.MallOrderStereoListBaseVO;
import com.fenbeitong.noc.api.service.mall.service.IMallOrderSearchService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.order.dto.BaseOrderListRespDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.MallOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.MallRefundDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.MallRefundListReqDTO;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncMallOrderServiceImpl</p>
 * <p>Description: 采购订单服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/15 4:02 PM
 */
@SuppressWarnings("all")
@ServiceAspect
@Service
@Slf4j
public class FuncMallOrderServiceImpl extends AbstractOrderService {

    @DubboReference(check = false)
    private IMallOrderSearchService mallOrderSearchService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Autowired
    private IEtlService etlService;

    @DubboReference(check = false)
    private ICommonService commonService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private FuncThirdInfoServiceImpl thirdInfoService;

    public Object listOrder(MallOrderListReqDTO req) {
        MallOrderStereoReqRpcDTO stereoReq = new MallOrderStereoReqRpcDTO();
        stereoReq.setPageIndex(req.getPageIndex());
        stereoReq.setPageSize(req.getPageSize());
        stereoReq.setCompanyId(req.getCompanyId());
        stereoReq.setOrderId(req.getOrderId());
        stereoReq.setCreateBegin(DateUtils.toSimpleStr(DateUtils.toDate(req.getCreateTimeBegin())));
        Date endDate = DateUtils.toDate(req.getCreateTimeEnd());
        if (endDate != null) {
            endDate = DateUtils.addDay(endDate, 1);
            endDate = new Date(endDate.getTime() - 1000);
            stereoReq.setCreateEnd(DateUtils.toSimpleStr(endDate));
        }
        stereoReq.setFinishBegin(DateUtils.toSimpleStr(DateUtils.toDate(req.getFinishTimeBegin())));
        Date finishEndDate = DateUtils.toDate(req.getFinishTimeEnd());
        if (finishEndDate != null) {
            finishEndDate = DateUtils.addDay(finishEndDate, 1);
            finishEndDate = new Date(finishEndDate.getTime() - 1000);
            stereoReq.setFinishEnd(DateUtils.toSimpleStr(finishEndDate));
        }
        stereoReq.setOrderStatus(req.getStatus());
        stereoReq.setBookingPerson(req.getUserName());
        stereoReq.setBookingPhone(req.getUserPhone());
        stereoReq.setConsigneeName(req.getConsigneeName());
        stereoReq.setConsigneePhone(req.getConsigneePhone());
        stereoReq.setOrderType(req.getOrderType());
        MallOrderStereoListResRpcDTO result = null;
        try {
            result = mallOrderSearchService.mallOrderStereoList(stereoReq);
        } catch (Exception e) {
            log.info(">>>采购订单列表查询接口>>>{}调用时异常", "IMallOrderSearchService.mallOrderStereoList(MallOrderStereoReqRpcDTO dto)");
        }
        int totalCount = result == null ? 0 : Optional.ofNullable(result.getTotalCount()).orElse(0L).intValue();
        BaseOrderListRespDTO resp = new BaseOrderListRespDTO();
        resp.setResults(Lists.newArrayList());
        resp.setTotalCount(totalCount);
        resp.setPageIndex(req.getPageIndex());
        resp.setPageSize(req.getPageSize());
        if (totalCount > 0) {
            List dataList = JsonUtils.toObj(JsonUtils.toJson(result.getResults()), List.class);
            List transferList = etlService.transform(2210L, dataList);
            resp.setResults(transferList);
            return resp;
        }
        return Maps.newHashMap();
    }

    public MallOrderStereoListBaseVO mallDetailOrder(String orderId) {
        MallOrderStereoListBaseVO result = null;
        try {
            result = mallOrderSearchService.mallOrderStereoDetail(null, null, orderId);
        } catch (Exception e) {
            log.info(">>>采购订单详情查询接口>>>{}调用时异常", "IMallOrderSearchService.mallOrderStereoDetail(String userId, String companyId, String orderId)");
        }
        return result;
    }

    public Object detailOrder(String orderId, String companyId) {
        return detailOrder(orderId, companyId, null);
    }

    public Object detailOrder(String orderId, String companyId, Consumer<MallOrderStereoListBaseVO> consumer) {
        MallOrderStereoListBaseVO result = mallDetailOrder(orderId);
        if (result == null) {
            return Maps.newHashMap();
        }
        if (Objects.nonNull(consumer)) {
            consumer.accept(result);
        }
        log.info(">>>采购订单详情查询接口返回数据：{}>>>",JsonUtils.toJson(result));
        Map data = JsonUtils.toObj(JsonUtils.toJson(result), Map.class);
        Map map=etlService.transform(2210L, data);
        Map<String, Object> orderInfo = null;
        if(map!=null) {
            EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(result.getBookingPerson().getId(), result.getCompany().getId());
            if (employeeContract != null) {
                orderInfo = (Map) map.get("user_info");
                if (orderInfo != null) {
                    orderInfo.put("employee_number", employeeContract.getEmployee_number());
                }
            }
            setThirdInfo(map,companyId, orderInfo, (Map)data.get("saasInfo"));
        }
        thirdInfoService.setCostAttribution(companyId, map, (Map) MapUtils.getValueByExpress(data, "data:costDetail"));
        return map;
    }

    public Object listRefundOrder(MallRefundListReqDTO req) {
        MallOrderStereoAfterReqRpcDTO afterReq = new MallOrderStereoAfterReqRpcDTO();
        afterReq.setPageIndex(req.getPageIndex());
        afterReq.setPageSize(req.getPageSize());
        afterReq.setCompanyId(req.getCompanyId());
        afterReq.setServiceOrderId(req.getRefundOrderId());
        afterReq.setOrderId(req.getOrderId());
        afterReq.setCreateBegin(DateUtils.toSimpleStr(DateUtils.toDate(req.getCreateTimeBegin())));
        Date endDate = DateUtils.toDate(req.getCreateTimeEnd());
        if (endDate != null) {
            endDate = DateUtils.addDay(endDate, 1);
            endDate = new Date(endDate.getTime() - 1000);
            afterReq.setCreateEnd(DateUtils.toSimpleStr(endDate));
        }
        afterReq.setFinishBegin(DateUtils.toSimpleStr(DateUtils.toDate(req.getFinishTimeBegin())));
        Date finishEndDate = DateUtils.toDate(req.getFinishTimeEnd());
        if (finishEndDate != null) {
            finishEndDate = DateUtils.addDay(finishEndDate, 1);
            finishEndDate = new Date(finishEndDate.getTime() - 1000);
            afterReq.setFinishEnd(DateUtils.toSimpleStr(finishEndDate));
        }
        afterReq.setBookingPerson(req.getUserName());
        afterReq.setBookingPhone(req.getUserPhone());
        afterReq.setStep(req.getStep());
        afterReq.setOrderType(req.getOrderType());
        MallOrderStereoAfterListResRpcDTO result = null;
        try {
            result = mallOrderSearchService.mallOrderStereoAfterList(afterReq);
        } catch (Exception e) {
            log.info(">>>采购售后单列表查询接口>>>{}调用时异常", "IMallOrderSearchService.mallOrderStereoAfterList(MallOrderStereoAfterReqRpcDTO req)");
        }
        int totalCount = result == null ? 0 : Optional.ofNullable(result.getTotalCount()).orElse(0L).intValue();
        BaseOrderListRespDTO resp = new BaseOrderListRespDTO();
        resp.setResults(Lists.newArrayList());
        resp.setTotalCount(totalCount);
        resp.setPageIndex(req.getPageIndex());
        resp.setPageSize(req.getPageSize());
        if (totalCount > 0) {
            List dataList = JsonUtils.toObj(JsonUtils.toJson(result.getResults()), List.class);
            List<Map> transferList = etlService.transform(2220L, dataList);
            if (!ObjectUtils.isEmpty(transferList) && "v_1.0".equals(req.getApiVersion())) {
                transferList.forEach(rowMap -> {
                    rowMap.put("order_info", rowMap.get("refund_order_info"));
                    rowMap.remove("refund_order_info");
                });
            }
            resp.setResults(transferList);
        }
        if (totalCount == 0){

            return Maps.newHashMap();
        }

        return resp;
    }

    public Object detailRefund(MallRefundDetailReqDTO req,String companyId) {
        MallOrderStereoAfterListBaseVO result = null;
        try {
            result = mallOrderSearchService.mallOrderStereoAfterDetail(req.getRefundOrderId());
        } catch (Exception e) {
            log.info(">>>采购售后单详情查询接口>>>{}调用时异常", "IMallOrderSearchService.mallOrderStereoAfterDetail(String refundOrderId)");
        }
        log.info("查询采购售后单详情数据返回:{}",JsonUtils.toJson(result));
        if (result == null) {
            return Maps.newHashMap();
        }
        Map data = JsonUtils.toObj(JsonUtils.toJson(result), Map.class);
        Map transformMap = etlService.transform(2220L, data);
        Map<String, Object> orderInfo =null;
        if(transformMap!=null) {
            EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(result.getBookingPerson().getId(), result.getCompany().getId());
            if (employeeContract != null) {
                orderInfo = (Map) transformMap.get("user_info");
                if (orderInfo != null) {
                    orderInfo.put("employee_number", employeeContract.getEmployee_number());
                }
            }
            Map saasInfo = Maps.newHashMap();
            saasInfo.put("duringApplyId",result.getDuringApplyId());
            saasInfo.put("costAttributionList",result.getCostAttributionList());
            setThirdInfo(transformMap,companyId, orderInfo, saasInfo);
            Map<String, Object> priceinfo = (Map) transformMap.get("price_info");
            if(!ObjectUtils.isEmpty(priceinfo)){
             priceinfo.put("company_total_pay",BigDecimalUtils.add(result.getCompanyTotalPay(),result.getRedcouponPay()));
            }

        }
        List<OpenCostAttributionDTO> costAttributionDTOS = thirdInfoService.setCostAttribution(companyId, transformMap, (Map) MapUtils.getValueByExpress(data, "data:costDetail"));
        if ( null == transformMap.get("saas_info")){
            Map costAttribution = Maps.newHashMap();
            costAttribution.put("cost_attribution",costAttributionDTOS);
            transformMap.put("saas_info",costAttribution);
        } else {
            Map saasInfo = (Map) transformMap.get("saas_info");
            Map costAttribution = Maps.newHashMap();
            saasInfo.put("cost_attribution",costAttributionDTOS);
        }
        if (!ObjectUtils.isEmpty(transformMap) && "v_1.0".equals(req.getApiVersion())) {
            transformMap.put("order_info", transformMap.get("refund_order_info"));
            transformMap.remove("refund_order_info");
        }
        return transformMap;
    }

    private void setThirdInfo(Map<String, Object> result, String companyId, Map userInfo, Map saasInfo) {
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

            String applyId = saasInfo == null ? null : StringUtils.obj2str(saasInfo.get("duringApplyId"));
            if (!ObjectUtils.isEmpty(applyId)) {
                //加载审批单信息
                SaasApplyCustomFieldRespDTO applyInfo = commonApplyService.getApplyCustomFields(companyId, applyId);
                String thirdId = applyInfo.getThirdId();
                thirdInfo.put("apply_id", thirdId);
            }

            //设置三方费用归属信息
            List<Map<String,Object>> costAttributionList =saasInfo == null ? null : JsonUtils.toObj(JsonUtils.toJson(saasInfo.get("costAttributionList")),List.class);
            if(!ObjectUtils.isEmpty(costAttributionList)){
                Map<Integer, List<Map<String, Object>>> categoryMap = costAttributionList.stream().collect(Collectors.groupingBy(c -> NumericUtils.obj2int(c.get("costAttributionType"))));
                for(Map.Entry<Integer, List<Map<String, Object>>> entry:categoryMap.entrySet()){
                    Integer category = entry.getKey();
                    String  costCategoryKey = category== 1 ? "cost_dept_id" : "cost_project_id";
                    List<Map<String, Object>> costListByGroup = entry.getValue();
                    List<String> idList = costListByGroup.stream().map(c -> StringUtils.obj2str(c.get("costAttributionId"))).collect(Collectors.toList());
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
}
