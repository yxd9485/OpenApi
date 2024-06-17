package com.fenbeitong.openapi.plugin.ecology.v8.sipai.controller;

import com.fenbeitong.openapi.plugin.ecology.v8.sipai.service.ISipaiOtherGrantVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: SipaiOtherGrantVoucherController</p>
 * <p>Description: 思派其他发券</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 3:36 PM
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/ecology/sipai/other/grantVoucher")
public class SipaiOtherGrantVoucherController {

    @Autowired
    private ISipaiOtherGrantVoucherService grantVoucherService;

    /**
     * 思派周末发券
     *
     * @param jobConfig 公司id
     * @return success
     */
    @RequestMapping("/grantWeekendOverTimeVoucher/{companyId}/{ruleId}")
    public String grantWeekendOverTimeVoucher(@PathVariable("companyId") String companyId, @PathVariable("ruleId") Long ruleId) {
        grantVoucherService.grantWeekendOverTimeVoucher(companyId, ruleId);
        return "SUCCESS";
    }

}
