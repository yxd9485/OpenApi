package com.fenbeitong.openapi.plugin.customize.wawj.service.impl;

import com.fenbeitong.openapi.plugin.customize.wawj.constant.WawjShiftType;
import com.fenbeitong.openapi.plugin.customize.wawj.dao.OpenWawjFuliApplyDao;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.*;
import com.fenbeitong.openapi.plugin.customize.wawj.entity.OpenWawjFuliApply;
import com.fenbeitong.openapi.plugin.customize.wawj.service.IWawjKaoQinShenQingService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.EmployeeUtils;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceDao;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceGrantVoucherRulesDao;
import com.fenbeitong.openapi.plugin.support.voucher.entity.Attendance;
import com.fenbeitong.openapi.plugin.support.voucher.entity.AttendanceGrantVoucherRule;
import com.fenbeitong.openapi.plugin.support.voucher.service.IGrantVoucherService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: WawjKaoQinShenQingServiceImpl</p>
 * <p>Description: 我爱我家考勤同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/26 6:24 PM
 */
@ServiceAspect
@Service
@Slf4j
public class WawjKaoQinShenQingServiceImpl implements IWawjKaoQinShenQingService {

    @Autowired
    private AttendanceDao attendanceDao;

    @Autowired
    private AttendanceGrantVoucherRulesDao rulesDao;

    @Autowired
    private OpenWawjFuliApplyDao applyDao;

    @DubboReference(check = false)
    private ICommonService commonService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @Autowired
    private IGrantVoucherService grantVoucherService;

    @Autowired
    private OpenWawjFuliApplyDao wiwjFuliApplyDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private EmployeeUtils employeeUtils;

    @Async
    @Override
    public void synKaoQin(WawjKaoQinSyncReqDTO req) {
        List<WawjKaoQinDTO> attendanceList = req.getAttendanceList();
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(req.getCompanyId(), Lists.newArrayList("wiwj_direct_grant_voucher_expr"));
        //直接发券表达式
        String wiwjDirectGrantVoucherExpr = openMsgSetups.get(0).getStrVal1();
        //考勤及申请有效时间
        int expiredDays = openMsgSetups.get(0).getIntVal1() == null ? 90 : openMsgSetups.get(0).getIntVal1();
        List<CommonIdDTO> commonIdDtos = commonService.queryIdDTO(req.getCompanyId(), attendanceList.stream().map(WawjKaoQinDTO::getThirdEmployeeId).collect(Collectors.toList()), 2, 3);
        List<EmployeeContract> employeeList = ObjectUtils.isEmpty(commonIdDtos) ? null : employeeUtils.queryEmployees(commonIdDtos.stream().map(CommonIdDTO::getThirdId).collect(Collectors.toList()), req.getCompanyId());
        Map<String, EmployeeContract> employeeMap = ObjectUtils.isEmpty(employeeList) ? Maps.newHashMap() : employeeList.stream().collect(Collectors.toMap(EmployeeContract::getThird_employee_id, Function.identity()));
        attendanceList.forEach(attendanceDto -> {
            Attendance attendance = new Attendance();
            attendance.setCompanyId(req.getCompanyId());
            attendance.setEmployeeName(attendanceDto.getName());
            EmployeeContract employee = employeeMap.get(attendanceDto.getThirdEmployeeId());
            if (employee != null) {
                attendance.setEmployeeId(employee.getId());
                attendance.setEmployeePhone(employee.getPhone_num());
                attendance.setCompanyName(employee.getCompany_name());
            }
            //日期类型 1:工作日;2:周末;3:法定假期
            Integer dateType = attendanceDto.getDateType();
            //班次类型 1:弹性班次;2:标准班次;3:管理班次;4:高管班次
            Integer shiftType = -1;
            //班次描述
            String shiftTypeDesc = attendanceDto.getShiftTypeDesc();
            if (!ObjectUtils.isEmpty(shiftTypeDesc)) {
                WawjShiftType wawjShiftType = WawjShiftType.getByName(shiftTypeDesc);
                shiftType = wawjShiftType == null ? -1 : wawjShiftType.getShiftTypeByDateType(dateType);
            }
            attendance.setThirdUserId(attendanceDto.getThirdEmployeeId());
            attendance.setWorkDate(DateUtils.toDate(attendanceDto.getWorkDate()));
            attendance.setDateType(dateType);
            attendance.setShiftType(shiftType);
            attendance.setShiftTypeDesc(shiftTypeDesc);
            attendance.setUserCheckInTime(DateUtils.toDate(attendanceDto.getCheckInTime()));
            attendance.setUserCheckOutTime(DateUtils.toDate(attendanceDto.getCheckoutTime()));
            BigDecimal workHours = attendanceDto.getWorkHours() == null ? BigDecimal.ZERO : attendanceDto.getWorkHours();
            attendance.setUserWorkTimeMinutes(workHours.multiply(new BigDecimal(60)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
            attendance.setGrantVoucherFlag(1);
            attendance.setType(attendanceDto.getType());
            int status = -1;
            //平日-微弹/管理  周末-管理 按照考勤规则直接发券 设置状态为0 即可
            if (dateType != null) {
                ExpressionParser parser = new SpelExpressionParser();
                StandardEvaluationContext context = new StandardEvaluationContext();
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put("dateType", dateType);
                dataMap.put("shiftType", shiftType);
                dataMap.put("shiftTypeDesc", shiftTypeDesc);
                context.setVariable("data", dataMap);
                Boolean directGrantVoucher = parser.parseExpression(wiwjDirectGrantVoucherExpr).getValue(context, Boolean.class);
                if (directGrantVoucher != null && directGrantVoucher) {
                    status = 0;
                }
            }
            attendance.setStatus(status);
            attendance.setAttr1(attendanceDto.getAttr1());
            attendance.setAttr2(attendanceDto.getAttr2());
            attendance.setAttr3(attendanceDto.getAttr3());
            attendance.setAttr4(attendanceDto.getAttr4());
            attendance.setAttr5(attendanceDto.getAttr5());
            Attendance oldAttendance = attendanceDao.getOldAttendance(attendance.getCompanyId(), attendance.getThirdUserId(), attendance.getWorkDate());
            if (oldAttendance == null) {
                closeAttendance(attendance, status, -expiredDays);
                attendanceDao.saveSelective(attendance);
            } else {
                attendance.setId(oldAttendance.getId());
                attendance.setStatus(oldAttendance.getStatus());
                closeAttendance(attendance, attendance.getStatus(), -expiredDays);
                attendanceDao.updateById(attendance);
            }
        });
    }

    private void closeAttendance(Attendance attendance, int status, int minusDays) {
        Date nowDate = DateUtils.now(true);
        Date lastDate = DateUtils.addDay(nowDate, minusDays);
        boolean ninetyDaysAgo = attendance.getWorkDate().compareTo(lastDate) < 0;
        //90天以前的直接关闭
        if (status == 0 && ninetyDaysAgo) {
            attendance.setStatus(2);
        }
    }

    @Async
    @Override
    public void synFuLiShenQing(WawjFuLiShenQingSyncReqDTO req) {
        List<WawjFuLiShenQingDTO> applyList = req.getApplyList();
        List<CommonIdDTO> commonIdDtos = commonService.queryIdDTO(req.getCompanyId(), applyList.stream().map(WawjFuLiShenQingDTO::getThirdEmployeeId).collect(Collectors.toList()), 2, 3);
        List<EmployeeContract> employeeList = ObjectUtils.isEmpty(commonIdDtos) ? null : employeeUtils.queryEmployees(commonIdDtos.stream().map(CommonIdDTO::getThirdId).collect(Collectors.toList()), req.getCompanyId());
        Map<String, EmployeeContract> employeeMap = ObjectUtils.isEmpty(employeeList) ? Maps.newHashMap() : employeeList.stream().collect(Collectors.toMap(EmployeeContract::getThird_employee_id, Function.identity()));
        applyList.forEach(applyDto -> {
            OpenWawjFuliApply apply = new OpenWawjFuliApply();
            apply.setApplyId(applyDto.getApplyId());
            apply.setCompanyId(req.getCompanyId());
            apply.setEmployeeName(applyDto.getName());
            EmployeeContract employee = employeeMap.get(applyDto.getThirdEmployeeId());
            if (employee != null) {
                apply.setEmployeeId(employee.getId());
                apply.setEmployeePhone(employee.getPhone_num());
                apply.setCompanyName(employee.getCompany_name());
                apply.setThirdUserId(employee.getThird_employee_id());
            }
            apply.setStatus(-1);
            apply.setType(applyDto.getType());
            apply.setWorkDate(DateUtils.toDate(applyDto.getWorkDate()));
            apply.setApplyTime(DateUtils.toDate(applyDto.getApplyTime()));
            OpenWawjFuliApply oldApply = applyDao.getOldApply(req.getCompanyId(), apply.getThirdUserId(), applyDto.getApplyId(), apply.getWorkDate());
            if (oldApply == null) {
                applyDao.saveSelective(apply);
            } else {
                apply.setStatus(oldApply.getStatus());
                apply.setId(oldApply.getId());
                applyDao.updateById(apply);
            }
        });
    }

    @Async
    @Override
    public void updateAttendanceApply(String companyId) {
        List<OpenWawjFuliApply> applyList = wiwjFuliApplyDao.listApply(companyId, -1);
        if (ObjectUtils.isEmpty(applyList)) {
            return;
        }
        Map<Date, List<OpenWawjFuliApply>> applyMap = applyList.stream().filter(a -> a.getWorkDate() != null).collect(Collectors.groupingBy(OpenWawjFuliApply::getWorkDate));
        applyMap.forEach((workDate, dateApplyList) -> {
            List<String> employeeIdList = dateApplyList.stream().filter(a -> a.getEmployeeId() != null).map(OpenWawjFuliApply::getEmployeeId).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(employeeIdList)) {
                Map<String, Attendance> attendanceMap = loadEmployeeAttendance(companyId, employeeIdList, workDate);
                //更新审批单及考勤状态
                updateApplyAttendanceStatus(dateApplyList, attendanceMap);
            }
        });
    }

    private void updateApplyAttendanceStatus(List<OpenWawjFuliApply> applyList, Map<String, Attendance> attendanceMap) {
        applyList.forEach(apply -> {
            String employeeId = apply.getEmployeeId();
            if (!ObjectUtils.isEmpty(employeeId)) {
                Attendance attendance = attendanceMap.get(employeeId);

                if (attendance != null) {
                    Date userCheckOutTime = attendance.getUserCheckOutTime();
                    Integer userWorkTimeMinutes = attendance.getUserWorkTimeMinutes();
                    //平日-微弹/管理  周末-管理
                    Integer shiftType = attendance.getShiftType();
                    //有效的考勤 高管的考勤都是无效的
                    boolean validAttendance = userCheckOutTime != null || userWorkTimeMinutes != null && userWorkTimeMinutes > 0 && shiftType != null && shiftType != 4;
                    log.info("我爱我家待更新状态的福利审批单:{}", JsonUtils.toJson(apply));
                    if(!ObjectUtils.isEmpty(apply.getType()) && 2==apply.getType()){
                        //延时晚到
                        attendance.setType(2);
                        if(WawjShiftType.KGZH_SHIFT.getShiftName().equals(attendance.getShiftTypeDesc())){
                            //延时晚到的班次类型为弹性班次
                            attendance.setShiftType(1);
                        }
                    }
                    if (validAttendance && attendance.getStatus() == -1) {
                        OpenWawjFuliApply updateApply = new OpenWawjFuliApply();
                        updateApply.setId(apply.getId());
                        updateApply.setStatus(0);
                        wiwjFuliApplyDao.updateById(updateApply);
                        Attendance updateAttendance = new Attendance();
                        updateAttendance.setId(attendance.getId());
                        updateAttendance.setStatus(0);
                        //更新延时晚到类型
                        updateAttendance.setType(attendance.getType());
                        //更新班次类型
                        updateAttendance.setShiftType(attendance.getShiftType());
                        attendanceDao.updateById(updateAttendance);
                    }
                }
            }
        });
    }

    private Map<String, Attendance> loadEmployeeAttendance(String companyId, List<String> employeeIdList, Date workDate) {
        Map<String, Attendance> attendanceMap = Maps.newHashMap();
        List<Attendance> attendanceList = attendanceDao.listUserWorkDateAttendance(companyId, employeeIdList, workDate);
        if (!ObjectUtils.isEmpty(attendanceList)) {
            attendanceMap = attendanceList.stream().collect(Collectors.toMap(Attendance::getEmployeeId, Function.identity()));
        }
        return attendanceMap;
    }

    @Async
    @Override
    public void grantVoucher(String companyId) {
        List<Attendance> attendanceList = attendanceDao.listNeedVoucherAttendance(companyId);
        grantVoucherByAttendance(companyId, attendanceList);
    }

    @Async
    @Override
    public void grantVoucherByUser(WawjGrantVoucherByUserReq req) {
        List<Attendance> attendanceList = attendanceDao.listNeedVoucherAttendance(req.getCompanyId());
        if (ObjectUtils.isEmpty(attendanceList)) {
            return;
        }
        List<String> userIdList = req.getUserIdList() == null ? Lists.newArrayList() : req.getUserIdList();
        List<Attendance> filteredAttendanceList = attendanceList.stream().filter(attendance -> userIdList.contains(attendance.getEmployeeId())).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(filteredAttendanceList)) {
            return;
        }
        grantVoucherByAttendance(req.getCompanyId(), filteredAttendanceList);
    }

    private void grantVoucherByAttendance(String companyId, List<Attendance> attendanceList) {
        if (ObjectUtils.isEmpty(attendanceList)) {
            return;
        }
        List<Attendance> filteredAttendanceList = attendanceList.stream().filter(attendance -> !ObjectUtils.isEmpty(attendance.getEmployeeId()) && (attendance.getUserCheckOutTime() != null || attendance.getUserWorkTimeMinutes() > 0)).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(filteredAttendanceList)) {
            return;
        }
        List<AttendanceGrantVoucherRule> ruleList = rulesDao.listRules(companyId, Lists.newArrayList());
        if (ObjectUtils.isEmpty(ruleList)) {
            return;
        }
        //尝试发券
        doGrantVoucher(filteredAttendanceList, ruleList);
        //更新数据
        updateData(filteredAttendanceList);
    }

    private void updateData(List<Attendance> attendanceList) {
        attendanceList.forEach(attendance -> {
            Attendance updateAttendance = new Attendance();
            updateAttendance.setId(attendance.getId());
            updateAttendance.setStatus(1);
            attendanceDao.updateById(updateAttendance);
        });
    }

    private void doGrantVoucher(List<Attendance> attendanceList, List<AttendanceGrantVoucherRule> ruleList) {
        ruleList.forEach(rule -> grantVoucherService.grantVoucherByAttendance(attendanceList, rule));
    }

    @Async
    @Override
    public void closeApply(String companyId) {
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("wiwj_direct_grant_voucher_expr"));
        //考勤及申请有效时间
        int expiredDays = openMsgSetups.get(0).getIntVal1() == null ? 90 : openMsgSetups.get(0).getIntVal1();
        Date nowDate = DateUtils.now(true);
        Date lastDate = DateUtils.addDay(nowDate, -expiredDays);
        List<OpenWawjFuliApply> applyList = wiwjFuliApplyDao.listCloseableApply(companyId, -1, lastDate);
        if (ObjectUtils.isEmpty(applyList)) {
            return;
        }
        applyList.forEach(apply -> {
            OpenWawjFuliApply updateApply = new OpenWawjFuliApply();
            updateApply.setId(apply.getId());
            updateApply.setStatus(1);
            wiwjFuliApplyDao.updateById(updateApply);
        });
    }

    @Override
    public void kaoqinSync(String companyId, List<WawjKqDTO> kqList) {
        kqList.forEach(kq -> {
            Attendance attendance = new Attendance();
            attendance.setCompanyId(companyId);
            attendance.setEmployeeId(kq.getEmployeeId());
            attendance.setEmployeeName(kq.getEmployeeName());
            attendance.setWorkDate(DateUtils.toDate(kq.getWorkDate()));
            attendance.setUserCheckInTime(DateUtils.toDate(kq.getUserCheckInTime()));
            attendance.setUserCheckOutTime(DateUtils.toDate(kq.getUserCheckOutTime()));
            attendance.setStatus(0);
            attendanceDao.saveSelective(attendance);
        });
    }

    @Override
    public void closeAttendance(String companyId) {
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("wiwj_direct_grant_voucher_expr"));
        //考勤及申请有效时间
        int expiredDays = ObjectUtils.isEmpty(openMsgSetups) || openMsgSetups.get(0).getIntVal1() == null ? 90 : openMsgSetups.get(0).getIntVal1();
        Date nowDate = DateUtils.now(true);
        Date lastDate = DateUtils.addDay(nowDate, -expiredDays);
        List<Attendance> attendanceList = attendanceDao.listCloseableAttendance(companyId, 0, lastDate);
        if (ObjectUtils.isEmpty(attendanceList)) {
            return;
        }
        attendanceList.forEach(kq -> {
            Attendance attendance = new Attendance();
            attendance.setId(kq.getId());
            //状态 0:初始值;1:已处理;2:已关闭
            attendance.setStatus(2);
            attendanceDao.updateById(attendance);
        });
    }
}
