package com.fenbeitong.openapi.plugin.ecology.v8.sipai.controller;

import com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto.SipaiSyncWorkflowReq;
import com.fenbeitong.openapi.plugin.ecology.v8.sipai.dto.SipaiUpdateWorkflowReq;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.IEcologyWorkFlowService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * <p>Title: IAttendanceGrantVoucherService</p>
 * <p>Description: 思派工作流</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 3:36 PM
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/ecology/sipai/workflow")
public class SipaiWorkflowController {

    @Autowired
    private IEcologyWorkFlowService workflowService;

    /**
     * 思派拉取工作流
     *
     * @param jobConfig 公司id
     * @return success
     */
    @RequestMapping("/syncApply")
    public String syncApply(@RequestParam("jobConfig") String jobConfig) {
        SipaiSyncWorkflowReq req = JsonUtils.toObj(jobConfig, SipaiSyncWorkflowReq.class);
        Date createDate = DateUtils.toDate(req.getCreateDate(), DateUtils.FORMAT_DATE_PATTERN);
        Map<String, Integer> applyNameMapping = req.getApplyNameMapping();
        workflowService.syncApply(req.getConfigId(), req.getCompanyId(), createDate, applyNameMapping);
        return "SUCCESS";
    }

    /**
     * 思派更新工作流
     *
     * @param jobConfig 公司id
     * @return success
     */
    @RequestMapping("/updateApply")
    public String updateApply(@RequestParam("jobConfig") String jobConfig) {
        SipaiUpdateWorkflowReq req = JsonUtils.toObj(jobConfig, SipaiUpdateWorkflowReq.class);
        workflowService.updateApply(req.getConfigId(), req.getCompanyId());
        return "SUCCESS";
    }

    /**
     * 思派关闭工作流
     *
     * @param companyId 公司id
     * @return success
     */
    @RequestMapping("/closeApply/{companyId}")
    public String closeApply(@PathVariable("companyId") String companyId) {
        workflowService.closeApply(companyId);
        return "SUCCESS";
    }

}
