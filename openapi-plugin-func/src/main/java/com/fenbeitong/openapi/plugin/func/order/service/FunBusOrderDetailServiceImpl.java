package com.fenbeitong.openapi.plugin.func.order.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.noc.api.service.base.BasePageResDTO;
import com.fenbeitong.noc.api.service.base.BasePageVO;
import com.fenbeitong.noc.api.service.bus.enums.BusOrderStatusEnum;
import com.fenbeitong.noc.api.service.bus.model.dto.req.SearchOpenBusDetailQueryReqDTO;
import com.fenbeitong.noc.api.service.bus.model.dto.req.SearchOpenBusListQueryReqDTO;
import com.fenbeitong.noc.api.service.bus.model.vo.*;
import com.fenbeitong.noc.api.service.bus.service.IBusOrderSearchService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.order.constant.BusOrderType;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.dto.OrderCostDetailDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.SceneServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.service.IGetEmployeeInfoFromUcService;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeBean;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.model.po.frequent.FrequentContact;
import com.fenbeitong.usercenter.api.service.frequent.IFrequentService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 汽车票详情接口
 * @author zhangpeng
 * @date 2022/3/30 11:20 上午
 */
@Slf4j
@ServiceAspect
@Service
public class FunBusOrderDetailServiceImpl extends AbstractOrderService {

    @DubboReference(check = false)
    private IBusOrderSearchService busOrderSearchService;

    @Autowired
    private SceneServiceImpl sceneService;

    @Autowired
    private IGetEmployeeInfoFromUcService getEmployeeInfoFromUcService;

    @DubboReference(check = false)
    private IFrequentService frequentService;

    private static final Long BUS_DETAIL_CONFIG_ID = 2550L;
    private static final Long BUS_LIST_CONFIG_ID = 2570L;
    private static final Integer DEFAULT_PAGE_SIZE = 30;
    private static final Integer DEFAULT_PAGE_INDEX = 1;

    @Autowired
    private IEtlService etlService;

    public Object getBusOrderList(BusOrderListReqDTO busOrderListReqDTO){
        BaseOrderListRespDTO resp = new BaseOrderListRespDTO();
        resp.setResults(Lists.newArrayList());
        resp.setPageIndex(busOrderListReqDTO.getPageIndex());
        resp.setPageSize(busOrderListReqDTO.getPageSize());
        resp.setTotalCount(0);

        SearchOpenBusListQueryReqDTO reqDTO = new SearchOpenBusListQueryReqDTO();
        BeanUtils.copyProperties(busOrderListReqDTO,reqDTO);
        buildListReqDTO(busOrderListReqDTO, reqDTO);
        if (!StringUtils.isBlank(busOrderListReqDTO.getOrderState())){
            reqDTO.setOrderStatus(Integer.valueOf(busOrderListReqDTO.getOrderState()));
        }
        BasePageVO basePageVO = new BasePageVO();
        basePageVO.setCurrentPage( null == busOrderListReqDTO.getPageIndex() ? DEFAULT_PAGE_INDEX : busOrderListReqDTO.getPageIndex());
        basePageVO.setPageSize( null == busOrderListReqDTO.getPageSize() ? DEFAULT_PAGE_SIZE : busOrderListReqDTO.getPageSize());
        reqDTO.setPageInfo(basePageVO);
        // 汽车票全是因公
        reqDTO.setAccountType(1);
        try {
            List<BusOrderListResDTO> busOrderDetailResDTOs = Lists.newArrayList();
            log.info(">>> 汽车票订单列表查询请求参数 : {} >>>",JsonUtils.toJson(reqDTO));
            BasePageResDTO<OrderBusOpenBaseVO> resDTO = busOrderSearchService.openPageSearch(reqDTO);
            log.info(">>> 汽车票订单列表原始结果 : {} >>>",JsonUtils.toJson(resDTO));
            if ( null == resDTO ){
                log.info("汽车票订单列表返回为空");
                return Lists.newArrayList();
            }
            resp.setTotalCount(resDTO.getPageInfo().getTotalSize());
            List<Map<String,Object>> listData = JsonUtils.toObj(JsonUtils.toJson(resDTO.getList()), new TypeReference<List<Map<String, Object>>>() {
            });
            log.info(">>> listData {} >>>",JsonUtils.toJson(listData));
            List<Map> transformList = etlService.transform(BUS_LIST_CONFIG_ID,listData);
            log.info(">>> 汽车票转换后的etl数据：{}>>>", JsonUtils.toJson(transformList));
            if (CollectionUtils.isNotBlank(transformList)){
                for (Map map : transformList) {
                    BusOrderListResDTO busOrderListResDTO = JsonUtils.toObj(JsonUtils.toJson(map),BusOrderListResDTO.class);
                    busOrderDetailResDTOs.add(busOrderListResDTO);
                }
            }
            resp.setResults(busOrderDetailResDTOs);
            return resp;
        } catch (Exception e){
            log.warn("调用场景获取汽车票订单列表失败 : {} ",e.getMessage());
            return Lists.newArrayList();
        }

    }

    private void buildListReqDTO(BusOrderListReqDTO busOrderListReqDTO, SearchOpenBusListQueryReqDTO reqDTO) {
        reqDTO.setParentOrderId(busOrderListReqDTO.getRootOrderId());
        if (!StringUtils.isBlank(busOrderListReqDTO.getCreateTimeBegin())){
            reqDTO.setCreateTimeStart(DateUtils.toDate(busOrderListReqDTO.getCreateTimeBegin()));
        }
        if (!StringUtils.isBlank(busOrderListReqDTO.getCreateTimeEnd())){
            reqDTO.setCreateTimeEnd(DateUtils.toDate(busOrderListReqDTO.getCreateTimeEnd()));
        }
        if (!StringUtils.isBlank(busOrderListReqDTO.getStartDateFrom())){
            reqDTO.setFromDateTimeStart(DateUtils.toDate(busOrderListReqDTO.getStartDateFrom()));
        }
        if (!StringUtils.isBlank(busOrderListReqDTO.getEndDateFrom())){
            reqDTO.setFromDateTimeEnd(DateUtils.toDate(busOrderListReqDTO.getEndDateFrom()));
        }
        reqDTO.setBusNumberSearch(busOrderListReqDTO.getBusNo());
    }

    public BusOrderDetailResDTO getBusOrderDetail(BusOrderDetailReqDTO busOrderDetailReqDTO){
        SearchOpenBusDetailQueryReqDTO reqDTO = new SearchOpenBusDetailQueryReqDTO();
        BeanUtils.copyProperties(busOrderDetailReqDTO,reqDTO);
        BusOrderDetailResDTO busOrderDetailResDTO = new BusOrderDetailResDTO();
        try {
            log.info("<<< 汽车票详情开始查询 , 参数为 : {} >>>",JsonUtils.toJson(reqDTO));
            OrderBusOpenDetailVO detailVO = busOrderSearchService.openDetailSearch(reqDTO);
            log.info("<<< 汽车票详情查询完毕 , 结果为 : {} >>>",JsonUtils.toJson(detailVO));
            Map<String, Object> data = JsonUtils.toObj(JsonUtils.toJson(detailVO), Map.class);
            Map transformMap = etlService.transform(BUS_DETAIL_CONFIG_ID, data);
            BeanUtils.copyProperties(transformMap,busOrderDetailResDTO);
            busOrderDetailResDTO = JsonUtils.toObj(JsonUtils.toJson(transformMap),BusOrderDetailResDTO.class);
            // 乘客user
            BusPassengerVO passenger = detailVO.getPassenger();
            if (!ObjectUtils.isEmpty(passenger)) {
                List<BusOrderDetailResDTO.User> users = new ArrayList<>();
                    BusOrderDetailResDTO.User user = new BusOrderDetailResDTO.User();
                    user.setId(passenger.getPassengerId());
                    user.setName(passenger.getPassengerName());
                    user.setDepartmentId(passenger.getPassengerUnitId());
                    user.setDepartmentName(passenger.getPassengerUnitName());
                    user.setCertificateNo(passenger.getCertificateNo());
                    user.setCertificateType(passenger.getCertificateType().getKey().toString());
                    FrequentContact frequentContact = frequentService.getById(passenger.getPassengerId());
                    if ( null != frequentContact ){
                        log.info("汽车票乘客信息 {} ",JsonUtils.toJson(frequentContact));
                        user.setPhone(frequentContact.getPhoneNum());
                        String fbtEmployeeId = frequentContact.getSelectedEmployeeId();
                        if (!StringUtils.isBlank(fbtEmployeeId)){
                            ThirdEmployeeRes thirdEmployeeRes = getEmployeeInfoFromUcService.getEmployInfoByEmployeeId(busOrderDetailReqDTO.getCompanyId(),fbtEmployeeId,"0");
                            log.info("汽车票乘客三方信息 : {}",JsonUtils.toJson(thirdEmployeeRes));
                            if ( null != thirdEmployeeRes && null != thirdEmployeeRes.getEmployee() ){
                                ThirdEmployeeBean thirdEmployeeBean = thirdEmployeeRes.getEmployee();
                                user.setThirdId(thirdEmployeeBean.getThirdEmployeeId());
                                user.setThirdDepartmentId(thirdEmployeeBean.getThird_org_id());
                            }
                        }
                    }
                    users.add(user);
                busOrderDetailResDTO.setUsers(users);
            }
            // 下单人信息
            ThirdEmployeeRes thirdEmployeeRes = getEmployeeInfoFromUcService.getEmployInfoByEmployeeId(busOrderDetailReqDTO.getCompanyId(),detailVO.getUserId(),"0");
            BusOrderDetailResDTO.Payer payer = new BusOrderDetailResDTO.Payer();
            if ( null != thirdEmployeeRes && null != thirdEmployeeRes.getEmployee() ){
                ThirdEmployeeBean thirdEmployeeBean = thirdEmployeeRes.getEmployee();
                payer.setThirdDepartmentId(thirdEmployeeBean.getThird_org_id());
                payer.setThirdId(thirdEmployeeBean.getThirdEmployeeId());
                payer.setDepartmentName(thirdEmployeeBean.getOrg_name());
                payer.setDepartmentId(thirdEmployeeBean.getOrg_unit_id());
                payer.setName(thirdEmployeeBean.getName());
                payer.setId(thirdEmployeeBean.getId());
                payer.setPhone(thirdEmployeeBean.getPhone_num());
                busOrderDetailResDTO.setPayer(payer);
            }
            // 行程信息
            if ( null != detailVO.getBusTravelVO() ){
                BusTravelVO busTravelVO = detailVO.getBusTravelVO();
                BusOrderDetailResDTO.Trip trip = new BusOrderDetailResDTO.Trip();
                trip.setBusNo(busTravelVO.getBusNumber());
                trip.setFromStationName(busTravelVO.getFromStationName());
                trip.setToStationName(busTravelVO.getToStationName());
                trip.setFromCityName(busTravelVO.getFromCityName());
                trip.setToCityName(busTravelVO.getToCityName());
                trip.setStartTime(busTravelVO.getFromDateTime());
                busOrderDetailResDTO.setTrip(trip);
            }
            if ( null != detailVO.getOrderStatus() ){
                busOrderDetailResDTO.setOrderType(BusOrderStatusEnum.DONE_CHANG.getKey().equals(detailVO.getOrderStatus().getKey()) ? BusOrderType.REFUND.getType() : BusOrderType.ORIGINAL.getType());
            }
            // 费用归属信息
            List<OrderCostDetailDTO> costInfoResults = sceneService.getOrderCostList(busOrderDetailReqDTO.getOrderId());
            List<BusOrderDetailResDTO.CostAttribution> costAttributionList = Lists.newArrayList();
            if (CollectionUtils.isNotBlank(costInfoResults)){
                costInfoResults.stream().forEach(orderCostDetailDTO -> {
                    List<OrderCostDetailDTO.CostAttributionGroup> costAttributionGroupList = orderCostDetailDTO.getCostAttributionGroupList();
                    if (CollectionUtils.isBlank(costAttributionGroupList)){
                        return;
                    }
                    for (OrderCostDetailDTO.CostAttributionGroup group : costAttributionGroupList) {
                        BusOrderDetailResDTO.CostAttribution costAttribution = new BusOrderDetailResDTO.CostAttribution();
                        costAttributionList.add(costAttribution);
                        costAttribution.setType(group.getCategory());
                        if (CollectionUtils.isBlank(group.getCostAttributionList())){
                            continue;
                        }
                        List<OrderCostDetailDTO.CostAttribution> sourceCostAttributionList = group.getCostAttributionList();
                        List<BusOrderDetailResDTO.Detail> details = Lists.newArrayList();
                        costAttribution.setDetails(details);
                        for (OrderCostDetailDTO.CostAttribution attribution : sourceCostAttributionList) {
                            BusOrderDetailResDTO.Detail detail = new BusOrderDetailResDTO.Detail();
                            BeanUtils.copyProperties(attribution,detail);
                            BigDecimal price = new BigDecimal(attribution.getPrice());
                            price = price.setScale(2, BigDecimal.ROUND_DOWN);
                            detail.setAmount(price.toString());
                            BigDecimal weigtBd = new BigDecimal(attribution.getWeight());
                            weigtBd = weigtBd.setScale(2, BigDecimal.ROUND_DOWN);
                            detail.setWeight(weigtBd.toString());
                            details.add(detail);
                        }
                    }
                });
            }
            BusOrderDetailResDTO.SaaS saasInfo = new BusOrderDetailResDTO.SaaS();
            if ( null != detailVO.getSaasInfoVO()){
                BusSaasInfoVO busSaasInfoVO = detailVO.getSaasInfoVO();
                BeanUtils.copyProperties(busSaasInfoVO,saasInfo);
                saasInfo.setExceedReasonDesc(busSaasInfoVO.getExceedReasonExt());
                saasInfo.setOrderReason(busSaasInfoVO.getOrderReasonName());
                List<BusOrderCostBaseVO> busOrderCostBaseVOS = busSaasInfoVO.getCostBaseVOs();
                List<BusOrderDetailResDTO.OrderRemarkExt> orderRemarkExtList = Lists.newArrayList();
                if (CollectionUtils.isNotBlank(busOrderCostBaseVOS)){
                    BusOrderCostBaseVO busOrderCostBaseVO = busOrderCostBaseVOS.get(0);
                    String customExt = busOrderCostBaseVO.getCustomExt();
                    if (!StringUtils.isBlank(customExt)){
                        List<Map> extList = JsonUtils.toObj(customExt, new TypeReference<List<Map>>() {
                        });
                        if ( null != extList ){
                            for (Map map : extList) {
                                BusOrderDetailResDTO.OrderRemarkExt orderRemarkExt = new BusOrderDetailResDTO.OrderRemarkExt();
                                orderRemarkExt.setTitle(Optional.ofNullable(map.get("custom_field_title")).orElse("").toString());
                                orderRemarkExt.setDetail(Optional.ofNullable(map.get("custom_field_content")).orElse("").toString());
                                orderRemarkExtList.add(orderRemarkExt);
                            }
                        }
                    }
                }
                saasInfo.setOrderRemarkExt(orderRemarkExtList);
                saasInfo.setOrderReasonDesc(busSaasInfoVO.getOrderReasonExt());
            }
            saasInfo.setCostAttributions(costAttributionList);
            busOrderDetailResDTO.setSaasInfo(saasInfo);
            log.info(">>>汽车票订单关联费用归属数据：{}>>>", JsonUtils.toJson(costInfoResults));
            return busOrderDetailResDTO;
        } catch (Exception e){
            log.warn("调用场景获取汽车票订单详情失败 : {}",e.getMessage());
            return new BusOrderDetailResDTO();
        }
    }
}
