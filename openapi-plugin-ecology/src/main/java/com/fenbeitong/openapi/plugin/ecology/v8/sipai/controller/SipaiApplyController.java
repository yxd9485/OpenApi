package com.fenbeitong.openapi.plugin.ecology.v8.sipai.controller;

import com.fenbeitong.openapi.plugin.ecology.v8.sipai.service.ISipaiApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: SipaiApplyController</p>
 * <p>Description: 思派行程(用车)审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 3:36 PM
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/ecology/sipai/apply")
public class SipaiApplyController {

    @Autowired
    private ISipaiApplyService applyService;

    /**
     * 思派生成行程审批
     *
     * @param jobConfig 公司id
     * @return success
     */
    @RequestMapping("/create/{companyId}")
    public String createApplypply(@PathVariable("companyId") String companyId) {
        applyService.createApply(companyId);
        return "SUCCESS";
    }

}
