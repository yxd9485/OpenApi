package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseUtils;
import com.fenbeitong.openapi.plugin.dingtalk.dto.PullAttendanceReq;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkAttendanceService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkGrantVoucherServiceImpl;
import com.fenbeitong.openapi.plugin.support.voucher.dto.CreateAttendanceReq;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: DingtalkAttendanceController</p>
 * <p>Description: 钉钉考勤</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/17 10:47 AM
 */
@Slf4j
@RestController
@RequestMapping("/dingtalk/attendance")
@Api(value = "考勤", tags = "钉钉考勤", description = "考勤")
public class DingtalkAttendanceController {

    @Autowired
    private IDingtalkAttendanceService attendanceService;

    @Autowired
    private DingtalkGrantVoucherServiceImpl sipaiAttendanceService;

    /**
     * 生成当天的考勤表
     *
     * @param jobConfig 生成考勤记录参数
     */
    @RequestMapping("/createTodayAttendance")
    @ApiOperation(value = "生成当天的考勤表", notes = "生成当天的考勤表", httpMethod = "POST")
    public Object createTodayAttendance(@RequestParam("jobConfig") String jobConfig) throws Exception {
        CreateAttendanceReq req = JsonUtils.toObj(jobConfig, CreateAttendanceReq.class);
        ValidatorUtils.validateBySpring(req);
        attendanceService.createAttendance(req, DateUtils.now(true));
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 生成指定日期的的考勤表
     *
     * @param jobConfig 生成指定日期考勤记录参数
     */
    @RequestMapping("/generateWorkDateAttendance")
    @ApiOperation(value = "生成指定日期的考勤表", notes = "生成指定日期的考勤表", httpMethod = "POST")
    public Object generateWorkDateAttendance(@RequestParam("jobConfig") String jobConfig) throws Exception {
        CreateAttendanceReq req = JsonUtils.toObj(jobConfig, CreateAttendanceReq.class);
        ValidatorUtils.validateBySpring(req);
        Date workDate = ObjectUtils.isEmpty(req.getWorkDate()) ? DateUtils.now(true) : DateUtils.toDate(req.getWorkDate());
        attendanceService.createAttendance(req, workDate);
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 生成昨天的考勤表
     *
     * @param req 生成考勤记录参数
     */
    @RequestMapping("/createYesterdayAttendance")
    @ApiOperation(value = "生成昨天的考勤表", notes = "生成昨天的考勤表", httpMethod = "POST")
    public Object createYesterdayAttendance(@Valid @RequestBody CreateAttendanceReq req) throws Exception {
        attendanceService.createAttendance(req, DateUtils.yesterday(true));
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 拉取钉钉考勤记录
     *
     * @param jobConfig 定时任务配置
     */
    @RequestMapping("/pullDingTalkAttendance")
    @ApiOperation(value = "拉取钉钉考勤记录", notes = "拉取钉钉考勤记录", httpMethod = "POST")
    public Object pullDingTalkAttendance(@RequestParam("jobConfig") String jobConfig) {
        PullAttendanceReq req = JsonUtils.toObj(jobConfig, PullAttendanceReq.class);
        List<Date> dateList = null;
        if (!ObjectUtils.isEmpty(req.getWorkDate())) {
            dateList = Lists.newArrayList(DateUtils.toDate(req.getWorkDate()));
        }
        if (ObjectUtils.isEmpty(dateList)) {
            dateList = Lists.newArrayList(DateUtils.yesterday(true), DateUtils.now(true));
        }
        List<String> dingtalkUserIdList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(req.getDingtalkUserId())) {
            dingtalkUserIdList.add(req.getDingtalkUserId());
        }
        attendanceService.pullDingTalkAttendance(req.getCompanyId(), req.getOnlyNormalLocation(), dateList, dingtalkUserIdList,req.getIsCheckApprove());
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 拉取钉钉考勤记录
     *
     * @param req 钉钉考勤拉取参数
     */
    @RequestMapping("/pullDingTalkAttendance/v2")
    @ApiOperation(value = "拉取钉钉考勤记录", notes = "拉取钉钉考勤记录", httpMethod = "POST")
    public Object pullDingTalkAttendanceV2(PullAttendanceReq req) {
        List<Date> dateList = null;
        if (!ObjectUtils.isEmpty(req.getWorkDate())) {
            dateList = Lists.newArrayList(DateUtils.toDate(req.getWorkDate()));
        }
        if (ObjectUtils.isEmpty(dateList)) {
            dateList = Lists.newArrayList(DateUtils.yesterday(true), DateUtils.now(true));
        }
        List<String> dingtalkUserIdList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(req.getDingtalkUserId())) {
            dingtalkUserIdList.add(req.getDingtalkUserId());
        }
        attendanceService.pullDingTalkAttendance(req.getCompanyId(), req.getOnlyNormalLocation(), dateList, dingtalkUserIdList,req.getIsCheckApprove());
        return DingtalkResponseUtils.success(Maps.newHashMap());
    }


    /**
     * 考勤发券按天发
     */
    @RequestMapping("/grantVoucher/{companyId}")
    @ApiOperation(value = "发券", notes = "发券", httpMethod = "POST")
    public String grantVoucher(@PathVariable("companyId") String companyId) {
        sipaiAttendanceService.grantOverTimeVoucher(companyId, DateUtils.yesterday(true), null);
        return "SUCCESS";
    }


    /**
     * 分发指定日期的考勤发券
     */
    @RequestMapping("/grantVoucherDate/{companyId}")
    @ApiOperation(value = "发券", notes = "发券", httpMethod = "POST")
    public String grantVoucherDate(@PathVariable("companyId") String companyId,String date) {
        sipaiAttendanceService.grantOverTimeVoucher(companyId, DateUtils.toDate(date,"yyyy-MM-dd HH:mm:ss"), null);
        return "SUCCESS";
    }

}
