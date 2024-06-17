package com.fenbeitong.openapi.plugin.func.order.service;

import com.fenbeitong.finhub.common.validation.dto.BaseTimeRangeDTO;
import com.fenbeitong.noc.api.service.base.BasePageResDTO;
import com.fenbeitong.noc.api.service.base.BasePageVO;
import com.fenbeitong.noc.api.service.meishi.model.RefundSearchQueryReqDTO;
import com.fenbeitong.noc.api.service.meishi.model.SearchQueryReqDTO;
import com.fenbeitong.noc.api.service.meishi.model.vo.AttributionVO;
import com.fenbeitong.noc.api.service.meishi.model.vo.stereo.OrderStereoVO;
import com.fenbeitong.noc.api.service.meishi.model.vo.stereo.RefundStereoVO;
import com.fenbeitong.noc.api.service.meishi.service.IMeishiOrderSearchService;
import com.fenbeitong.openapi.plugin.func.order.constant.MeiShiBizType;
import com.fenbeitong.openapi.plugin.func.order.constant.MeiShiStatus;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.dto.SaasApplyCustomFieldRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenCostAttributionDTO;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.util.*;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: FuncMeiShiOrderServiceImpl</p>
 * <p>Description: 美食订单开放</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/1 4:58 PM
 */
@SuppressWarnings("all")
@Slf4j
@ServiceAspect
@Service
public class FuncMeiShiOrderServiceImpl extends AbstractOrderService {

    @DubboReference(check = false)
    private IMeishiOrderSearchService meishiOrderSearchService;

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

    public MeiShiOrderListRespDTO listOrder(MeiShiOrderListReqDTO req) {
        SearchQueryReqDTO queryReq = new SearchQueryReqDTO();
        queryReq.setOrderId(req.getOrderId());
        queryReq.setCompanyId(req.getCompanyId());
        queryReq.setUserName(req.getUserName());
        queryReq.setUserPhone(req.getUserPhone());
        queryReq.setStatusList(req.getStatusList());
        queryReq.setOrderType(req.getOrderType());
        if (!ObjectUtils.isEmpty(req.getCreateTimeBegin()) || !ObjectUtils.isEmpty(req.getCreateTimeEnd())) {
            BaseTimeRangeDTO timeRange = getBaseTimeRange(req.getCreateTimeBegin(), req.getCreateTimeEnd());
            queryReq.setBuyTime(timeRange);
        }
        BasePageVO basePageVO = new BasePageVO();
        basePageVO.setPage(req.getPageIndex());
        basePageVO.setPageSize(req.getPageSize());
        queryReq.setPageInfo(basePageVO);
        BasePageResDTO<OrderStereoVO> result = null;
        try {
            result = meishiOrderSearchService.pageSearch(queryReq);
        } catch (Exception e) {
            log.info(">>>美食订单列表查询接口>>>{}调用时异常", "IMeishiOrderSearchService.pageSearch(SearchQueryReqDTO searchQueryReqDTO)");
        }
        return result == null ? null : ObjectUtils.isEmpty(result.getList()) ? null : buildMeiShiOrderListResp(result);
    }

    private MeiShiOrderListRespDTO buildMeiShiOrderListResp(BasePageResDTO<OrderStereoVO> result) {
        PageInfoDTO pageInfo = getPageInfo(result.getPageInfo());
        MeiShiOrderListRespDTO resp = new MeiShiOrderListRespDTO();
        resp.setTotalCount(pageInfo.getTotalSize());
        resp.setTotalPages(pageInfo.getTotalPages());
        resp.setPageIndex(pageInfo.getCurrentPage());
        resp.setPageSize(pageInfo.getPageSize());
        resp.setOrderList(result.getList().stream().map(this::buildMeiShiOrder).collect(Collectors.toList()));
        return resp;
    }

    @SuppressWarnings("unchecked")
    private MeiShiOrderDTO buildMeiShiOrder(OrderStereoVO stereoOrder) {
        MeiShiOrderDTO meiShiOrder = new MeiShiOrderDTO();
        //订单信息
        MeiShiOrderInfoDTO meiShiOrderInfo = new MeiShiOrderInfoDTO();
        meiShiOrderInfo.setBizType(MeiShiBizType.getByKey(stereoOrder.getBizType()).getValue());
        //创建时间
        meiShiOrderInfo.setCreateTime(DateUtils.toSimpleStr(stereoOrder.getCreateTime()));
        //支付时间
        meiShiOrderInfo.setPayTime(DateUtils.toSimpleStr(stereoOrder.getPayTime()));
        meiShiOrderInfo.setGoodsName(stereoOrder.getGoodsName());
        meiShiOrderInfo.setOrderId(stereoOrder.getOrderId());
        //退款状态
        meiShiOrderInfo.setRefundStatus(stereoOrder.getRefundStatus());
        meiShiOrderInfo.setRefundStatusName(MeiShiStatus.getByKey(stereoOrder.getRefundStatus()).getValue());
        meiShiOrderInfo.setShopName(stereoOrder.getShopName());
        //订单状态
        meiShiOrderInfo.setStatus(stereoOrder.getStatus());
        meiShiOrderInfo.setStatusName(stereoOrder.getStatusName());
        meiShiOrderInfo.setSupplierName("美团");
        meiShiOrderInfo.setSupplierOrderId(stereoOrder.getSupplierOrderId());
        meiShiOrder.setOrderInfo(meiShiOrderInfo);
        //用户信息
        OrderUserInfo orderUserInfo = new OrderUserInfo();
        orderUserInfo.setId(stereoOrder.getUserId());
        orderUserInfo.setName(stereoOrder.getUserName());
        orderUserInfo.setPhone(stereoOrder.getUserPhone());
        orderUserInfo.setUserUnitId(stereoOrder.getUserUnitId());
        orderUserInfo.setUnitName(stereoOrder.getUserUnitName());
        meiShiOrder.setUserInfo(orderUserInfo);
        //金额信息
        OrderPriceInfoDTO priceInfo = new OrderPriceInfoDTO();
        priceInfo.setTotalPrice(stereoOrder.getTotalPrice());
        priceInfo.setCompanyTotalPay(BigDecimalUtils.add(stereoOrder.getCompanyTotalPay(), stereoOrder.getCompanyRedcoupon()));
        priceInfo.setPersonalTotalPay(stereoOrder.getPersonalTotalPay());
        priceInfo.setCouponAmount(stereoOrder.getDiscountAmount());
        priceInfo.setRedEnvelope(stereoOrder.getCompanyRedcoupon());
        meiShiOrder.setPriceInfo(priceInfo);
        //管控信息
        OrderSaasInfoDTO saasInfo = new OrderSaasInfoDTO();
        List<OrderSaasInfoDTO.CostAttribution> costAttributions = new ArrayList<>();
        List<AttributionVO> attributionList = stereoOrder.getSaasInfo().getAttributionList();
        //管控信息-费用类别
        if ( null != stereoOrder && null != stereoOrder.getCostDetail() ){
            String costCategory = stereoOrder.getCostDetail().getCostCategory();
            String costCategoryId = stereoOrder.getCostDetail().getCostCategoryId();
            saasInfo.setCostCategory(costCategory);
            saasInfo.setCostCategoryCode(costCategoryId);
        }
        attributionList.forEach(attributionVO -> {
            costAttributions.add(OrderSaasInfoDTO.CostAttribution.builder()
                    .costAttributionName(attributionVO.getCostAttributionName())
                    .costAttributionCategory(attributionVO.getCostAttributionType())
                    .costAttributionId(attributionVO.getCostAttributionId())
                    .costAttributionCustomExt(attributionVO.getCustomExt() == null ? null : JsonUtils.toObj(attributionVO.getCustomExt(), List.class))
                    .build());
        });
        saasInfo.setCostAttributionList(costAttributions);
        saasInfo.setApplyId(stereoOrder.getSaasInfo().getApplyId());
        saasInfo.setDuringApplyId(stereoOrder.getSaasInfo().getDuringApplyId());
        saasInfo.setRemark(stereoOrder.getSaasInfo().getRemark());
        saasInfo.setExceedReason(stereoOrder.getSaasInfo().getExceedReason());
        saasInfo.setIsExceed(stereoOrder.getSaasInfo().getIsExceed());
        List<Map<String, Object>> exceedItemObj = JsonUtils.toObj(saasInfo.getExceedItem(), List.class);
        if (!ObjectUtils.isEmpty(exceedItemObj)) {
            StringBuffer exceedItemStr = new StringBuffer();
            exceedItemObj.forEach(item -> exceedItemStr.append(StringUtils.obj2str(item.get("content")) + ";"));
            saasInfo.setExceedItem(exceedItemStr.substring(0, exceedItemStr.length() - 1));
        }
        meiShiOrder.setSaasInfo(saasInfo);
        Map<String, Object> thirdInfo = getThirdInfo(stereoOrder.getCompanyId(), meiShiOrder.getUserInfo(), meiShiOrder.getSaasInfo());
        if (!ObjectUtils.isEmpty(thirdInfo)) {
            meiShiOrder.setThirdInfo(thirdInfo);
        }
        return meiShiOrder;
    }

    public MeiShiOrderDTO detailOrder(String orderId) {
        OrderStereoVO stereoOrder = null;
        try {
            stereoOrder = meishiOrderSearchService.getOrder(orderId);
        } catch (Exception e) {
            log.info(">>>美食订单详情查询接口>>>{}调用时异常", "IMeishiOrderSearchService.getOrder(String orderId)");
        }
        log.info(">>>美食订单详情查询返回数据:{}",JsonUtils.toJson(stereoOrder));
        MeiShiOrderDTO meiShiOrderDTO = stereoOrder == null ? null : buildMeiShiOrder(stereoOrder);
        if (stereoOrder != null) {
            EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(stereoOrder.getUserId(), stereoOrder.getCompanyId());
            if (employeeContract != null) {
                OrderUserInfo orderUserInfo = meiShiOrderDTO.getUserInfo();
                if (orderUserInfo != null) {
                    orderUserInfo.setEmployeeNumber(employeeContract.getEmployee_number());
                }
            }
            Map map= JsonUtils.toObj(JsonUtils.toJson(stereoOrder), Map.class);
            List<OpenCostAttributionDTO> costAttributionDTOS = thirdInfoService.setCostAttribution(stereoOrder.getCompanyId(), null, (Map) MapUtils.getValueByExpress(map, "data:costDetail"));
            if ( null != meiShiOrderDTO.getSaasInfo() ){
                meiShiOrderDTO.getSaasInfo().setCostAttributionDTOList(costAttributionDTOS);
            }
        }
        return meiShiOrderDTO;
    }

    public Object listRefundOrder(MeiShiRefundListReqDTO req) {
        RefundSearchQueryReqDTO queryReq = new RefundSearchQueryReqDTO();
        queryReq.setRefundOrderId(req.getRefundOrderId());
        queryReq.setRefundStatusList(req.getRefundStatusList());
        queryReq.setOrderId(req.getOrderId());
        queryReq.setOrderType(req.getOrderType());
        queryReq.setCompanyId(req.getCompanyId());
        queryReq.setUserName(req.getUserName());
        queryReq.setUserPhone(req.getUserPhone());
        if (!ObjectUtils.isEmpty(req.getCreateTimeBegin()) || !ObjectUtils.isEmpty(req.getCreateTimeEnd())) {
            BaseTimeRangeDTO timeRange = getBaseTimeRange(req.getCreateTimeBegin(), req.getCreateTimeEnd());
            queryReq.setBuyTime(timeRange);
        }
        BasePageVO basePageVO = new BasePageVO();
        basePageVO.setPage(req.getPageIndex());
        basePageVO.setPageSize(req.getPageSize());
        queryReq.setPageInfo(basePageVO);
        BasePageResDTO<RefundStereoVO> result = null;
        try {
            result = meishiOrderSearchService.refundPageSearch(queryReq);
        } catch (Exception e) {
            log.info(">>>美食退款订单列表查询接口>>>{}调用时异常", "IMeishiOrderSearchService.refundPageSearch(RefundSearchQueryReqDTO searchQueryReqDTO)");
        }
        return result == null ? null : ObjectUtils.isEmpty(result.getList()) ? null : buildMeiShiRefundListResp(result, req.getApiVersion());
    }

    private MeiShiRefundListRespDTO buildMeiShiRefundListResp(BasePageResDTO<RefundStereoVO> result, String apiVersion) {
        MeiShiRefundListRespDTO resp = new MeiShiRefundListRespDTO();
        PageInfoDTO pageInfo = getPageInfo(result.getPageInfo());
        pageInfo.setTotalSize(result.getPageInfo().getTotalSize());
        resp.setTotalCount(pageInfo.getTotalSize());
        resp.setTotalPages(pageInfo.getTotalPages());
        resp.setPageIndex(pageInfo.getCurrentPage());
        resp.setPageSize(pageInfo.getPageSize());
        resp.setRefundList(result.getList().stream().map(stereoRefund -> buildMeiShiRefund(stereoRefund, apiVersion)).collect(Collectors.toList()));
        return resp;
    }

    private Object buildMeiShiRefund(RefundStereoVO stereoRefund, String apiVersion) {
        MeiShiRefundDTO meiShiRefund = new MeiShiRefundDTO();
        //退款订单信息
        MeiShiRefundInfoDTO refundInfo = new MeiShiRefundInfoDTO();
        refundInfo.setRefundOrderId(stereoRefund.getRefundOrderId());
        refundInfo.setOrderId(stereoRefund.getOrderId());
        refundInfo.setBizType(MeiShiBizType.getByKey(stereoRefund.getBizType()).getValue());
        //创建时间
        refundInfo.setCreateTime(DateUtils.toSimpleStr(stereoRefund.getCreateTime()));
        //支付时间
        refundInfo.setPayTime(DateUtils.toSimpleStr(stereoRefund.getPayTime()));
        //退款时间
        refundInfo.setRefundTime(DateUtils.toSimpleStr(stereoRefund.getRefundTime()));
        refundInfo.setGoodsName(stereoRefund.getGoodsName());
        refundInfo.setRefundStatus(stereoRefund.getRefundStatus());
        refundInfo.setRefundStatusName(MeiShiStatus.getByKey(stereoRefund.getRefundStatus()).getValue());
        refundInfo.setShopName(stereoRefund.getShopName());
        refundInfo.setSupplierName("美团");
        refundInfo.setSupplierOrderId(stereoRefund.getSupplierRefundOrderId());
        if ("v_1.0".equals(apiVersion)) {
            meiShiRefund.setOrderInfo(refundInfo);
        } else {
            meiShiRefund.setRefundOrderInfo(refundInfo);
        }
        //用户信息
        OrderUserInfo orderUserInfo = new OrderUserInfo();
        orderUserInfo.setId(stereoRefund.getUserId());
        orderUserInfo.setName(stereoRefund.getUserName());
        orderUserInfo.setPhone(stereoRefund.getUserPhone());
        orderUserInfo.setUserUnitId(stereoRefund.getUserUnitId());
        orderUserInfo.setUnitName(stereoRefund.getUserUnitName());
        meiShiRefund.setUserInfo(orderUserInfo);
        //金额信息
        OrderPriceInfoDTO priceInfo = new OrderPriceInfoDTO();
        priceInfo.setTotalPrice(stereoRefund.getTotalPrice().negate());
        priceInfo.setCompanyTotalPay(BigDecimalUtils.add(stereoRefund.getCompanyTotalPay(),stereoRefund.getCompanyRedcoupon()).negate());
        priceInfo.setPersonalTotalPay(stereoRefund.getPersonalTotalPay().negate());
        priceInfo.setRedEnvelope(stereoRefund.getCompanyRedcoupon());
        meiShiRefund.setPriceInfo(priceInfo);
        meiShiRefund.setSaasInfo(new OrderSaasInfoDTO());
        if (stereoRefund.getSaasInfo() != null) {
            //管控信息
            OrderSaasInfoDTO saasInfo = new OrderSaasInfoDTO();
            List<OrderSaasInfoDTO.CostAttribution> costAttributions = new ArrayList<>();
            List<AttributionVO> attributionList = stereoRefund.getSaasInfo().getAttributionList();
            attributionList.forEach(attributionVO -> {
                costAttributions.add(OrderSaasInfoDTO.CostAttribution.builder()
                        .costAttributionName(attributionVO.getCostAttributionName())
                        .costAttributionCategory(attributionVO.getCostAttributionType())
                        .costAttributionId(attributionVO.getCostAttributionId())
                        .costAttributionCustomExt(attributionVO.getCustomExt() == null ? null : JsonUtils.toObj(attributionVO.getCustomExt(), List.class))
                        .build());
            });
            saasInfo.setApplyId(stereoRefund.getSaasInfo().getApplyId());
            saasInfo.setDuringApplyId(stereoRefund.getSaasInfo().getDuringApplyId());
            saasInfo.setCostAttributionList(costAttributions);
            saasInfo.setRemark(stereoRefund.getSaasInfo().getRemark());
            saasInfo.setExceedReason(stereoRefund.getSaasInfo().getExceedReason());
            saasInfo.setIsExceed(stereoRefund.getSaasInfo().getIsExceed());
            List<Map<String, Object>> exceedItemObj = JsonUtils.toObj(saasInfo.getExceedItem(), List.class);
            if (!ObjectUtils.isEmpty(exceedItemObj)) {
                StringBuffer exceedItemStr = new StringBuffer();
                exceedItemObj.forEach(item -> exceedItemStr.append(StringUtils.obj2str(item.get("content")) + ";"));
                saasInfo.setExceedItem(exceedItemStr.substring(0, exceedItemStr.length() - 1));
            }
            meiShiRefund.setSaasInfo(saasInfo);
            Map<String, Object> thirdInfo = getThirdInfo(stereoRefund.getCompanyId(), meiShiRefund.getUserInfo(), meiShiRefund.getSaasInfo());
            if (!ObjectUtils.isEmpty(thirdInfo)) {
                meiShiRefund.setThirdInfo(thirdInfo);
            }
            //管控信息-费用类别
            getCostDetailByOrderId(stereoRefund.getRefundOrderId(),saasInfo);
        }
        return meiShiRefund;
    }

    private void getCostDetailByOrderId( String orderId , OrderSaasInfoDTO saasInfo ){
        try {
            List<CostInfoResult> costInfoResultList = orderCostService.queryCostInfoListByOrderId(orderId);
            if (CollectionUtils.isNotBlank(costInfoResultList)){
                String costCategory = costInfoResultList.get(0).getCostCategory();
                saasInfo.setCostCategory(costCategory);
            }
        } catch (Exception e){
            log.info("获取订单费控信息失败 {}",e.getMessage());
        }
    }

    private PageInfoDTO getPageInfo(BasePageVO basePageVo) {
        PageInfoDTO pageInfo = new PageInfoDTO();
        pageInfo.setCurrentPage(basePageVo.getCurrentPage());
        pageInfo.setPageSize(basePageVo.getPageSize());
        pageInfo.setTotalPages(basePageVo.getTotalPages());
        return pageInfo;
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

    public Object detailRefund(MeiShiRefundDetailReqDTO req) {
        RefundStereoVO stereoRefund = null;
        try {
            stereoRefund = meishiOrderSearchService.getRefund(req.getRefundOrderId());
        } catch (Exception e) {
            log.info(">>>美食退款订单详情查询接口>>>{}调用时异常", "IMeishiOrderSearchService.getRefund(String refundOrderId)");
        }
        MeiShiRefundDTO meiShiRefundDTO = stereoRefund == null ? null : (MeiShiRefundDTO) (buildMeiShiRefund(stereoRefund, req.getApiVersion()));
        if (stereoRefund != null) {
            EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(stereoRefund.getUserId(), stereoRefund.getCompanyId());
            if (employeeContract != null) {
                OrderUserInfo orderUserInfo = meiShiRefundDTO.getUserInfo();
                if (orderUserInfo != null) {
                    orderUserInfo.setEmployeeNumber(employeeContract.getEmployee_number());
                }
            }
            Map map= JsonUtils.toObj(JsonUtils.toJson(stereoRefund), Map.class);
            List<OpenCostAttributionDTO> costAttributionDTOS = thirdInfoService.setCostAttribution(stereoRefund.getCompanyId(), null, (Map) MapUtils.getValueByExpress(map, "data:costDetail"));
            if ( null != stereoRefund.getSaasInfo() ){
                meiShiRefundDTO.getSaasInfo().setCostAttributionDTOList(costAttributionDTOS);
            }
        }
        return meiShiRefundDTO;
    }

    private Map<String, Object> getThirdInfo(String companyId, OrderUserInfo userInfo, OrderSaasInfoDTO saasInfo) {
        try {
            Map<String, Object> thirdInfo = Maps.newHashMap();
            String userId = userInfo.getId();
            CommonInfoReqDTO req = new CommonInfoReqDTO();
            req.setCompanyId(companyId);
            req.setType(IdTypeEnums.FB_ID.getKey());
            req.setBusinessType(IdBusinessTypeEnums.EMPLOYEE.getKey());
            req.setIdList(Lists.newArrayList(userId));
            List<CommonInfoResDTO> employeeList = commonService.queryCommonInfoByType(req);
            if (!ObjectUtils.isEmpty(employeeList)) {
                thirdInfo.put("user_id", employeeList.get(0).getThirdId());
            }

            String deptId = userInfo.getUserUnitId();
            req.setType(IdTypeEnums.FB_ID.getKey());
            req.setBusinessType(IdBusinessTypeEnums.ORG.getKey());
            req.setIdList(Lists.newArrayList(deptId));
            List<CommonInfoResDTO> orgUnitList = commonService.queryCommonInfoByType(req);
            if (!ObjectUtils.isEmpty(orgUnitList)) {
                thirdInfo.put("dept_id", orgUnitList.get(0).getThirdId());
            }

            String applyId = saasInfo.getApplyId();
            String duringApplyId = saasInfo.getDuringApplyId();
            if (!ObjectUtils.isEmpty(applyId)) {
                //加载审批单信息
                SaasApplyCustomFieldRespDTO applyInfo = commonApplyService.getApplyCustomFields(companyId, applyId);
                String thirdId = applyInfo.getThirdId();
                thirdInfo.put("apply_id", thirdId);
            }
            if (!ObjectUtils.isEmpty(duringApplyId)) {
                //加载审批单信息
                SaasApplyCustomFieldRespDTO applyInfo = commonApplyService.getApplyCustomFields(companyId, duringApplyId);
                String thirdId = applyInfo.getThirdId();
                thirdInfo.put("during_apply_id", thirdId);
            }
            List<OrderSaasInfoDTO.CostAttribution> costAttributionList = saasInfo.getCostAttributionList();
            //设置三方费用归属信息
            if(!ObjectUtils.isEmpty(costAttributionList)){
                Map<Integer, List<OrderSaasInfoDTO.CostAttribution>> categoryMap = costAttributionList.stream().collect(Collectors.groupingBy(c -> c.getCostAttributionCategory()));
                for(Map.Entry<Integer, List<OrderSaasInfoDTO.CostAttribution>> entry:categoryMap.entrySet()){
                    Integer category = entry.getKey();
                    String  costCategoryKey = category== 1 ? "cost_dept_id" : "cost_project_id";
                    List<OrderSaasInfoDTO.CostAttribution> costListByGroup = entry.getValue();
                    List<String> idList = costListByGroup.stream().map(c -> StringUtils.obj2str(c.getCostAttributionId())).collect(Collectors.toList());
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
            return thirdInfo;
        } catch (Exception e) {
            log.info(">>>美食订单获取第三方信息接口调用时异常:{}>>>", e);
            return null;
        }
    }
}
