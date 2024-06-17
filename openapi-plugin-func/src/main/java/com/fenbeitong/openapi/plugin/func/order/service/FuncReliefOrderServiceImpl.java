package com.fenbeitong.openapi.plugin.func.order.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanOrderStereoInfoVO;
import com.fenbeitong.noc.api.service.relief.model.dto.req.ReliefOrderStereoListReqRpcDTO;
import com.fenbeitong.noc.api.service.relief.model.dto.resp.ReliefOrderStereoListResRpcDTO;
import com.fenbeitong.noc.api.service.relief.model.vo.ReliefPriceStereoInfoVO;
import com.fenbeitong.noc.api.service.relief.model.vo.ReliefStereoInfoVO;
import com.fenbeitong.noc.api.service.relief.service.IReliefOrderSearchService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.order.dto.AirOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.OrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.ReliefOrderListReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.TrainOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: FuncReliefOrderServiceImpl</p>
 * <p>Description: 减免订单开放</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/8/6 4:58 PM
 */
@Slf4j
@ServiceAspect
@Service
public class FuncReliefOrderServiceImpl extends AbstractOrderService {

    private static final Long RELIEF_LIST = 2330l;
    private static final Long RELIEF_DETAIL = 2330l;


    @DubboReference(check = false)
    private IReliefOrderSearchService reliefOrderSearchService;

    @Autowired
    private FuncThirdInfoServiceImpl thirdInfoService;
    @Autowired
    private FuncHotelOrderServiceImpl hotelOrderService;
    @Autowired
    private FuncCarOrderServiceImpl carOrderService;
    @Autowired
    private FuncAirOrderServiceImpl funcAirOrderService;
    @Autowired
    private FuncTrainOrderServiceImpl trainOrderService;

    @Autowired
    private IEtlService etlService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;


    public Object list(ReliefOrderListReqDTO req) {
        Map<String, Object> results = new HashMap<>();
        try {
            ReliefOrderStereoListReqRpcDTO queryReq = new ReliefOrderStereoListReqRpcDTO();
            queryReq.setOrderId(req.getOrderId());
            queryReq.setCompanyId(req.getCompanyId());
            queryReq.setUserName(req.getUserName());
            queryReq.setUserPhone(req.getUserPhone());
            queryReq.setConsumerName(req.getConsumerName());
            queryReq.setConsumerPhone(req.getConsumerPhone());
            queryReq.setOrderStatus(req.getStatusList());
            if (!ObjectUtils.isEmpty(req.getCreateTimeBegin()) || !ObjectUtils.isEmpty(req.getCreateTimeEnd())) {
                queryReq.setCreateBegin(req.getCreateTimeBegin());
                queryReq.setCreateEnd(req.getCreateTimeEnd());
            }
            queryReq.setPageIndex(ObjUtils.toInteger(req.getPageIndex(), 1));
            queryReq.setPageSize(ObjUtils.toInteger(req.getPageSize(), 10));
            ReliefOrderStereoListResRpcDTO reliefOrderStereoListResRpcDTO = reliefOrderSearchService.stereoList(queryReq);
            if (reliefOrderStereoListResRpcDTO != null && reliefOrderStereoListResRpcDTO.getResults() != null && reliefOrderStereoListResRpcDTO.getResults().size() > 0) {
                List<Map> transform = etlService.transform(RELIEF_LIST, JsonUtils.toObj(JsonUtils.toJson(reliefOrderStereoListResRpcDTO.getResults()), new TypeReference<List<Map<String, Object>>>() {
                }));
                results.put("results", transform);
                results.put("page_index", reliefOrderStereoListResRpcDTO.getPageIndex());
                results.put("page_size", reliefOrderStereoListResRpcDTO.getPageSize());
                results.put("total_pages", (reliefOrderStereoListResRpcDTO.getTotalCount() + queryReq.getPageSize() - 1) / queryReq.getPageSize());
                results.put("total_count", reliefOrderStereoListResRpcDTO.getTotalCount());
            }
        } catch (Exception e) {
            log.warn(">>>减免查询列表查询接口>>>{}调用时异常", e);
        }
        return results;
    }


    public Object detail(OrderDetailReqDTO req) {
        Map<String, Object> result = new HashMap<>();
        try {
             ReliefStereoInfoVO reliefStereoInfoVO = reliefOrderSearchService.stereoDetail(req.getOrderId());
             log.info("减免订单详情查询返回数据:{}",JsonUtils.toJson(reliefStereoInfoVO));
             result = etlService.transform(RELIEF_DETAIL, JsonUtils.toObj(JsonUtils.toJson(reliefStereoInfoVO), Map.class));
             //设置对应的三方信息
            if (!ObjUtils.isEmpty(result)) {
                setThirdInfo(reliefStereoInfoVO, result);

                EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(reliefStereoInfoVO.getOrderStereoInfoVO().getUserId(),
                        reliefStereoInfoVO.getOrderStereoInfoVO().getCompanyId());
                if (employeeContract != null) {
                    Map<String, Object> orderInfo = (Map) result.get("user_info");
                    if (orderInfo != null) {
                        orderInfo.put("employee_number", employeeContract.getEmployee_number());
                    }
                }
                ReliefPriceStereoInfoVO priceInfo =  reliefStereoInfoVO.getPriceStereoInfoVO();
                Map tarPriceInfo = (Map) MapUtils.getValueByExpress(result, "price_info");

                if (!ObjectUtils.isEmpty(priceInfo) && !ObjectUtils.isEmpty(tarPriceInfo)) {
                    BigDecimal amountCompany = BigDecimalUtils.obj2big(priceInfo.getCompanyAccountPay(), BigDecimal.ZERO);
                    BigDecimal amountRedcoupon = BigDecimalUtils.obj2big(priceInfo.getCompanyRedcoupon(), BigDecimal.ZERO);
                    BigDecimal companyTotalPay = amountCompany.add(amountRedcoupon);
                    tarPriceInfo.put("company_total_pay", companyTotalPay);
                }
            }
            return result;
        } catch (Exception e) {
            log.warn(">>>减免查询详情查询接口>>>{}调用时异常", e);
        }
        return null;
    }

    private void setThirdInfo(ReliefStereoInfoVO reliefStereoInfoVO, Map<String, Object> result) {
        Integer orderCategory = reliefStereoInfoVO.getOrderStereoInfoVO().getOrderType().getKey();
        String companyId = reliefStereoInfoVO.getOrderStereoInfoVO().getCompanyId();
        String remoteOrderId = reliefStereoInfoVO.getOrderStereoInfoVO().getRemoteOrderId();
        String ticketId = reliefStereoInfoVO.getOrderStereoInfoVO().getTicketId();
        Map detailMap = new HashMap();
        switch (orderCategory) {
            case 1: //飞机票
                AirOrderDetailReqDTO airOrderDetailReqDTO = new AirOrderDetailReqDTO();
                airOrderDetailReqDTO.setIsIntl(false);
                airOrderDetailReqDTO.setOrderId(remoteOrderId);
                airOrderDetailReqDTO.setTicketId(ticketId);
                airOrderDetailReqDTO.setCompanyId(companyId);
                detailMap = (Map)funcAirOrderService.detail(airOrderDetailReqDTO);
                if (detailMap.size()<1) {
                    airOrderDetailReqDTO.setIsIntl(true);
                    detailMap = (Map)funcAirOrderService.detail(airOrderDetailReqDTO);
                }
                break;
            case 15: //火车
                TrainOrderDetailReqDTO trainOrderDetailReqDTO = new TrainOrderDetailReqDTO();
                trainOrderDetailReqDTO.setOrderId(remoteOrderId);
                trainOrderDetailReqDTO.setTicketId(ticketId);
                trainOrderDetailReqDTO.setCompanyId(companyId);
                detailMap = (Map)trainOrderService.detail(trainOrderDetailReqDTO);
                break;
            case 11: //酒店
                OrderDetailReqDTO orderDetailReqDTO = new OrderDetailReqDTO();
                orderDetailReqDTO.setOrderId(remoteOrderId);
                orderDetailReqDTO.setCompanyId(companyId);
                detailMap = (Map)hotelOrderService.detail(orderDetailReqDTO);
                break;
            case 3: //用车
                OrderDetailReqDTO detailReqDTO = new OrderDetailReqDTO();
                detailReqDTO.setOrderId(remoteOrderId);
                detailReqDTO.setCompanyId(companyId);
                detailMap = (Map)carOrderService.detail(detailReqDTO);
                break;
        }
        result.put("third_info", detailMap.get("third_info"));
    }

}
