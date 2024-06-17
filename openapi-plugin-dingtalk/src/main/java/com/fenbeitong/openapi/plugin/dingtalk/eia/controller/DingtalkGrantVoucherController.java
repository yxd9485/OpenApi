package com.fenbeitong.openapi.plugin.dingtalk.eia.controller;

import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.GrantOverTimeVoucherReq;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkGrantVoucherServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: DingtalkGrantVoucherController</p>
 * <p>Description: 钉钉考勤发券</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 3:36 PM
 */
@RestController
@RequestMapping("/dingtalk/grantVoucher")
@Api(value = "发券", tags = "发券", description = "发券")
public class DingtalkGrantVoucherController {

    @Autowired
    private DingtalkGrantVoucherServiceImpl grantVoucherService;

    /**
     * 加班发券
     *
     * @param companyId 公司id
     * @return success
     */
    @RequestMapping("/grantOverTimeVoucher")
    @ApiOperation(value = "加班发券", notes = "加班发券", httpMethod = "POST")
    public String grantOverTimeVoucher(@RequestParam("companyId") String companyId) {
        grantVoucherService.grantOverTimeVoucher(companyId);
        return "SUCCESS";
    }

    /**
     * 根据规则加班发券
     *
     * @param jobConfig 定时任务配置
     * @return success
     */
    @RequestMapping("/grantOverTimeVoucherByRule")
    public String grantOverTimeVoucherByRule(@RequestParam("jobConfig") String jobConfig) {
        GrantOverTimeVoucherReq req = JsonUtils.toObj(jobConfig, GrantOverTimeVoucherReq.class);
        grantVoucherService.grantOverTimeVoucherByRule(req.getCompanyId(), req.getRuleIdList());
        return "SUCCESS";
    }

}
