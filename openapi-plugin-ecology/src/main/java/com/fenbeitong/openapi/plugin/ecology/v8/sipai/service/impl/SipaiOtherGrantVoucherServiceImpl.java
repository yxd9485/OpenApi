package com.fenbeitong.openapi.plugin.ecology.v8.sipai.service.impl;

import com.fenbeitong.openapi.plugin.ecology.v8.sipai.constant.SipaiWorkFlowName;
import com.fenbeitong.openapi.plugin.ecology.v8.sipai.service.ISipaiOtherGrantVoucherService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflow;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceDao;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceGrantVoucherRulesDao;
import com.fenbeitong.openapi.plugin.support.voucher.entity.Attendance;
import com.fenbeitong.openapi.plugin.support.voucher.entity.AttendanceGrantVoucherRule;
import com.fenbeitong.openapi.plugin.support.voucher.service.IGrantVoucherService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: SipaiOtherGrantVoucherServiceImpl</p>
 * <p>Description: 思派其他发券服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/4 9:53 PM
 */
@ServiceAspect
@Service
public class SipaiOtherGrantVoucherServiceImpl implements ISipaiOtherGrantVoucherService {

    @Value("${host.openplus}")
    private String openPlusHost;

    @Autowired
    private IGrantVoucherService grantVoucherService;

    @Autowired
    private AttendanceGrantVoucherRulesDao rulesDao;

    @Autowired
    private AttendanceDao attendanceDao;

    @Autowired
    private OpenEcologyWorkflowDao workflowDao;

    @Autowired
    private RestHttpUtils httpUtils;

    @Override
    public void grantWeekendOverTimeVoucher(String companyId, Long ruleId) {
        //未发券的已审批通过的加班申请记录
        List<OpenEcologyWorkflow> workflowList = getUnhandlerOverTimeWorkflow(companyId);
        if (!ObjectUtils.isEmpty(workflowList)) {
            AttendanceGrantVoucherRule rule = rulesDao.getById(ruleId);
            workflowList.forEach(workflow -> {
                //加班日
                Date workdate = DateUtils.toDate(workflow.getStartDate(), DateUtils.FORMAT_DATE_PATTERN);
                Attendance attendance = attendanceDao.getUserWorkDateAttendance(companyId, workflow.getEmployeeId(), workdate);
                //打卡记录为空 尝试拉取一下
                if (attendance != null && attendance.getUserCheckInTime() == null && attendance.getUserCheckOutTime() == null) {
                    //拉取用户打卡记录
                    pullUserDintalkCheckTime(attendance);
                    //再加载一次
                    attendance = attendanceDao.getUserWorkDateAttendance(companyId, workflow.getEmployeeId(), workdate);
                }
                //考勤记录存在 且打卡签到或者签退
                if (attendance != null && (attendance.getUserCheckInTime() != null || attendance.getUserCheckOutTime() != null)) {
                    grantVoucherService.grantVoucherByAttendance(Lists.newArrayList(attendance), rule);
                    updateOverTimeApply(workflow.getId(), attendance);
                }
            });
        }
    }

    private void pullUserDintalkCheckTime(Attendance attendance) {
        String url = String.format("%s/openapi/dingtalk/attendance/pullDingTalkAttendance/v2?companyId=%s&onlyNormalLocation=1&workDate=%s&dingtalkUserId=%s", openPlusHost, attendance.getCompanyId(), DateUtils.toSimpleStr(attendance.getWorkDate(), true), attendance.getDingtalkUserId());
        String result = httpUtils.get(url, Maps.newHashMap());
    }

    private void updateOverTimeApply(Long id, Attendance attendance) {
        OpenEcologyWorkflow workflow = new OpenEcologyWorkflow();
        workflow.setId(id);
        workflow.setState(1);
        Map<String, Object> extInfo = Maps.newLinkedHashMap();
        extInfo.put("attendance_id", attendance.getId());
        extInfo.put("company_name", attendance.getCompanyName());
        extInfo.put("employee_name", attendance.getEmployeeName());
        extInfo.put("work_date", attendance.getWorkDate());
        extInfo.put("user_check_out_time", attendance.getUserCheckOutTime());
        workflow.setExtInfo(JsonUtils.toJson(extInfo));
        workflowDao.updateById(workflow);
    }

    private List<OpenEcologyWorkflow> getUnhandlerOverTimeWorkflow(String companyId) {
        OpenEcologyWorkflow workflow = new OpenEcologyWorkflow();
        workflow.setCompanyId(companyId);
        workflow.setAgreed(1);
        workflow.setWorkflowName(SipaiWorkFlowName.OVER_TIME_APPLY.getType());
        workflow.setState(0);
        return workflowDao.findList(workflow);
    }
}
