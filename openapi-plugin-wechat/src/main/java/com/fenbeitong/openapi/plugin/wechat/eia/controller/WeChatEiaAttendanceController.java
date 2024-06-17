package com.fenbeitong.openapi.plugin.wechat.eia.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.support.voucher.dto.CreateAttendanceReq;
import com.fenbeitong.openapi.plugin.support.voucher.service.IGrantVoucherService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WechatResponseUtils;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.attendance.WeChatPullAttendanceReqDTO;
import com.fenbeitong.openapi.plugin.wechat.eia.service.attendance.WeChatAttendanceService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @Description 微信考勤发券
 * @Author duhui
 * @Date 2021-02-19
 **/
@Slf4j
@RestController
@RequestMapping("/wechat/attendance")
@Api(value = "考勤", tags = "微信考勤", description = "考勤")
public class WeChatEiaAttendanceController {


    @Autowired
    WeChatAttendanceService weChatAttendanceService;

    @Autowired
    IGrantVoucherService IGrantVoucherService;


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
        weChatAttendanceService.createAttendance(req, DateUtils.now(true));
        return WechatResponseUtils.success(Maps.newHashMap());
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
        weChatAttendanceService.createAttendance(req, workDate);
        return WechatResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 生成昨天的考勤表
     *
     * @param
     */
    @RequestMapping("/createYesterdayAttendance")
    @ApiOperation(value = "生成昨天的考勤表", notes = "生成昨天的考勤表", httpMethod = "POST")
    public Object createYesterdayAttendance(@RequestParam("jobConfig") String jobConfig) throws Exception {
        CreateAttendanceReq req = JsonUtils.toObj(jobConfig, CreateAttendanceReq.class);
        weChatAttendanceService.createAttendance(req, DateUtils.yesterday(true));
        return WechatResponseUtils.success(Maps.newHashMap());
    }


    /**
     * 拉取微信考勤记录
     *
     * @param jobConfig 定时任务配置
     */
    @RequestMapping("/pullWeChatkAttendance")
    @ApiOperation(value = "拉取微信考勤记录", notes = "拉取微信考勤记录", httpMethod = "POST")
    public Object pullWeChatkAttendance(@RequestParam("jobConfig") String jobConfig) {
        WeChatPullAttendanceReqDTO req = JsonUtils.toObj(jobConfig, WeChatPullAttendanceReqDTO.class);
        List<Date> dateList = null;
        if (!ObjectUtils.isEmpty(req.getWorkDate())) {
            dateList = Lists.newArrayList(DateUtils.toDate(req.getWorkDate()));
        }
        if (ObjectUtils.isEmpty(dateList)) {
            dateList = Lists.newArrayList(DateUtils.yesterday(true), DateUtils.now(true));
        }
        List<String> weChatUserIdList = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(req.getWeChatUserId())) {
            weChatUserIdList.add(req.getWeChatUserId());
        }
        weChatAttendanceService.pullWeChatAttendance(req.getCompanyId(), req.getOpenCheckinDataType(), dateList, weChatUserIdList);
        return WechatResponseUtils.success(Maps.newHashMap());
    }


    /**
     * 发放分贝券
     *
     * @param companyId 公司id
     * @return success
     */
    @RequestMapping("/grantVoucher/{companyId}")
    @ApiOperation(value = "发券", notes = "发券", httpMethod = "POST")
    public String grantVoucher(@PathVariable("companyId") String companyId) {
        IGrantVoucherService.grantOverTimeVoucher(companyId);
        return "SUCCESS";
    }

    /**
     * 分发指定日期的考勤发券
     */
    @RequestMapping("/grantVoucherDate/{companyId}")
    @ApiOperation(value = "发券", notes = "发券", httpMethod = "POST")
    public String grantVoucherDate(@PathVariable("companyId") String companyId,String date) {
        IGrantVoucherService.grantOverTimeVoucher(companyId, DateUtils.toDate(date,"yyyy-MM-dd HH:mm:ss"), null);
        return "SUCCESS";
    }
}
