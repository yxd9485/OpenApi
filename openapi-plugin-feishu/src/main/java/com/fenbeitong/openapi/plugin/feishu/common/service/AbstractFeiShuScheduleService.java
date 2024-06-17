package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.fenbeipay.api.constant.enums.cashier.OrderType;
import com.fenbeitong.finhub.kafka.msg.uc.schedule.ScheduleSceneAirDetail;
import com.fenbeitong.finhub.kafka.msg.uc.schedule.ScheduleSceneTrainDetail;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResultEntity;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.event.constant.ScheduleType;
import com.fenbeitong.openapi.plugin.event.schedule.dto.EmployeeScheduleDTO;
import com.fenbeitong.openapi.plugin.event.schedule.dto.ScheduleMsgDTO;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.dto.*;
import com.fenbeitong.openapi.plugin.feishu.common.dto.schedule.req.FeishuCalendarsReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.schedule.req.FeishuEventsAttendeeReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.schedule.req.FeishuEventsReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.schedule.resp.FeishuCalendarRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.schedule.resp.FeishuEventsRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuNoPermissionException;
import com.fenbeitong.openapi.plugin.feishu.common.util.AbstractFeiShuHttpUtils;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.support.schedule.dao.TbCalendarDao;
import com.fenbeitong.openapi.plugin.support.schedule.dao.TbEventDao;
import com.fenbeitong.openapi.plugin.support.schedule.entity.TbCalendar;
import com.fenbeitong.openapi.plugin.support.schedule.entity.TbEvent;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@ServiceAspect
@Service
@Slf4j
public abstract class AbstractFeiShuScheduleService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Value("${host.webapp}")
    private String webappHost;

    @Value("${feishu.isv.appId}")
    private String appId;

    @Autowired
    private TbCalendarDao calendarDao;

    @Autowired
    private TbEventDao tbEventDao;

    private static String FEISHU_ISV_APPLINK_HOME_URL = "https://applink.feishu.cn/client/mini_program/open?appId=%s&mode=window-semi&path=";

    /**
     * 获取FeiShuHttpUtils
     *
     * @return
     */
    protected abstract AbstractFeiShuHttpUtils getFeiShuHttpUtils();


    /**
     * 创建日历
     *
     * @param corpId
     * @param calendars
     * @return
     */
    public FeishuCalendarRespDTO createFeishuCalendar(String corpId, FeishuCalendarsReqDTO calendars) {
        String url = feishuHost + FeiShuConstant.ADD_CALENDARS;
        FeishuCalendarRespDTO calendarRespDTO = new FeishuCalendarRespDTO();
        try{
            String res = getFeiShuHttpUtils().postJsonWithTenantAccessToken(url,  JsonUtils.toJson(calendars), corpId);
            calendarRespDTO = JsonUtils.toObj(res, FeishuCalendarRespDTO.class);
            if (calendarRespDTO == null || !FeiShuResponseCode.FEISHU_SUCESS.equals(calendarRespDTO.getCode())) {
                throw new OpenApiFeiShuException(calendarRespDTO.getCode());
            }
            return calendarRespDTO;
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().value() == 400){
                log.warn("调用飞书接口没有权限：corpId={}" , corpId);
                //公司没有开通接口权限
                throw new OpenApiFeiShuNoPermissionException(RespCode.ERROR);
            }
        }
       return calendarRespDTO;
    }

    /**
     * 创建日程
     *
     * @param corpId
     * @param events
     * @return
     */
    public FeishuEventsRespDTO createFeishuEvents(String corpId, FeishuEventsReqDTO events , String calendarId) {
        String url = feishuHost + String.format(FeiShuConstant.ADD_EVENTS , calendarId);
        FeishuEventsRespDTO eventsRespDTO = new FeishuEventsRespDTO();
        try{
            String res = getFeiShuHttpUtils().postJsonWithTenantAccessToken(url,  JsonUtils.toJson(events), corpId);
            eventsRespDTO = JsonUtils.toObj(res, FeishuEventsRespDTO.class);
            if (eventsRespDTO == null || !FeiShuResponseCode.FEISHU_SUCESS.equals(eventsRespDTO.getCode())) {
                throw new OpenApiFeiShuException(eventsRespDTO.getCode());
            }
            return eventsRespDTO;
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().value() == 400){
                log.warn("调用飞书接口没有权限：corpId={}" , corpId);
                //公司没有开通接口权限
                throw new OpenApiFeiShuNoPermissionException(RespCode.ERROR);
            }
        }
        return eventsRespDTO;
    }

    /**
     * 修改日程
     *
     * @param corpId
     * @param events
     * @return
     */
    public FeishuEventsRespDTO updateFeishuEvents(String corpId, FeishuEventsReqDTO events, String calendarId , String eventsId) {
        String url = feishuHost + String.format(FeiShuConstant.UPDATE_EVENTS , calendarId , eventsId);
        String res = getFeiShuHttpUtils().patchWithTenantAccessToken(url,  JsonUtils.toJson(events), corpId);
        FeishuEventsRespDTO eventsRespDTO = JsonUtils.toObj(res, FeishuEventsRespDTO.class);
        if (eventsRespDTO == null || !FeiShuResponseCode.FEISHU_SUCESS.equals(eventsRespDTO.getCode())) {
            throw new OpenApiFeiShuException(eventsRespDTO.getCode());
        }
        return eventsRespDTO;
    }

    /**
     * 删除日程
     *
     * @param corpId
     * @param events
     * @return
     */
    public FeiShuRespDTO deleteFeishuEvents(String corpId, FeishuEventsReqDTO events, String calendarId , String eventsId) {
        String url = feishuHost +  String.format(FeiShuConstant.DELETE_EVENTS , calendarId , eventsId);
        String res = getFeiShuHttpUtils().deleteWithTenantAccessToken(url , corpId);
        FeiShuRespDTO eventsRespDTO = JsonUtils.toObj(res, FeiShuRespDTO.class);
        if (eventsRespDTO == null || !FeiShuResponseCode.FEISHU_SUCESS.equals(eventsRespDTO.getCode())) {
            throw new OpenApiFeiShuException(eventsRespDTO.getCode());
        }
        return eventsRespDTO;
    }

    /**
     * 创建日程参与人员
     *
     * @param corpId
     * @param eventsAttendee
     * @return
     */
    public FeiShuRespDTO createFeishuEventsAttendees(Integer openType , String corpId, FeishuEventsAttendeeReqDTO eventsAttendee , String calendarId , String eventsId) {
        String userType = "open_id";
        if(OpenType.FEISHU_EIA.getType() == openType){
            userType = "user_id";
        }
        String url = feishuHost + String.format(FeiShuConstant.EVENTS_ATTENDEES , calendarId , eventsId , userType);
        String res = getFeiShuHttpUtils().postJsonWithTenantAccessToken(url,  JsonUtils.toJson(eventsAttendee), corpId);
        FeiShuRespDTO resp = JsonUtils.toObj(res, FeiShuRespDTO.class);
        if (resp == null || !FeiShuResponseCode.FEISHU_SUCESS.equals(resp.getCode())) {
            throw new OpenApiFeiShuException(resp.getCode());
        }
        return resp;
    }

    /**
     * 解析日程信息
     *
     * @return
     */
    public OpenapiResultEntity parseScheduleInfo(ScheduleMsgDTO data) {
        try{
            String corpId = data.getCorpId();
            TbCalendar calendar = createCalendar(data);
            List<EmployeeScheduleDTO> scheduleList = data.getScheduleList();
            for(EmployeeScheduleDTO employeeSchedule : scheduleList){
                Integer scheduleType = employeeSchedule.getScheduleType();
                if(ScheduleType.SCHEDULE_CREATE_TYPE.equals(scheduleType)){
                    //新增
                    Integer openType = data.getOpenType();
                    createSchdule( employeeSchedule, calendar , openType , corpId);
                }else if(ScheduleType.SCHEDULE_DELETE_TYPE.equals(scheduleType)){
                    //删除
                    String eventId = employeeSchedule.getEventId();
                    deleteFeishuEvents(corpId, null, calendar.getCalendarId(), employeeSchedule.getEventId());
                    tbEventDao.updateEventStatus( eventId , scheduleType);
                }else if(ScheduleType.SCHEDULE_UPDATE_TYPE.equals(scheduleType)){
                    //修改
                    updateEvents( employeeSchedule ,  calendar ,  corpId);
                    tbEventDao.updateEventStatus( employeeSchedule.getEventId() , scheduleType);
                }
            }
        }catch (OpenApiFeiShuNoPermissionException e){
            //没有开通接口权限，返回正常，不再进行重试
            return OpenapiResponseUtils.success(null);
        }catch (Exception e){
            e.printStackTrace();
            return OpenapiResponseUtils.error(RespCode.ERROR , e.getMessage());
        }
        return OpenapiResponseUtils.success(null);
    }

    /**
     * 创建日历信息
     * @param data
     */
    private TbCalendar createCalendar(ScheduleMsgDTO data){
        String corpId = data.getCorpId();
        String companyId = data.getCompanyId();
        //通过公司id查询日历表中是否存在
        TbCalendar calendar = calendarDao.getTbCalendarByCompanyId(companyId);
        if(calendar == null || StringUtils.isBlank(calendar.getCalendarId())){
            //创建日历信息
            FeishuCalendarsReqDTO calendarReq = new FeishuCalendarsReqDTO();
            calendarReq.setSummary(data.getCompanyName());
            FeishuCalendarRespDTO feishuCalendar = createFeishuCalendar(corpId, calendarReq);
            String calendarId = feishuCalendar.getData().getCalendar().getCalendarId();
            calendar = TbCalendar.builder().id(RandomUtils.bsonId()).calendarId(calendarId).calendarTitle(data.getCompanyName()).companyId(companyId).createTime(new Date()).build();
            calendarDao.save(calendar);
        }
        return calendar;
    }

    /**
     * 创建日程信息
     * @param employeeSchedule
     * @param calendar
     */
    private void createSchdule(EmployeeScheduleDTO employeeSchedule,TbCalendar calendar , Integer openType , String corpId){
        String companyId = employeeSchedule.getCompanyId();
        String employeeId = employeeSchedule.getEmployeeId();
        String thirdEmployeeId = employeeSchedule.getThirdEmployeeId();
        //调用飞书接口新增日程
        FeishuEventsReqDTO feishuEventsReqDTO = buildEvents( employeeSchedule , openType , corpId);
        FeishuEventsRespDTO feishuEvents = createFeishuEvents(corpId, feishuEventsReqDTO , calendar.getCalendarId());
        //日程绑定人员
        FeishuEventsAttendeeReqDTO eventsAttendee = new FeishuEventsAttendeeReqDTO();
        FeishuEventsAttendeeReqDTO.Attendees attendees = new FeishuEventsAttendeeReqDTO.Attendees();
        attendees.setType("user");
        attendees.setUserId(thirdEmployeeId);
        List<FeishuEventsAttendeeReqDTO.Attendees> list =  new ArrayList<>();
        list.add(attendees);
        eventsAttendee.setAttendees(list);
        eventsAttendee.setNeedNotification(true);
        createFeishuEventsAttendees( openType , corpId,  eventsAttendee , calendar.getCalendarId() , feishuEvents.getData().getEvent().getEventId());
        //新增日程表中数据
        TbEvent events = TbEvent.builder().id(RandomUtils.bsonId())
            .calendarId(calendar.getCalendarId())
            .eventId(feishuEvents.getData().getEvent().getEventId())
            .employeeId(employeeId)
            .thirdEmployeeId(thirdEmployeeId)
            .companyId(companyId)
            .orderId(employeeSchedule.getOrderId())
            .eventTitle(feishuEventsReqDTO.getSummary())
            .eventDescription(feishuEventsReqDTO.getDescription())
            .status(ScheduleType.SCHEDULE_CREATE_TYPE)
            .createTime(new Date())
            .updateTime(new Date())
            .build();
        Integer businessType = employeeSchedule.getBusinessType();
        if(ScheduleType.SCHEDULE_BUSSINESS_AIR_TYPE.equals(businessType)){
            events.setFlightNumber(employeeSchedule.getScheduleSceneDetail().getAir().getFlightNumber());
            events.setStartDate(employeeSchedule.getStartDate());
            events.setStartTime(employeeSchedule.getStartTime());
            events.setEndTime(employeeSchedule.getEndTime());
        }
        tbEventDao.save(events);
    }

    /**
     * 修改日程信息
     * @param employeeSchedule
     * @param calendar
     * @param corpId
     */
    private void updateEvents(EmployeeScheduleDTO employeeSchedule , TbCalendar calendar , String corpId){
        String plannedStartTime = employeeSchedule.getPlannedStartTime();
        FeishuEventsReqDTO.TimeInfo timeInfo = new FeishuEventsReqDTO.TimeInfo();
        Date date = DateUtils.toDate(plannedStartTime, DateUtils.FORMAT_DATE_TIME_PATTERN);
        timeInfo.setTimestamp(StringUtils.obj2str(date.getTime()/1000));
        String plannedEndTime = employeeSchedule.getPlannedEndTime();
        Date endDate = DateUtils.toDate(plannedEndTime, DateUtils.FORMAT_DATE_TIME_PATTERN);
        timeInfo.setTimestamp(StringUtils.obj2str(endDate.getTime()/1000));
        FeishuEventsReqDTO feishuEventsReqDTO = FeishuEventsReqDTO.builder().startTime(timeInfo).endTime(timeInfo).build();
        updateFeishuEvents( corpId,  feishuEventsReqDTO,  calendar.getCalendarId() ,  employeeSchedule.getEventId());
    }

    private FeishuEventsReqDTO buildEvents(EmployeeScheduleDTO employeeSchedule , Integer openType , String corpId){
        Integer businessType = employeeSchedule.getBusinessType();
        String summary = "";
        if(ScheduleType.SCHEDULE_BUSSINESS_AIR_TYPE.equals(businessType)){
            //国内机票
            ScheduleSceneAirDetail air = employeeSchedule.getScheduleSceneDetail().getAir();
            summary = buildAirSummary( air );
        }else if(ScheduleType.SCHEDULE_BUSSINESS_TRAIN_TYPE.equals(businessType)){
            //火车
            ScheduleSceneTrainDetail train = employeeSchedule.getScheduleSceneDetail().getTrain();
            summary = buildTrainSummary( train );
        }
        return buildEvents(summary, employeeSchedule , openType , corpId);
    }

    private String buildAirSummary(ScheduleSceneAirDetail air){
        //航班号
        String flightNumber = air.getFlightNumber();
        //出发机场
        String startAirportName = air.getStartAirportName();
        //出发航站楼
        String startAirportTerminal =  StringUtils.isBlank( air.getStartAirportTerminal() ) ? "" : air.getStartAirportTerminal();
        //到达机场
        String arrivalAirportAbbreviation = air.getArrivalAirportName();
        //到达航站楼
        String arrivalAirportTerminal = StringUtils.isBlank( air.getArrivalAirportTerminal() ) ? "" : air.getArrivalAirportTerminal() ;
        StringBuilder airSummary = new StringBuilder();
        airSummary.append(flightNumber);
        airSummary.append(": ");
        airSummary.append(startAirportName);
        airSummary.append(startAirportTerminal);
        airSummary.append("-");
        airSummary.append(arrivalAirportAbbreviation);
        airSummary.append(arrivalAirportTerminal);
        return airSummary.toString();
    }

    private String buildTrainSummary(ScheduleSceneTrainDetail train){
        //列车号
        String trainNumber = train.getTrainNumber();
        //出发站点
        String startSiteName = train.getStartSiteName();
        //到达站点
        String arrivalSiteName = train.getArrivalSiteName();
        //车厢：05车厢 12C
        String seatName = train.getSeatName();
        //检票口
        String ticketGate = train.getTicketGate();
        StringBuilder trainSummary = new StringBuilder();
        trainSummary.append(trainNumber);
        trainSummary.append("车次");
        trainSummary.append(" ");
        trainSummary.append(startSiteName);
        trainSummary.append("-");
        trainSummary.append(arrivalSiteName);
        trainSummary.append(" ");
        trainSummary.append(seatName);
        if(!StringUtils.isBlank(ticketGate) && !"-".equals(ticketGate)){
            trainSummary.append(" 检票口：");
            ticketGate = ticketGate.replaceAll("检票口：", "");
            ticketGate = ticketGate.replaceAll("检票口", "");
            trainSummary.append(ticketGate);
        }
        return trainSummary.toString();
    }

    private FeishuEventsReqDTO buildEvents(String summary , EmployeeScheduleDTO employeeSchedule, Integer openType , String corpId){
        String startTime = employeeSchedule.getStartTime();
        String endTime = employeeSchedule.getEndTime();
        String orderId = employeeSchedule.getOrderId();
        Integer businessType = employeeSchedule.getBusinessType();
        FeishuEventsReqDTO.TimeInfo startTimeInfo = setTimeInfo(startTime);
        FeishuEventsReqDTO.TimeInfo endTimeInfo = setTimeInfo(endTime);
        String description = buildDescription( openType , corpId , orderId , businessType);
        FeishuEventsReqDTO feishuEventsReqDTO = FeishuEventsReqDTO.builder().summary(summary).startTime(startTimeInfo).endTime(endTimeInfo).description(description).needNotification(true).build();
        return feishuEventsReqDTO;
    }

    private FeishuEventsReqDTO.TimeInfo setTimeInfo(String time){
        FeishuEventsReqDTO.TimeInfo timeInfo = new FeishuEventsReqDTO.TimeInfo();
        Date date = DateUtils.toDate(time, DateUtils.FORMAT_DATE_TIME_PATTERN);
        timeInfo.setTimestamp(StringUtils.obj2str(date.getTime()/1000));
        return timeInfo;
    }

    private String buildDescription(Integer openType , String corpId,String orderId , Integer businessType){
        String jumpUrl = getJumpUrl(openType, corpId, orderId, businessType);
        StringBuilder descrition = new StringBuilder();
        descrition.append("订单详情：");
        descrition.append("<a href='"+jumpUrl+"'>点击查看订单详情</a>");
        descrition.append("\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(详情、退票、改签)");
        descrition.append("\n\n");
        descrition.append("温馨提示\n");
        if(ScheduleType.SCHEDULE_BUSSINESS_AIR_TYPE.equals(businessType)){
            //国内机票
            descrition.append("-航班变动，请以航司或机场公布信息为准");
        }else if(ScheduleType.SCHEDULE_BUSSINESS_TRAIN_TYPE.equals(businessType)){
            //火车
            descrition.append("-列车变动，请以车站公布信息为准");
        }
        return descrition.toString();
    }

    /**
     * 判断是内嵌版还是市场版
     * @param openType
     * @return
     */
    private String getJumpUrl(Integer openType , String corpId,String orderId , Integer businessType){
        Integer orderType = -1;
        if(ScheduleType.SCHEDULE_BUSSINESS_AIR_TYPE.equals(businessType)){
            //国内机票
            orderType = OrderType.Air.getKey();
        }else if(ScheduleType.SCHEDULE_BUSSINESS_TRAIN_TYPE.equals(businessType)){
            //火车
            orderType = OrderType.Train.getKey();
        }
        Map eventMsgMap = new HashMap();
        eventMsgMap.put("order_type",orderType);
        eventMsgMap.put("order_id", orderId);
        String messageUrl = MessagePushUtils.initOrderUrl(eventMsgMap, "");
        if (!StringUtils.isBlank(messageUrl)) {
            if(OpenType.FEISHU_EIA.getType() == openType){
                //内嵌版
                String uri = webappHost + String.format(FeiShuConstant.FEISHU_EIA_APP_HOME, corpId);
                messageUrl = messageUrl.replace("url=", "redirectFbtUrl=");
                return uri + messageUrl;
            }else if(OpenType.FEISHU_ISV.getType() == openType){
                //市场版
                String uri =  String.format(FEISHU_ISV_APPLINK_HOME_URL, appId );
                String loginUrl = "pages/login/index?";
                messageUrl =  messageUrl.replace("?", "&");
                messageUrl = loginUrl + messageUrl.replace("url=", "redirectFbtUrl=");
                String encodeUrl = null;
                try {
                    encodeUrl = URLEncoder.encode(messageUrl, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return  uri + encodeUrl;
            }
        }
        return null;
    }

}
