package com.fenbeitong.openapi.plugin.customize.wawj.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.noc.car.model.req.CarButtSimpleOrderReq;
import com.fenbeitong.noc.car.model.res.CarButtSimpleOrdersRes;
import com.fenbeitong.noc.car.service.ICarButtJointService;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjCarOrderSensitiveService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceDao;
import com.fenbeitong.openapi.plugin.support.voucher.entity.Attendance;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: WawjCarOrderSensitiveServiceImpl</p>
 * <p>Description: 我爱我家用车敏感订单服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/5 11:54 AM
 */
@ServiceAspect
@Service
public class WawjCarOrderSensitiveServiceImpl implements IWawjCarOrderSensitiveService {

    @DubboReference(check = false)
    private ICarButtJointService carButtJointService;

    @Autowired
    private AttendanceDao attendanceDao;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Async
    @Override
    public void setSensitive(String companyId, int day) {
        LocalDateTime nowDayZeroTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
        LocalDateTime monthStartTime = nowDayZeroTime.minusDays(nowDayZeroTime.getDayOfMonth() - 1);
        LocalDateTime lastMonthEnd = monthStartTime.minusSeconds(1);
        int dayOfMonth = lastMonthEnd.getDayOfMonth();
        LocalDateTime lastMonthStart = LocalDateTime.of(lastMonthEnd.minusDays(dayOfMonth - 1).toLocalDate(), LocalTime.of(0, 0, 0));
        Date startDate = Date.from(lastMonthStart.plusDays(day - 1).atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastMonthEnd.plusDays(day - 1).atZone(ZoneId.systemDefault()).toInstant());
        //上个月的用车订单
        List<CarButtSimpleOrdersRes> carOrderList = getCarOrderList(companyId, startDate, endDate);
        if (ObjectUtils.isEmpty(carOrderList)) {
            return;
        }
        //涉及到的员工
        Set<String> employeeList = carOrderList.stream().map(CarButtSimpleOrdersRes::getEmployeeId).collect(Collectors.toSet());
        //员工对应的考勤
        List<Attendance> attendanceList = getAttendanceList(companyId, Lists.newArrayList(employeeList), DateUtils.addDay(startDate, -1), endDate);
        //分析结果并设置标记
        setCarOrderSensitive(companyId, carOrderList, attendanceList);
    }

    private void setCarOrderSensitive(String companyId, List<CarButtSimpleOrdersRes> carOrderList, List<Attendance> attendanceList) {
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("wiwj_customize_baoxiao_info"));
        Map baoxiaoInfo = JsonUtils.toObj(openMsgSetups.get(0).getStrVal1(), Map.class);
        //用车类型
        Integer useCarType = NumericUtils.obj2int(baoxiaoInfo.get("useCarType"), 1);
        //深夜用车类型
        Integer useCarLateNightType = NumericUtils.obj2int(baoxiaoInfo.get("useCarLateNightType"), 4);
        Map<String, List<CarButtSimpleOrdersRes>> employeeCarOrderMap = carOrderList.stream().collect(Collectors.groupingBy(CarButtSimpleOrdersRes::getEmployeeId, LinkedHashMap::new, Collectors.toList()));
        Map<String, List<Attendance>> attendanceMap = attendanceList.stream().collect(Collectors.groupingBy(Attendance::getEmployeeId));
        List<String> sensitiveOrderIdList = Lists.newArrayList();
        List<String> sensitiveMsgList = Lists.newArrayList();
        String msgFormat = "订单[{0}] 日期[{1}] 员工[{2}] 原因[{3}]";
        employeeCarOrderMap.forEach((employeeId, employeeCarOrderList) -> {
            List<Attendance> employeeAttendanceList = attendanceMap.get(employeeId);
            Map<Date, Attendance> dateAttendanceMap = ObjectUtils.isEmpty(employeeAttendanceList) ? null : employeeAttendanceList.stream().collect(Collectors.toMap(Attendance::getWorkDate, Function.identity()));
            employeeCarOrderList.forEach(order -> {
                Integer sceneCode = order.getSceneCode();
                String sceneName = useCarType.equals(sceneCode) ? "用车" : useCarLateNightType.equals(sceneCode) ? "深夜用车" : "";
                //上车时间
                Date createTime = order.getCreateTime();
                //获取当天日期
                Date workDate = DateUtils.toDate(DateUtils.toSimpleStr(createTime, true));
                //早上5点
                Date fiveAm = DateUtils.addMinute(workDate, 300);
                //00:00-05:00 匹配前一个工作日
                if (createTime.compareTo(fiveAm) <= 0) {
                    workDate = DateUtils.addDay(workDate, -1);
                }
                //获取对应的考勤记录
                Attendance attendance = ObjectUtils.isEmpty(dateAttendanceMap) ? null : dateAttendanceMap.get(workDate);
                //未打卡
                if (attendance == null || attendance.getUserCheckOutTime() == null) {
                    String msg = sceneName + MessageFormat.format(msgFormat, order.getOrderId(), DateUtils.toSimpleStr(workDate, true), order.getEmployeeId(), "未能成功匹配考勤记录");
                    sensitiveOrderIdList.add(order.getOrderId());
                    sensitiveMsgList.add(msg);
                } else {
                    Integer status = attendance.getStatus();
                    if (status == -1) {
                        String msg = sceneName + MessageFormat.format(msgFormat, order.getOrderId(), DateUtils.toSimpleStr(workDate, true), order.getEmployeeId() + ":" + attendance.getEmployeeName(), "没有审批通过的加班申请");
                        sensitiveOrderIdList.add(order.getOrderId());
                        sensitiveMsgList.add(msg);
                    } else {
                        //打卡时间
                        Date userCheckOutTime = attendance.getUserCheckOutTime();
                        Date workDate_21_00 = DateUtils.addHour(workDate, 21);
                        Date workDate_22_30 = DateUtils.addMinute(DateUtils.addHour(workDate, 22), 30);
                        //用车类型 打车时间早于21点
                        if (useCarType.equals(sceneCode) && userCheckOutTime.compareTo(workDate_21_00) < 0) {
                            String msg = sceneName + MessageFormat.format(msgFormat, order.getOrderId(), DateUtils.toSimpleStr(workDate, true), order.getEmployeeId() + ":" + attendance.getEmployeeName(), "下班打卡时间" + DateUtils.toStr(userCheckOutTime, "HH:mm:ss") + "早于21:00");
                            sensitiveOrderIdList.add(order.getOrderId());
                            sensitiveMsgList.add(msg);
                        }
                        //深夜用车类型 打车时间早于22：30点
                        else if (useCarLateNightType.equals(sceneCode) && userCheckOutTime.compareTo(workDate_22_30) < 0) {
                            String msg = sceneName + MessageFormat.format(msgFormat, order.getOrderId(), DateUtils.toSimpleStr(workDate, true), order.getEmployeeId() + ":" + order.getEmployeeId() + ":" + attendance.getEmployeeName(), "下班打卡时间" + DateUtils.toStr(userCheckOutTime, "HH:mm:ss") + "早于22:30");
                            sensitiveOrderIdList.add(order.getOrderId());
                            sensitiveMsgList.add(msg);
                        }
                    }
                }
            });
        });
        if (!ObjectUtils.isEmpty(sensitiveOrderIdList)) {
            List<List<String>> batchIdList = CollectionUtils.batch(sensitiveOrderIdList, 100);
            batchIdList.forEach(carButtJointService::strikeSensitiveCarOrders);
        }
        if (!ObjectUtils.isEmpty(sensitiveMsgList)) {
            String msg = "我爱我家用车敏感订单\n" + String.join("\n", sensitiveMsgList);
            exceptionRemind.remindDingTalk(msg);
        }
    }

    private List<Attendance> getAttendanceList(String companyId, List<String> employeeList, Date startDate, Date endDate) {
        List<Attendance> attendanceList = attendanceDao.listRangeAttendance(companyId, employeeList, DateUtils.toDate(DateUtils.toSimpleStr(startDate, true)), DateUtils.toDate(DateUtils.toSimpleStr(endDate, true)));
        return attendanceList == null ? Lists.newArrayList() : attendanceList;
    }

    private List<CarButtSimpleOrdersRes> getCarOrderList(String companyId, Date startDate, Date endDate) {
        CarButtSimpleOrderReq req = new CarButtSimpleOrderReq();
        req.setLimit(100);
        req.setPageIndex(1);
        req.setCompanyId(companyId);
        req.setCreateTimeFrom(startDate);
        req.setCreateTimeTo(endDate);
        List<CarButtSimpleOrdersRes> carOrderList = Lists.newArrayList();
        List<CarButtSimpleOrdersRes> carOrderPageList = carButtJointService.queryButtSimpleOrder(req);
        while (!ObjectUtils.isEmpty(carOrderPageList)) {
            carOrderList.addAll(carOrderPageList);
            req.setPageIndex(req.getPageIndex() + 1);
            carOrderPageList = carButtJointService.queryButtSimpleOrder(req);
        }
        return carOrderList;
    }
}
