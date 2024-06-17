package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkGrantVoucherServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: IAttendanceGrantVoucherService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 3:36 PM
 */
@RestController
@RequestMapping("/dingtalk/sipai/attendance")
@Api(value = "思派考勤", tags = "思派考勤", description = "思派考勤发券")
public class SipaiAttendanceController {

    @Autowired
    private DingtalkGrantVoucherServiceImpl sipaiAttendanceService;

    /**
     * 思派发券
     *
     * @param companyId 公司id
     * @return success
     */
    @RequestMapping("/grantVoucher")
    @ApiOperation(value = "发券", notes = "发券", httpMethod = "POST")
    public String grantVoucher(@RequestParam("companyId") String companyId) {
        sipaiAttendanceService.grantOverTimeVoucher(companyId);
        return "SUCCESS";
    }

}
