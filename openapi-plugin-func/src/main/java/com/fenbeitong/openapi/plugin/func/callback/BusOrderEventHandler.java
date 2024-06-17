package com.fenbeitong.openapi.plugin.func.callback;

import com.fenbeitong.finhub.kafka.msg.order.common.BudgetTicketInfo;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.order.dto.BusOrderEvent;
import com.fenbeitong.openapi.plugin.func.annotation.FuncOrderCallBack;
import com.fenbeitong.openapi.plugin.func.order.dto.*;
import com.fenbeitong.openapi.plugin.func.order.service.FunBusOrderDetailServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.IGetEmployeeInfoFromUcService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 汽车票订单事件
 * @author zhangpeng
 * @date 2022/3/31 10:30 上午
 */
@SuppressWarnings("all")
@Component
@Slf4j
public class BusOrderEventHandler extends EventHandler<BusOrderEvent> {

    @Autowired
    private FunBusOrderDetailServiceImpl funBusOrderDetailService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private IGetEmployeeInfoFromUcService getEmployeeInfoFromUcService;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private ThirdCallbackRecordDao thirdCallbackRecordDao;

    private static final String STATUS_ON_CHANGING_OR_REFUNDING = "退改进行中";

    private static final String STATUS_ON_HAS_CHANGE_OR_REFUND_RECORD = "有退改记录";

    private static final String STATUS_ON_CHANGE_OR_REFUND = "92";

    // 出票成功
    private static final Integer STATUS_ON_BUG_TICEKT_SUCCESS = 80;

    private static final String OPERATION_ON_BUG_TICEKT_SUCCESS = "100008";

    private static final Integer REFUND_COMPLETE = 30;

    /**
     * 汽车票消息处理
     * @param event 汽车票事件
     * @param args  参数
     * @return true 处理成功
     * statusList: 80：正向单出票成功状态 ; 92 是逆向单退票成功状态
     */
    @FuncOrderCallBack(companyId = "companyId", type = 135, status = "status", statusList = {80,92})
    @Override
    public boolean process(BusOrderEvent event, Object... args) {
        log.info("汽车票详情开始处理 event {} ",JsonUtils.toJson(event));
        if (null == event.getBudgetOperateInfo()){
            log.info("汽车票详情事件数据为空 , 不处理");
        }
        List<BudgetTicketInfo> ticketInfoList = event.getBudgetOperateInfo().getBudgetTicketInfos();
        // 一张订单对应多个票id , 查询列表
        if (CollectionUtils.isBlank(ticketInfoList)){
            // 退票 92 的没有 ticket 信息
            if (!REFUND_COMPLETE.equals(event.getBudgetOperateInfo().getOperate())){
                log.info("如果不是退款完成的退票信息 , 不处理");
                return false;
            }
            // 做幂等,已经落库的订单,不再落库
            List<ThirdCallbackRecord> thirdCallbackRecordList = thirdCallbackRecordDao.getApplyByApplyIdAndCompanyId(event.getCompanyId(),event.getRootOrderId(),CallbackType.ORDER.getType());
            Set<String> tickets = thirdCallbackRecordList.stream().map(ThirdCallbackRecord::getTicketId).collect(Collectors.toSet());
            log.info("已经存在的汽车票 tickets : {}",JsonUtils.toJson(tickets));
            BusOrderListReqDTO orderListReqDTO = new BusOrderListReqDTO();
            orderListReqDTO.setCompanyId(event.getCompanyId());
            orderListReqDTO.setOrderId(event.getFbOrderId());
            // 只查退改单
            orderListReqDTO.setOrderState(STATUS_ON_CHANGE_OR_REFUND);
            BaseOrderListRespDTO baseOrderListRespDTO = (BaseOrderListRespDTO) funBusOrderDetailService.getBusOrderList(orderListReqDTO);
            List<BusOrderListResDTO> busOrderList = baseOrderListRespDTO.getResults();
            if (CollectionUtils.isNotBlank(busOrderList)){
                busOrderList.stream().filter(busOrder->event.getRootOrderId().equals(busOrder.getRootOrderId())&&!tickets.contains(busOrder.getTicketId())).forEach(busOrderListResDTO -> {
                    getBusOrderDetail(event,busOrderListResDTO.getTicketId());
                });
            }
        } else {
            // 80 状态的有 ticket 信息
            if (STATUS_ON_BUG_TICEKT_SUCCESS.equals(event.getStatus()) && !OPERATION_ON_BUG_TICEKT_SUCCESS.equals(event.getOperationType().toString()) ){
                log.info("当前状态不是出票成功状态 , 不执行");
                return false;
            }
            // 一个订单对应一个票id
            for (BudgetTicketInfo ticketInfo : ticketInfoList) {
                String ticketId = Optional.ofNullable(ticketInfo).orElse( new BudgetTicketInfo() ).getTicketId();
                getBusOrderDetail(event,ticketId);
            }
        }
        return true;
    }

    private void getBusOrderDetail(BusOrderEvent event , String ticketId){
        BusOrderDetailReqDTO busOrderDetailReqDTO = new BusOrderDetailReqDTO();
        busOrderDetailReqDTO.setTicketId(ticketId);
        busOrderDetailReqDTO.setOrderId(event.getFbOrderId());
        busOrderDetailReqDTO.setCompanyId(event.getCompanyId());
        log.info("汽车票详情参数 {}" , JsonUtils.toJson(busOrderDetailReqDTO));
        log.info("汽车票详情开始查询");
        BusOrderDetailResDTO detail = funBusOrderDetailService.getBusOrderDetail(busOrderDetailReqDTO);
        log.info("汽车票详情查询结束 , 结果为 {}",JsonUtils.toJson(detail));
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(event.getCompanyId());
        ThirdEmployeeRes thirdEmployeeRes = getEmployeeInfoFromUcService.getEmployInfoByEmployeeId(event.getCompanyId(),event.getEmployeeId(),"0");
        if (!ObjectUtils.isEmpty(detail)) {
            if (STATUS_ON_CHANGING_OR_REFUNDING.equals(detail.getOrderState()) || STATUS_ON_HAS_CHANGE_OR_REFUND_RECORD.equals(detail.getOrderState())){
                log.info("当前汽车票状态为退改进行中 , 或者为原单退票状态 , 不落库");
                return;
            }
            ThirdCallbackRecord record = new ThirdCallbackRecord();
            record.setType(135);
            record.setTypeName("汽车票订单");
            record.setTicketId(ticketId);
            record.setOrderId(event.getFbOrderId());
            record.setOrderStatus(event.getBizOrderStatus());
            record.setCompanyId(event.getCompanyId());
            if ( null != thirdEmployeeRes && null != thirdEmployeeRes.getEmployee()){
                record.setUserName(thirdEmployeeRes.getEmployee().getName());
            }
            record.setContactName(Optional.ofNullable(detail.getContacter()).orElse(new BusOrderDetailResDTO.Contacter()).getName());
            record.setCompanyName(Optional.ofNullable(authDefinition).orElse(new AuthDefinition()).getAppName());
            record.setCallbackType(CallbackType.ORDER.getType());
            record.setCallbackData(JsonUtils.toJson(detail));
            callbackRecordDao.saveSelective(record);
            businessDataPushService.pushData(event.getCompanyId(), record, 0, 4);
        }
    }

}
