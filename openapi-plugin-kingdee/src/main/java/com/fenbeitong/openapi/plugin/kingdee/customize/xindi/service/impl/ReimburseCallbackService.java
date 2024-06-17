package com.fenbeitong.openapi.plugin.kingdee.customize.xindi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.*;
import org.apache.dubbo.config.annotation.DubboReference;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.func.order.dto.AirOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.dto.TrainOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncAirOrderServiceImpl;
import com.fenbeitong.openapi.plugin.func.order.service.FuncTrainOrderServiceImpl;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.xindi.dto.KingdeeCommitDataDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.xindi.dto.KingdeeSaveReimbursementTravelDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.xindi.service.KingDeeK3CloudService;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ViewReqDTO;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.constant.ResultEnum;
import com.fenbeitong.openapi.plugin.support.callback.dao.OpenKingdeeConfigDao;
import com.fenbeitong.openapi.plugin.support.callback.dao.OpenKingdeeDataDao;
import com.fenbeitong.openapi.plugin.support.callback.dao.OpenKingdeeDataDetailDao;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenKingdeeConfig;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenKingdeeData;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenKingdeeDataDetail;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcGetOrgUnitRequest;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.util.KingdeeBaseUtils;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyCreatVo;
import com.fenbeitong.usercenter.api.service.company.ICompanyNewInfoService;
import com.fenbeitong.usercenter.api.service.orgunit.IOrgUnitService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Title: SupportCallbackBillService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/9/25 6:53 PM
 */
@Slf4j
@ServiceAspect
@Service
public class ReimburseCallbackService {

    @Autowired
    private OpenKingdeeDataDetailDao openKingdeeDataDetailDao;
    @Autowired
    private OpenKingdeeDataDao openKingdeeDataDao;
    @Autowired
    private KingdeeService kingdeeService;
    @Autowired
    private KingdeeConfig kingdeeConfig;
    @Autowired
    private OpenKingdeeConfigDao kingdeeConfigDao;
    @Autowired
    private FuncAirOrderServiceImpl funcAirOrderService;
    @Autowired
    private FuncTrainOrderServiceImpl trainOrderService;
    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;
    @Autowired
    OpenSysConfigDao openSysConfigDao;
    @Autowired
    UserCenterService userCenterService;
    @Autowired
    private KingDeeK3CloudService kingDeeK3CloudService;
    @DubboReference(check = false)
    private IOrgUnitService orgUnitService;
    @DubboReference(check = false)
    private ICompanyNewInfoService companyNewInfoService;
    @Value("${host.air_biz}")
    private String airBizHost;
    @Value("${host.train_biz}")
    private String trainBizHost;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    public boolean pushBillData(String companyId, String data) {
        try {
            Map map = JsonUtils.toObj(data, Map.class);
            log.info("push reimburse companyId: {}, data: {} ", companyId, data);
            if (map != null && map.get("order_info") != null) {
                Map orderInfo = (Map) MapUtils.getValueByExpress(map, "order_info");
                String orderType = String.valueOf(orderInfo.get("order_category_type"));
                Map<String, Object> param = new HashMap<>();
                param.put("companyId", companyId);
                param.put("type", orderType);
                OpenKingdeeConfig openKingdeeConfig = kingdeeConfigDao.getOpenKingdeeConfig(param);
                if (openKingdeeConfig != null) {
                    //1 初始化报销单与明细数据
                    KingdeeSaveReimbursementTravelDTO kingdeeSaveReimbursementTravelDTO = initReiburseSaveData(openKingdeeConfig, map);
                    KingdeeSaveReimbursementTravelDTO.FEntity fEntity = kingdeeSaveReimbursementTravelDTO.getData().getModel().getEntity().get(0);
                    KingdeeSaveReimbursementTravelDTO.Model model = kingdeeSaveReimbursementTravelDTO.getData().getModel();
                    //2 添加明细行程与事由数据
                    addFEntitys(map, fEntity, model);
                    //3 初始化数据库报销单数据
                    OpenKingdeeData openKingdeeData = initOpenKingdeeData(map, openKingdeeConfig, model);
                    //4 初始化数据库报销单明细数据
                    OpenKingdeeDataDetail openKingdeeDataDetail = initOpenKingdeeDataDetail(fEntity, model);
                    //5 保存报销单逻辑数据
                    saveReimburseData(companyId, map, kingdeeSaveReimbursementTravelDTO, fEntity, openKingdeeData, openKingdeeDataDetail);
                } else {
                    log.warn("kingdee config data no set");
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("单次推送报销单数据失败", e);
            return false;
        }
        return true;

    }

    private void saveReimburseData(String companyId, Map map, KingdeeSaveReimbursementTravelDTO kingdeeSaveReimbursementTravelDTO, KingdeeSaveReimbursementTravelDTO.FEntity fEntity, OpenKingdeeData openKingdeeData, OpenKingdeeDataDetail openKingdeeDataDetail) {
        Map orderInfo = (Map) MapUtils.getValueByExpress(map, "order_info");
        Map priceInfo = (Map) MapUtils.getValueByExpress(map, "price_info");
        String orderType = String.valueOf(orderInfo.get("order_category_type"));
        String orderStatus = String.valueOf(orderInfo.get("status"));
        BigDecimal totalPrice = getPrice(priceInfo.get("total_price"));
        //订单号
        String orderId = orderInfo.get("order_id") != null ? String.valueOf(orderInfo.get("order_id")) : String.valueOf(orderInfo.get("id"));
        switch (orderType) {
            case "7": // 7 国内机票  40 国际机票   1800 出票成功 1821 改签成功
                if ("1800".equals(orderStatus) || "1821".equals(orderStatus) || "1823".equals(orderStatus)) {//
                    save(kingdeeSaveReimbursementTravelDTO, fEntity, openKingdeeData, openKingdeeDataDetail);
                } else if ("1811".equals(orderStatus)) {//退票成功
                    refundAirTicket(companyId, orderInfo, totalPrice, orderId);
                }
                break;
            case "15": //火车 3202 出票成功 3703 改签成功
                if ("3202".equals(orderStatus) || "3703".equals(orderStatus)) {
                    save(kingdeeSaveReimbursementTravelDTO, fEntity, openKingdeeData, openKingdeeDataDetail);
                } else if ("3801".equals(orderStatus)) {//退票成功 只能改签一次
                    refundTrainTicket(companyId, map, totalPrice, orderId);
                }
                break;
            case "11": //酒店 2501 订房成功  2800 退订成功
                if ("2501".equals(orderStatus) || "2800".equals(orderStatus)) {
                    save(kingdeeSaveReimbursementTravelDTO, fEntity, openKingdeeData, openKingdeeDataDetail);
                }
                break;
            case "3": //用车 610 已取消  700 已完成
                if ("610".equals(orderStatus) || "700".equals(orderStatus) || "611".equals(orderStatus)) {
                    save(kingdeeSaveReimbursementTravelDTO, fEntity, openKingdeeData, openKingdeeDataDetail);
                }
                break;
            default:
        }
    }

    private void refundTrainTicket(String companyId, Map map, BigDecimal totalPrice, String orderId) {
        Map trainInfo = (Map) MapUtils.getValueByExpress(map, "train_info");
        String ticketId = (String) trainInfo.get("ticket_id");
        //查询票的历史数据来进行保存更新
        Map trainHistoryOrder = getTrainHistoryOrder(orderId, ticketId);
        Map resultData = (Map) trainHistoryOrder.get("data");
        Map originOrder = (Map) resultData.get("original_order");
        Map selfOrder = (Map) resultData.get("self_order");
        List<Map> parentOrders = resultData.get("parent_orders") != null ? (List<Map>) resultData.get("parent_orders") : new ArrayList<Map>();
        parentOrders.add(originOrder);
        if (parentOrders != null && parentOrders.size() > 0) {
            for (Map parent : parentOrders) {
                List<Map> tickets = (List<Map>) parent.get("tickets");
                String id = (String) parent.get("order_id");
                for (Map ticket : tickets) {
                    totalPrice = totalPrice.add(getPrice(((Map) ticket.get("price_info")).get("total_price")));
                    TrainOrderDetailReqDTO trainOrderDetailReqDTO = new TrainOrderDetailReqDTO();
                    trainOrderDetailReqDTO.setCompanyId(companyId);
                    trainOrderDetailReqDTO.setTicketId((String) ticket.get("ticket_id"));
                    trainOrderDetailReqDTO.setOrderId(id);
                    Map<String, Object> orderDetail = (Map) trainOrderService.detail(trainOrderDetailReqDTO);
                    Map orderPriceInfo = (Map) MapUtils.getValueByExpress(orderDetail, "price_info");
                    Map orderInfoMap = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
                    orderPriceInfo.put("total_price", getPrice(orderPriceInfo.get("total_price")).negate());
                    if (id.equals((String) originOrder.get("order_id"))) {
                        orderInfoMap.put("status", 3202);
                        orderInfoMap.put("status_name", "出票成功-退款");
                    } else {
                        orderInfoMap.put("status", 3703);
                        orderInfoMap.put("status_name", "改签成功-退款");
                    }
                    createCallbackOrder(orderDetail, "15", companyId);
                }
            }
        }
        selfOrder.put("total_price", totalPrice);
        TrainOrderDetailReqDTO trainOrderDetailReqDTO = new TrainOrderDetailReqDTO();
        trainOrderDetailReqDTO.setCompanyId(companyId);
        trainOrderDetailReqDTO.setTicketId(ticketId);
        trainOrderDetailReqDTO.setOrderId(orderId);
        Map<String, Object> orderDetail = (Map) trainOrderService.detail(trainOrderDetailReqDTO);
        Map orderPriceInfo = (Map) MapUtils.getValueByExpress(orderDetail, "price_info");
        Map orderInfoMap = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
        orderInfoMap.put("status", 3202);
        orderInfoMap.put("status_name", "退款成功-退款");
        orderPriceInfo.put("total_price", totalPrice);
        createCallbackOrder(orderDetail, "15", companyId);
    }

    private void refundAirTicket(String companyId, Map orderInfo, BigDecimal totalPrice, String orderId) {
        String ticketId = (String) orderInfo.get("ticket_id");
        //查询票的历史数据来进行保存更新
        Map airHistoryOrder = getAirHistoryOrder(orderId, ticketId, false);
        Map resultData = (Map) airHistoryOrder.get("data");
        Map originOrder = (Map) resultData.get("original_order");
        Map selfOrder = (Map) resultData.get("self_order");
        List<Map> parentOrders = (List<Map>) resultData.get("parent_orders");
        if (parentOrders != null && parentOrders.size() > 0) {
            for (Map parent : parentOrders) {
                List<Map> tickets = (List<Map>) parent.get("tickets");
                String id = (String) parent.get("order_id");
                for (Map ticket : tickets) {
                    totalPrice = totalPrice.add(getPrice(ticket.get("total_price")));
                    AirOrderDetailReqDTO req = new AirOrderDetailReqDTO();
                    req.setCompanyId(companyId);
                    req.setOrderId(id);
                    req.setTicketId((String) ticket.get("ticket_id"));
                    req.setIsIntl(false);
                    Map<String, Object> orderDetail = (Map<String, Object>) funcAirOrderService.detail(req);
                    Map orderPriceInfo = (Map) MapUtils.getValueByExpress(orderDetail, "price_info");
                    Map orderInfoMap = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
                    orderPriceInfo.put("total_price", getPrice(orderPriceInfo.get("total_price")).negate());
                    if (id.equals((String) originOrder.get("order_id"))) {
                        orderInfoMap.put("status", 1800);
                        orderInfoMap.put("status_name", "出票成功-退款");
                    } else {
                        orderInfoMap.put("status", 1821);
                        orderInfoMap.put("status_name", "改签成功-退款");
                    }
                    createCallbackOrder(orderDetail, "7", companyId);
                }
            }
        }
        selfOrder.put("total_price", totalPrice);
        AirOrderDetailReqDTO req = new AirOrderDetailReqDTO();
        req.setCompanyId(companyId);
        req.setOrderId(orderId);
        req.setTicketId(ticketId);
        req.setIsIntl(false);
        Map<String, Object> orderDetail = (Map<String, Object>) funcAirOrderService.detail(req);
        Map orderPriceInfo = (Map) MapUtils.getValueByExpress(orderDetail, "price_info");
        Map orderInfoMap = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
        orderInfoMap.put("status", 1800);
        orderInfoMap.put("status_name", "退款成功-退款");
        orderPriceInfo.put("total_price", totalPrice);
        createCallbackOrder(orderDetail, "7", companyId);
    }

    private void save(KingdeeSaveReimbursementTravelDTO kingdeeSaveReimbursementTravelDTO, KingdeeSaveReimbursementTravelDTO.FEntity fEntity, OpenKingdeeData openKingdeeData, OpenKingdeeDataDetail openKingdeeDataDetail) {
        if (fEntity.getOtherTraAmount().compareTo(BigDecimal.ZERO) != 0) {
            openKingdeeData.setCallbackData(JsonUtils.toJson(kingdeeSaveReimbursementTravelDTO));
            openKingdeeDataDao.saveSelective(openKingdeeData);
            Long id = openKingdeeData.getId();
            openKingdeeDataDetail.setDataId(id);
            openKingdeeDataDetail.setEntityData(JsonUtils.toJson(fEntity));
            openKingdeeDataDetailDao.saveSelective(openKingdeeDataDetail);
        }
    }

    private OpenKingdeeDataDetail initOpenKingdeeDataDetail(KingdeeSaveReimbursementTravelDTO.FEntity fEntity, KingdeeSaveReimbursementTravelDTO.Model model) {
        OpenKingdeeDataDetail openKingdeeDataDetail = new OpenKingdeeDataDetail();
        openKingdeeDataDetail.setCreateDate(new Date());
        openKingdeeDataDetail.setUpdateDate(new Date());
        if (fEntity.getTravelStartDate() != null && fEntity.getTravelEndDate() != null) {
            fEntity.setDays(DateUtils.differentDaysByMillisecond(fEntity.getTravelStartDate(), fEntity.getTravelEndDate()) + 1);
        } else {
            fEntity.setDays(0);
        }
        openKingdeeDataDetail.setExpenseAmount(fEntity.getOtherTraAmount());
        openKingdeeDataDetail.setExpenseDeptId(fEntity.getExpenseDeptEntryId().getNumber());
        openKingdeeDataDetail.setExpenseId(fEntity.getExpId().getNumber());
        openKingdeeDataDetail.setExpenseRemark(model.getCausa());
        openKingdeeDataDetail.setTravelEndDate(fEntity.getTravelEndDate());
        openKingdeeDataDetail.setTravelStartDate(fEntity.getTravelStartDate());
        openKingdeeDataDetail.setTravelEndSite(fEntity.getTravelEndSite());
        openKingdeeDataDetail.setTravelStartSite(fEntity.getTravelStartSite());
        return openKingdeeDataDetail;
    }

    private OpenKingdeeData initOpenKingdeeData(Map map, OpenKingdeeConfig openKingdeeConfig, KingdeeSaveReimbursementTravelDTO.Model model) {
        Map orderInfo = (Map) MapUtils.getValueByExpress(map, "order_info");
        Map orderThirdInfo = (Map) MapUtils.getValueByExpress(map, "third_info");
        Map orderUserInfo = (Map) MapUtils.getValueByExpress(map, "user_info");
        //订单号
        String orderId = orderInfo.get("order_id") != null ? String.valueOf(orderInfo.get("order_id")) : String.valueOf(orderInfo.get("id"));
        OpenKingdeeData openKingdeeData = new OpenKingdeeData();
        openKingdeeData.setApplyDate(new Date());
        openKingdeeData.setDataType(1);
        openKingdeeData.setOrderId(orderId);
        openKingdeeData.setApplyOrg(openKingdeeConfig.getOrgId());
        openKingdeeData.setBillType(model.getRequestType());
        openKingdeeData.setApplyReason(openKingdeeConfig.getRemark());
        openKingdeeData.setApplyUserId(String.valueOf(orderUserInfo.get("id")));
        openKingdeeData.setApplyUserName(String.valueOf(orderUserInfo.get("name")));
        openKingdeeData.setContactPhone(String.valueOf(orderUserInfo.get("phone")));
        openKingdeeData.setApplyDepartmentId(String.valueOf(orderUserInfo.get("unit_id")));
        openKingdeeData.setApplyDepartmentName(String.valueOf(orderUserInfo.get("unit_name")));
        openKingdeeData.setExpenseOrg(openKingdeeConfig.getOrgId());
        openKingdeeData.setCurrencyId(openKingdeeConfig.getCurrencyId());
        openKingdeeData.setStatus("0");
        openKingdeeData.setExecuteMax(5);
        openKingdeeData.setExecuteNum(0);
        openKingdeeData.setContactUnitType(openKingdeeConfig.getContactUnitType());
        openKingdeeData.setExpreseDep(orderThirdInfo.get("passenger_unit_id") != null ? String.valueOf(orderThirdInfo.get("passenger_unit_id")) : String.valueOf(orderThirdInfo.get("guest_unit_id")));
        openKingdeeData.setContactUnit(String.valueOf(orderUserInfo.get("unit_id")));
        openKingdeeData.setCreateDate(new Date());
        openKingdeeData.setUpdateDate(new Date());
        return openKingdeeData;
    }

    private void addFEntitys(Map map, KingdeeSaveReimbursementTravelDTO.FEntity fEntity, KingdeeSaveReimbursementTravelDTO.Model model) {
        Map orderInfo = (Map) MapUtils.getValueByExpress(map, "order_info");
        Map orderPassenger = (Map) MapUtils.getValueByExpress(map, "passenger_info");
        Map orderGuestInfo = MapUtils.getValueByExpress(map, "guest_info") != null ? ((List<Map>) MapUtils.getValueByExpress(map, "guest_info")).get(0) : null;
        Map priceInfo = (Map) MapUtils.getValueByExpress(map, "price_info");
        String orderType = String.valueOf(orderInfo.get("order_category_type"));
        switch (orderType) {
            case "7": // 7 国内机票  40 国际机票
                Map ticketInfo = (Map) MapUtils.getValueByExpress(map, "ticket_info");
                Map goSegment = (Map) MapUtils.getValueByExpress(ticketInfo, "go_segment");
                Map backSegment = (Map) MapUtils.getValueByExpress(ticketInfo, "back_segment");
                if (backSegment != null) {
                    fEntity.setTravelStartDate(DateUtils.toDate(((String) goSegment.get("starting_date")).concat(" ").concat((String) goSegment.get("starting_time")), "yyyy-MM-dd HH:mm"));
                    fEntity.setTravelEndDate(DateUtils.toDate(((String) backSegment.get("arrived_date")).concat(" ").concat((String) goSegment.get("arrived_time")), "yyyy-MM-dd HH:mm"));
                    fEntity.setTravelStartSite((String) goSegment.get("starting_city"));
                    fEntity.setTravelEndSite((String) backSegment.get("arrived_city"));
                } else {
                    fEntity.setTravelStartDate(DateUtils.toDate(((String) goSegment.get("starting_date")).concat(" ").concat((String) goSegment.get("starting_time")), "yyyy-MM-dd HH:mm"));
                    fEntity.setTravelEndDate(DateUtils.toDate(((String) goSegment.get("arrived_date")).concat(" ").concat((String) goSegment.get("arrived_time")), "yyyy-MM-dd HH:mm"));
                    fEntity.setTravelStartSite((String) goSegment.get("starting_city"));
                    fEntity.setTravelEndSite((String) goSegment.get("arrived_city"));
                }
                //设置机票的事由
                model.setCausa(String.valueOf(orderPassenger.get("name")).concat("乘坐从飞机，从 ").concat(fEntity.getTravelStartSite()).concat(DateUtils.toStr(fEntity.getTravelStartDate(), "yyyy-MM-dd HH:mm:ss")).concat(" 到 ").concat(fEntity.getTravelEndSite()).concat(DateUtils.toStr(fEntity.getTravelEndDate(), "yyyy-MM-dd HH:mm:ss")).concat(", 总消费：").concat(String.valueOf(priceInfo.get("total_price"))));
                break;
            case "15": //火车
                Map trainInfo = (Map) MapUtils.getValueByExpress(map, "train_info");
                fEntity.setTravelStartDate(DateUtils.toDate((String) trainInfo.get("start_time")));
                fEntity.setTravelEndDate(DateUtils.toDate((String) trainInfo.get("end_time")));
                fEntity.setTravelStartSite((String) trainInfo.get("from_station"));
                fEntity.setTravelEndSite((String) trainInfo.get("to_station"));
                //设置火车的事由
                model.setCausa(String.valueOf(orderPassenger.get("name")).concat("乘坐从火车，从 ").concat(fEntity.getTravelStartSite()).concat(DateUtils.toStr(fEntity.getTravelStartDate(), "yyyy-MM-dd HH:mm:ss")).concat(" 到 ").concat(fEntity.getTravelEndSite()).concat(DateUtils.toStr(fEntity.getTravelEndDate(), "yyyy-MM-dd HH:mm:ss")).concat(", 总消费：").concat(String.valueOf(priceInfo.get("total_price"))));
                break;
            case "11": //酒店
                Map hotelInfo = (Map) MapUtils.getValueByExpress(map, "hotel_info");
                fEntity.setTravelStartDate(DateUtils.toDate((String) hotelInfo.get("checkin_date")));
                fEntity.setTravelEndDate(DateUtils.toDate((String) hotelInfo.get("checkout_date")));
                fEntity.setTravelStartSite(hotelInfo.get("city_name") != null ? (String) hotelInfo.get("city_name") : "北京");
                fEntity.setTravelEndSite(hotelInfo.get("city_name") != null ? (String) hotelInfo.get("city_name") : "北京");
                //设置酒店的事由
                model.setCausa(String.valueOf(orderGuestInfo.get("name")).concat("在 ").concat(fEntity.getTravelStartSite()).concat(hotelInfo.get("hotel_name") != null ? (String) hotelInfo.get("hotel_name") : " ").concat("地址为：").concat(hotelInfo.get("hotel_address") != null ? (String) hotelInfo.get("hotel_address") : " ").concat(DateUtils.toStr(fEntity.getTravelStartDate(), "yyyy-MM-dd")).concat(" 到 ").concat(fEntity.getTravelEndSite()).concat(DateUtils.toStr(fEntity.getTravelEndDate(), "yyyy-MM-dd")).concat(" 时间段入住").concat(", 总消费：").concat(String.valueOf(priceInfo.get("total_price"))));
                break;
            case "3": //用车
                fEntity.setTravelStartDate(orderInfo.get("departure_time") != null ? DateUtils.toDate((String) orderInfo.get("departure_time")) : DateUtils.toDate((String) orderInfo.get("create_time")));
                fEntity.setTravelEndDate(orderInfo.get("arrival_time") != null ? DateUtils.toDate((String) orderInfo.get("arrival_time")) : DateUtils.toDate((String) orderInfo.get("create_time")));
                fEntity.setTravelStartSite((String) orderInfo.get("departure_name"));
                fEntity.setTravelEndSite((String) orderInfo.get("arrival_name"));
                //设置用车的事由
                model.setCausa(String.valueOf(orderPassenger.get("name")).concat("用车，从 ").concat(fEntity.getTravelStartSite()).concat(DateUtils.toStr(fEntity.getTravelStartDate(), "yyyy-MM-dd HH:mm:ss")).concat(" 到 ").concat(fEntity.getTravelEndSite()).concat(DateUtils.toStr(fEntity.getTravelEndDate(), "yyyy-MM-dd HH:mm:ss")).concat(", 总消费：").concat(String.valueOf(priceInfo.get("total_price"))));
                break;
            default:
        }
    }

    public boolean createReimburseJob() {
        AtomicBoolean resultFlag = new AtomicBoolean(true);
        List<OpenKingdeeData> openKingdeeDatas = openKingdeeDataDao.getNeedProcessedTaskList();
        openKingdeeDatas.forEach(openKingdeeData -> {
//                JSONObject paramJson = JSON.parseObject(openKingdeeData.getCallbackData());
//                JSONObject data1 = (JSONObject) paramJson.get("data");
//                JSONObject model = (JSONObject) data1.get("Model");
//                JSONArray fEntitys = (JSONArray) model.get("FEntity");
//                JSONObject fEntity = (JSONObject) fEntitys.get(0);
//                BigDecimal otherTraAmount = (BigDecimal)fEntity.get("FOtherTraAmount");
//                if (otherTraAmount.compareTo(new BigDecimal(BigInteger.ZERO)) != 0) {
//
//                }
            try {
                log.info("create reimburse start, id: {}, orderId: {}  ", openKingdeeData.getId(), openKingdeeData.getOrderId());
                MultiValueMap loginParam;
                String loginUrl;
                // 判断环境 生产环境则loginByAppSecret,appId和appSecret均不为空
                if (!StringUtils.isBlank(kingdeeConfig.getLoginByAppSecret()) && !StringUtils.isBlank(kingdeeConfig.getAppId()) && !StringUtils.isBlank(kingdeeConfig.getAppSecret())) {
                    String username = getValueFromJson(openKingdeeData.getCallbackData(), "data.Model.FCreatorId.FUserID");
                    loginParam = KingdeeBaseUtils.buildLoginByAppSecret(kingdeeConfig.getAcctId(), username, kingdeeConfig.getAppId(), kingdeeConfig.getAppSecret(), kingdeeConfig.getLcid());
                    loginUrl = kingdeeConfig.getUrl() + kingdeeConfig.getLoginByAppSecret();
                } else {
                    loginParam = KingdeeBaseUtils.buildLogin(kingdeeConfig.getAcctId(), kingdeeConfig.getUserName(), kingdeeConfig.getPassword(), kingdeeConfig.getLcid());
                    loginUrl = kingdeeConfig.getUrl() + kingdeeConfig.getLogin();
                }
                ResultVo login = kingdeeService.login(loginUrl, loginParam);
                if (login.getCode() != ResultEnum.SUCCESS.getCode()) {
                    log.info("【登录金蝶系统失败】：{}", login.getMsg());
                    return;
                }
                Map<String, Object> map2 = (Map<String, Object>) login.getData();
                String cookie = map2.get("cookie").toString();
                ResultVo resultVo = kingdeeService.save(kingdeeConfig.getUrl() + kingdeeConfig.getSave(), cookie, openKingdeeData.getCallbackData());
                if (resultVo.getCode() == 0) {
                    Map result = (Map) resultVo.getData();
                    JSONArray successEntitys = (JSONArray) result.get("SuccessEntitys");
                    JSONObject jsonObject = (JSONObject) successEntitys.get(0);
                    String number = (String) jsonObject.get("Number");
                    log.info("create reimburse save success, id: {}, orderId: {}  billNo:{} ", openKingdeeData.getId(), openKingdeeData.getOrderId(), number);
                    //更新数据
                    openKingdeeData.setStatus("1");
                    openKingdeeData.setBillNo(String.valueOf(number));
                    openKingdeeData.setUpdateDate(new Date());
                    openKingdeeData.setResult(JsonUtils.toJson(resultVo));
                    openKingdeeDataDao.updateById(openKingdeeData);
                    //提交数据
                    KingdeeCommitDataDTO kingdeeCommitDataDTO = new KingdeeCommitDataDTO();
                    KingdeeCommitDataDTO.Resource data = new KingdeeCommitDataDTO.Resource();
                    data.getNumbers().add(openKingdeeData.getBillNo());
                    kingdeeCommitDataDTO.setData(data);
                    ResultVo submit = kingdeeService.submit(kingdeeConfig.getUrl() + kingdeeConfig.getSubmit(), cookie, JsonUtils.toJson(kingdeeCommitDataDTO));
                    if (submit.getCode() == 0) {
                        openKingdeeData.setStatus("2");
                        openKingdeeData.setUpdateDate(new Date());
                        openKingdeeData.setResult(JsonUtils.toJson(submit));
                        openKingdeeData.setExecuteNum(openKingdeeData.getExecuteNum() + 1);
                        openKingdeeDataDao.updateById(openKingdeeData);
                    } else {
                        openKingdeeData.setStatus("2");
                        openKingdeeData.setExecuteNum(openKingdeeData.getExecuteNum() + 1);
                        openKingdeeData.setUpdateDate(new Date());
                        openKingdeeData.setResult(JsonUtils.toJson(submit));
                        openKingdeeDataDao.updateById(openKingdeeData);
                        resultFlag.set(false);
                        exceptionRemind.remindDingTalk("肇庆骏鸿报销单提交失败,orderId=" + openKingdeeData.getOrderId());

                    }
                } else {
                    exceptionRemind.remindDingTalk("肇庆骏鸿报销单创建失败,orderId=" + openKingdeeData.getOrderId());
                    openKingdeeData.setStatus("3");
                    openKingdeeData.setExecuteNum(openKingdeeData.getExecuteNum() + 1);
                    openKingdeeData.setUpdateDate(new Date());
                    openKingdeeData.setResult(JsonUtils.toJson(resultVo));
                    openKingdeeDataDao.updateById(openKingdeeData);
                    resultFlag.set(false);
                }
            } catch (Exception e) {
                log.info("create reimburse save failure, id: {}, orderId: {}  ", openKingdeeData.getId(), openKingdeeData.getOrderId());
                log.warn("创建报销单失败", e);
                resultFlag.set(false);
            }
        });
        return resultFlag.get();
    }


    private KingdeeSaveReimbursementTravelDTO initReiburseSaveData(OpenKingdeeConfig openKingdeeConfig, Map map) {
        Map orderInfo = (Map) MapUtils.getValueByExpress(map, "order_info");
        Map orderThirdInfo = (Map) MapUtils.getValueByExpress(map, "third_info");
        Map orderPassenger = (Map) MapUtils.getValueByExpress(map, "passenger_info");
        Map orderGuestInfo = MapUtils.getValueByExpress(map, "guest_info") != null ? ((List<Map>) MapUtils.getValueByExpress(map, "guest_info")).get(0) : null;
        Map priceInfo = (Map) MapUtils.getValueByExpress(map, "price_info");
        Integer orderOpType = Integer.valueOf(String.valueOf(orderInfo.get("order_op_type"))); // 10 表示正向单  20 表示逆向单
        String orderType = String.valueOf(orderInfo.get("order_category_type"));
        KingdeeSaveReimbursementTravelDTO kingdeeSaveReimbursementTravelDTO = new KingdeeSaveReimbursementTravelDTO();
        KingdeeSaveReimbursementTravelDTO.Model model = new KingdeeSaveReimbursementTravelDTO.Model();
        model.setOrderId(orderInfo.get("order_id") != null ? String.valueOf(orderInfo.get("order_id")) : String.valueOf(orderInfo.get("id")));
        //银行信息 开户银行页面必填
        model.setBankBranch(openKingdeeConfig.getBankBranch());
        model.setBankAccount(openKingdeeConfig.getBankAccount());
        model.setBankAccountName(openKingdeeConfig.getBankAccountName());
        KingdeeSaveReimbursementTravelDTO.FBillTypeID fBillTypeID = new KingdeeSaveReimbursementTravelDTO.FBillTypeID();
        fBillTypeID.setNumber(orderOpType == 10 ? "CLFBX001_SYS" : "CLFBX001_SYS");
        model.setBillTypeId(fBillTypeID);
        model.setCausa(openKingdeeConfig.getRemark());
        KingdeeSaveReimbursementTravelDTO.FCONTACTUNIT fcontactunit = new KingdeeSaveReimbursementTravelDTO.FCONTACTUNIT();
        model.setContactUnitType(openKingdeeConfig.getContactUnitType());
        model.setCreateDate(new Date());//DateUtils.toDate((String)orderInfo.get("create_time"), "yyyy-MM-dd HH:mm:ss")

        //审计会计
        //调用分贝通，查询部门详情
        String depId = orderThirdInfo.get("passenger_unit_id") != null ? String.valueOf(orderThirdInfo.get("passenger_unit_id")) : String.valueOf(orderThirdInfo.get("guest_unit_id"));
        String userId = orderThirdInfo.get("passenger_user_id") != null ? String.valueOf(orderThirdInfo.get("passenger_user_id")) : orderThirdInfo.get("guest_user_id") != null ? (String.valueOf(orderThirdInfo.get("guest_user_id")).split(","))[0] : "";
        UcGetOrgUnitRequest openApi = UcGetOrgUnitRequest.builder()
            .companyId(openKingdeeConfig.getCompanyId())
            .orgId(depId)
            .operatorId(userId)
            .type(2)
            .build();
        Map ucOrgUnit = userCenterService.getUcOrgUnit(openApi);
        log.info("find dep data: {}", JsonUtils.toJson(ucOrgUnit));
        Map data = (Map) ucOrgUnit.get("data");
        List<Map> expandLists = (List<Map>) data.get("expand_list");
        //审计会计编码获取
        String auditId = null;
        if (expandLists != null && expandLists.size() > 0 && expandLists.get(0).get("accountant_number1") != null) {
            auditId = expandLists.get(0).get("accountant_number1") != null ? (String) expandLists.get(0).get("accountant_number1") : (expandLists.get(0).get("accountant_number2") != null ? (String) expandLists.get(0).get("accountant_number2") : (expandLists.get(0).get("accountant_number3") != null ? (String) expandLists.get(0).get("accountant_number3") : ""));
        }
        //expandId 费用项目：
        String expandId = null;
        String companyId = openKingdeeConfig.getCompanyId();
        ViewReqDTO viewReqDTO = initFindConfig("1", depId, companyId);
        List<List> kingdeeListData = kingDeeK3CloudService.findKingdeeListData(viewReqDTO);
        log.info("get dep detail id: {} result: {}", depId, JsonUtils.toJson(kingdeeListData));
        if (kingdeeListData != null && kingdeeListData.size() > 0) {
            List fields = (List) kingdeeListData.get(0);
            ViewReqDTO viewParam = null;
            if (orderType.equals("7")) {
                viewParam = initFindConfig("2", String.valueOf(fields.get(1)), companyId);
            } else if (orderType.equals("11")) {
                viewParam = initFindConfig("2", String.valueOf(fields.get(2)), companyId);
            } else if (orderType.equals("3")) {
                viewParam = initFindConfig("2", String.valueOf(fields.get(3)), companyId);
            } else if (orderType.equals("15")) {
                viewParam = initFindConfig("2", String.valueOf(fields.get(4)), companyId);
            }
            expandId = getExtFileld(viewParam);
        }
        KingdeeSaveReimbursementTravelDTO.FCreatorId fCreatorId = new KingdeeSaveReimbursementTravelDTO.FCreatorId();
        if ("11".equals(orderType)) {
            model.setContactPhoneNo(String.valueOf(orderGuestInfo.get("phone")));
            fcontactunit.setNumber(userId);
            fCreatorId.setUserId(String.valueOf(orderThirdInfo.get("user_id")));
        } else {
            model.setContactPhoneNo(String.valueOf(orderPassenger.get("phone")));
            fcontactunit.setNumber(userId);
            fCreatorId.setUserId(String.valueOf(orderThirdInfo.get("user_id")));
        }
        model.setContactUnit(fcontactunit);
        model.setCreatorId(fCreatorId);
        KingdeeSaveReimbursementTravelDTO.F_JH_CWSP f_jh_cwsp = new KingdeeSaveReimbursementTravelDTO.F_JH_CWSP();
        f_jh_cwsp.setStaffNumber(ObjectUtil.isNotEmpty(auditId) ? auditId : openKingdeeConfig.getJhCwsp());
        model.setJhCwsp(f_jh_cwsp);
        KingdeeSaveReimbursementTravelDTO.FCurrencyID fCurrencyID = new KingdeeSaveReimbursementTravelDTO.FCurrencyID();
        fCurrencyID.setNumber(openKingdeeConfig.getCurrencyId());
        model.setCurrencyId(fCurrencyID);
        KingdeeSaveReimbursementTravelDTO.FLocCurrencyID locCurrencyId = new KingdeeSaveReimbursementTravelDTO.FLocCurrencyID();
        locCurrencyId.setNumber(openKingdeeConfig.getLocalCurrencyId());
        model.setLocCurrencyId(locCurrencyId);
        KingdeeSaveReimbursementTravelDTO.FOrgID orgId = new KingdeeSaveReimbursementTravelDTO.FOrgID();
        orgId.setNumber(openKingdeeConfig.getOrgId());
        model.setOrgId(orgId);
        KingdeeSaveReimbursementTravelDTO.FPayOrgId payOrgId = new KingdeeSaveReimbursementTravelDTO.FPayOrgId();
        payOrgId.setNumber(openKingdeeConfig.getPayOrgId());
        model.setPayOrgId(payOrgId);
        KingdeeSaveReimbursementTravelDTO.FExpenseOrgId expenseOrgId = new KingdeeSaveReimbursementTravelDTO.FExpenseOrgId();
        expenseOrgId.setNumber(openKingdeeConfig.getOrgId());
        model.setExpenseOrgId(expenseOrgId);
        KingdeeSaveReimbursementTravelDTO.FPaySettlleTypeID paySettlleTypeId = new KingdeeSaveReimbursementTravelDTO.FPaySettlleTypeID();
        paySettlleTypeId.setNumber(openKingdeeConfig.getPaySettlleTypeId());
        model.setPaySettlleTypeId(paySettlleTypeId);
        KingdeeSaveReimbursementTravelDTO.FExchangeTypeID exchangeTypeId = new KingdeeSaveReimbursementTravelDTO.FExchangeTypeID();
        exchangeTypeId.setNumber(openKingdeeConfig.getExchangeRateType());
        model.setExchangeTypeId(exchangeTypeId);
        model.setExchangeRate(openKingdeeConfig.getExchangeRate());
        KingdeeSaveReimbursementTravelDTO.FRequestDeptID requestDeptID = new KingdeeSaveReimbursementTravelDTO.FRequestDeptID();
        requestDeptID.setNumber(depId);
        model.setRequestDeptId(requestDeptID);
        model.setOutContactUnitType(openKingdeeConfig.getOutContactUnitType());
        model.setApplyDate(new Date());
        model.setRequestType(getPrice(priceInfo.get("total_price")).compareTo(new BigDecimal(BigInteger.ZERO)) > 0 ? "1" : "2");
        model.setCombinePay(getPrice(priceInfo.get("total_price")).compareTo(new BigDecimal(BigInteger.ZERO)) > 0 ? true : false);
        model.setCheckbox1(getPrice(priceInfo.get("total_price")).compareTo(new BigDecimal(BigInteger.ZERO)) > 0 ? false : true);
        model.setRealPay(getPrice(priceInfo.get("total_price")).compareTo(new BigDecimal(BigInteger.ZERO)) > 0 ? true : false);
        //明细数据
        ArrayList<KingdeeSaveReimbursementTravelDTO.FEntity> fEntities = new ArrayList<>();
        KingdeeSaveReimbursementTravelDTO.FEntity fEntity = new KingdeeSaveReimbursementTravelDTO.FEntity();
        fEntity.setJhFysm(openKingdeeConfig.getRemark());
        //fEntity.setExpenseAmount(getPrice(priceInfo.get("total_price")));
        KingdeeSaveReimbursementTravelDTO.FExpenseDeptEntryID fExpenseDeptEntryID = new KingdeeSaveReimbursementTravelDTO.FExpenseDeptEntryID();
        fExpenseDeptEntryID.setNumber(depId);
        fEntity.setExpenseDeptEntryId(fExpenseDeptEntryID);
        KingdeeSaveReimbursementTravelDTO.FExpID fExpID = new KingdeeSaveReimbursementTravelDTO.FExpID();
        fExpID.setNumber(expandId != null ? expandId : openKingdeeConfig.getExpandId());
        fEntity.setExpId(fExpID);
//        fEntity.setExpSubmitAmount(getPrice(priceInfo.get("total_price")));
//        fEntity.setLocExpSubmitAmount(getPrice(priceInfo.get("total_price")));
//        fEntity.setLocnoTaxAmount(getPrice(priceInfo.get("total_price")));
//        fEntity.setLocReqSubmitAmount(getPrice(priceInfo.get("total_price")));
        if ("1".equals(model.getRequestType())) {
            fEntity.setOtherTraAmount(getPrice(priceInfo.get("total_price")));
        } else {
            fEntity.setOtherTraAmount(getPrice(priceInfo.get("total_price")).negate());
        }
//        fEntity.setReqSubmitAmount(getPrice(priceInfo.get("total_price")));
//        fEntity.setRequestAmount(getPrice(priceInfo.get("total_price")));
//        fEntity.setTaxSubmitAmt(getPrice(priceInfo.get("total_price")));
        fEntities.add(fEntity);
        model.setEntity(fEntities);
//        model.setReqAmountSum(getPrice(priceInfo.get("total_price")));
//        model.setExpAmountSum(getPrice(priceInfo.get("total_price")));
//        model.setLocExpAmountSum(getPrice(priceInfo.get("total_price")));
//        model.setLocReqAmountSum(getPrice(priceInfo.get("total_price")));
//        model.setReqReimbAmountSum(getPrice(priceInfo.get("total_price")));
//        model.setReqPayReFoundAmountSum(getPrice(priceInfo.get("total_price")));
        KingdeeSaveReimbursementTravelDTO.FProposerID fProposerID = new KingdeeSaveReimbursementTravelDTO.FProposerID();
        fProposerID.setStafNumber(userId);
        model.setProposeId(fProposerID);
        KingdeeSaveReimbursementTravelDTO.FExpenseDeptID fExpenseDeptID = new KingdeeSaveReimbursementTravelDTO.FExpenseDeptID();
        fExpenseDeptID.setNumber(depId);
        model.setExpenseDepId(fExpenseDeptID);
        KingdeeSaveReimbursementTravelDTO.FRefundBankAccount refundBankAccount = new KingdeeSaveReimbursementTravelDTO.FRefundBankAccount();
        refundBankAccount.setNumber(openKingdeeConfig.getRefundBankAccount());
        model.setRefundBankAccount(refundBankAccount);

        KingdeeSaveReimbursementTravelDTO.Resource resource = new KingdeeSaveReimbursementTravelDTO.Resource();
        resource.setModel(model);
        kingdeeSaveReimbursementTravelDTO.setData(resource);
        return kingdeeSaveReimbursementTravelDTO;
    }

    private String getExtFileld(ViewReqDTO department) {
        String expandId = null;
        List<List> expanseList = kingDeeK3CloudService.findKingdeeListData(department);
        log.info("find expense id: {} result: {} ", department.getData().getFilterString(), JsonUtils.toJson(expanseList));
        if (expanseList != null && expanseList.size() > 0) {
            expandId = (String) expanseList.get(0).get(0);
        }
        return expandId;

    }

//    private String getBillNo(Long id) {
//        if (String.valueOf(id).length() < 3 && String.valueOf(id).length() == 1) {
//            return new StringBuilder("CLBX").append(DateUtils.toStr(new Date(), "yyMM")).append("00").append(id).toString();
//        } else if (String.valueOf(id).length() < 3 && String.valueOf(id).length() == 2) {
//            return new StringBuilder("CLBX").append(DateUtils.toStr(new Date(), "yyMM")).append("0").append(id).toString();
//        } else {
//            return new StringBuilder("CLBX").append(DateUtils.toStr(new Date(), "yyMM")).append(id).toString();
//        }
//    }


    private void createCallbackOrder(Map orderDetail, String orderType, String companyId) {
        if (!ObjectUtils.isEmpty(orderDetail)) {
            CompanyCreatVo companyCreatVo = companyNewInfoService.info(companyId);
            Map orderInfo = (Map) MapUtils.getValueByExpress(orderDetail, "order_info");
            Map priceInfo = (Map) MapUtils.getValueByExpress(orderDetail, "price_info");
            Map passengerInfo = (Map) MapUtils.getValueByExpress(orderDetail, "passenger_info");
            Map userInfo = (Map) MapUtils.getValueByExpress(orderDetail, "user_info");
            if (getPrice(priceInfo.get("total_price")).compareTo(BigDecimal.ZERO) != 0) {
                switch (orderType) {
                    case "7": // 7 国内机票  40 国际机票
                        orderInfo.put("order_category_type", 7);
                        orderInfo.put("order_category_name", "国内机票");
                        orderInfo.put("order_op_type", (Integer) orderInfo.get("status") == 1811 ? 20 : 10);
                        orderInfo.put("order_op_type_name", (Integer) orderInfo.get("status") == 1811 ? "逆向单" : "正向单");
                        ThirdCallbackRecord airRecord = new ThirdCallbackRecord();
                        airRecord.setType(7);
                        airRecord.setTypeName("国内机票");
                        airRecord.setOrderId((String) orderInfo.get("order_id"));
                        airRecord.setCompanyId(companyId);
                        airRecord.setOrderStatus(NumericUtils.obj2int(orderInfo.get("status")));
                        airRecord.setCompanyName(companyCreatVo.getCompanyName());
                        airRecord.setContactName((String) passengerInfo.get("name"));
                        airRecord.setUserName((String) userInfo.get("name"));
                        airRecord.setCallbackType(CallbackType.ORDER.getType());
                        airRecord.setCallbackData(JsonUtils.toJson(orderDetail));
                        callbackRecordDao.saveSelective(airRecord);
                        businessDataPushService.pushData(companyId, airRecord, 0, 4);
                        break;
                    case "15": //火车
                        orderInfo.put("order_category_type", 15);
                        orderInfo.put("order_category_name", "火车");
                        orderInfo.put("order_op_type", (Integer) orderInfo.get("status") == 3801 ? 20 : 10);
                        orderInfo.put("order_op_type_name", (Integer) orderInfo.get("status") == 3801 ? "逆向单" : "正向单");
                        ThirdCallbackRecord trainRecord = new ThirdCallbackRecord();
                        trainRecord.setType(15);
                        trainRecord.setTypeName("火车票");
                        trainRecord.setOrderId((String) orderInfo.get("order_id"));
                        trainRecord.setCompanyId(companyId);
                        trainRecord.setOrderStatus(NumericUtils.obj2int(orderInfo.get("status")));
                        trainRecord.setCompanyName(companyCreatVo.getCompanyName());
                        trainRecord.setContactName((String) passengerInfo.get("name"));
                        trainRecord.setUserName((String) userInfo.get("name"));
                        trainRecord.setCallbackType(CallbackType.ORDER.getType());
                        trainRecord.setCallbackData(JsonUtils.toJson(orderDetail));
                        callbackRecordDao.saveSelective(trainRecord);
                        businessDataPushService.pushData(companyId, trainRecord, 0, 4);
                        break;
                    case "11": //酒店
                        break;
                    case "3": //用车
                        break;
                    default:
                }
            }

        }
    }

    private Map getAirHistoryOrder(String orderId, String ticketId, boolean isIntl) {
        String url = null;
        if (!isIntl) {
            url = airBizHost + String.format("/stereo/domestic/air/orders/detail?order_id=" + orderId + "&ticket_id=" + ticketId);
        } else {
            url = airBizHost + String.format("/stereo/intl/air/orders/detail?order_id=" + orderId + "&ticket_id=" + ticketId);
        }
        String result = RestHttpUtils.get(url, MapUtils.newHashMap());
        Map data = JsonUtils.toObj(result, Map.class);
        log.info("getAirHistoryOrder orderId: {}, ticketId: {} result: {}", orderId, ticketId, JsonUtils.toJson(result));
        if (ObjectUtils.isEmpty(data)) {
            return Maps.newHashMap();
        }
        return data;
    }

    /**
     * 初始化查询条件 type 1 是部门   2 费用
     */
    public ViewReqDTO initFindConfig(String type, String filterValue, String companyId) {
        KingDeeK3CloudConfigDTO config = getConfig(companyId);
        ViewReqDTO department = config.getDepartment();
        ViewReqDTO.bean beanData = department.getData();
        if (type.equals("1")) {
            beanData.setFieldKeys("F_QDQX_CWAudit,F_JiPiao,F_JiuDian,F_HuoChe,F_YongChe,FNAME,FDEPTID,FNUMBER");
            beanData.setFilterString(beanData.getFilterString().concat(" and FNUMBER=").concat(filterValue));
        } else if (type.equals("2")) {
            beanData.setFormId("BD_Expense");
            beanData.setFieldKeys("FNUMBER");
            beanData.setFilterString("FEXPID=".concat(filterValue));
        }
        return department;
    }

    /**
     * 获取配置
     */
    public KingDeeK3CloudConfigDTO getConfig(String companyId) {
        // 获取配置信息
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("type", OpenSysConfigType.JINDIE_3kCLOUD_SYS_CONFIG.getType());
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(configMap);
        KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO = JsonUtils.toObj(openSysConfig.getValue(), KingDeeK3CloudConfigDTO.class);
        return kingDee3KCloudConfigDTO;
    }

    private Map getTrainHistoryOrder(String orderId, String ticketId) {
        String url = trainBizHost + String.format("/internal/orders/trains/order_all_detail?order_id=" + orderId + "&ticket_id=" + ticketId);
        String result = RestHttpUtils.get(url, MapUtils.newHashMap());
        Map data = JsonUtils.toObj(result, Map.class);
        log.info("getTrainHistoryOrder orderId: {}, ticketId: {} result: {}", orderId, ticketId, JsonUtils.toJson(result));
        if (ObjectUtils.isEmpty(data)) {
            return Maps.newHashMap();
        }
        return data;
    }

    private BigDecimal getPrice(Object object) {
        if (object instanceof Double) {
            return new BigDecimal((Double) object);
        } else if (object instanceof BigDecimal) {
            return (BigDecimal) object;
        } else {
            return new BigDecimal((Integer) object);
        }
    }

    /**
     * 递归从json串中获取key对应的value,可能是多层结构,多级key按xx.xx.xx传递
     * 这里使用的是alibaba.fastjson
     *
     * @param json json串
     * @param key  要获取value对应的key
     * @return
     */
    private String getValueFromJson(String json, String key) {
        String[] split = key.split("\\.");
        if (split.length > 1) {
            for (String s : split) {
                String targetKey = key.substring(key.indexOf(".") + 1);
                JSONObject jsonObject = JSONObject.parseObject(json);
                String string = jsonObject.getString(s);
                return getValueFromJson(string, targetKey);
            }
        }
        // 没有"." 直接取
        JSONObject jsonObject = JSONObject.parseObject(json);
        return jsonObject.getString(key);
    }

}
