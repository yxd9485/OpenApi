package com.fenbeitong.openapi.plugin.beisen.standard.controller;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.util.BeisenResponseUtils;
import com.fenbeitong.openapi.plugin.beisen.standard.service.BeisenAttendanceService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @param
 * @param
 * @param
 * @Description 根据北森审批单行程发券
 * @Author duhui
 * @Date 2021/9/27
 **/
@Controller
@Slf4j
@RequestMapping("/beisen/apply/attendance")
public class BeiSenAttendanceController {


    @Autowired
    BeisenAttendanceService beisenAttendanceService;

    /**
     * @Description 拉取审批并生成考勤表
     * @Author duhui
     * @Date 2021/9/27
     **/
    @RequestMapping("/create_attendance")
    @ResponseBody
    @ApiOperation(value = "生成考勤", notes = "生成考勤", httpMethod = "POST")
    public Object createAttendance(@RequestParam("jobConfig") String jobConfig) {
        BeisenParamConfig beisenParamConfig = JsonUtils.toObj(jobConfig, BeisenParamConfig.class);
        beisenAttendanceService.createAttendanceByAooly(beisenParamConfig);
        return BeisenResponseUtils.success("success");
    }

    /**
     * @Description 发券
     * @Author duhui
     * @Date 2021/9/27
     **/
    @RequestMapping("/grant_voucher/{companyId}")
    @ResponseBody
    @ApiOperation(value = "发券", notes = "发券", httpMethod = "POST")
    public Object grantVoucher(@PathVariable("companyId") String companyId) {
        beisenAttendanceService.grantVoucher(companyId, null);
        return "SUCCESS";
    }

}
