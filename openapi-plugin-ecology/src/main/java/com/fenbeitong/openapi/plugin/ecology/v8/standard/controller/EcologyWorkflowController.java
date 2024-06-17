package com.fenbeitong.openapi.plugin.ecology.v8.standard.controller;

import com.fenbeitong.openapi.plugin.ecology.v8.common.EcologySyncWorkflowReq;
import com.fenbeitong.openapi.plugin.ecology.v8.common.EcologyUpdateWorkflowReq;
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
 * @author lizhen
 * @date 2020/12/9
 */
@RestController
@RequestMapping("/ecology/standard/workflow")
public class EcologyWorkflowController {

    @Autowired
    private IEcologyWorkFlowService workflowService;

    /**
     * 泛微拉取工作流
     *
     * @param jobConfig 公司id
     * @return success
     */
    @RequestMapping("/syncApply")
    public String syncApply(@RequestParam("jobConfig") String jobConfig) {
        EcologySyncWorkflowReq req = JsonUtils.toObj(jobConfig, EcologySyncWorkflowReq.class);
        Date createDate = DateUtils.toDate(req.getCreateDate(), DateUtils.FORMAT_DATE_PATTERN);
        Map<String, Integer> applyNameMapping = req.getApplyNameMapping();
        workflowService.syncApply(req.getConfigId(), req.getCompanyId(), createDate, applyNameMapping);
        return "SUCCESS";
    }

    /**
     * 泛微更新工作流
     *
     * @param jobConfig 公司id
     * @return success
     */
    @RequestMapping("/updateApply")
    public String updateApply(@RequestParam("jobConfig") String jobConfig) {
        EcologyUpdateWorkflowReq req = JsonUtils.toObj(jobConfig, EcologyUpdateWorkflowReq.class);
        workflowService.updateApply(req.getConfigId(), req.getCompanyId());
        return "SUCCESS";
    }

    /**
     * 泛微关闭工作流
     *
     * @param companyId 公司id
     * @return success
     */
    @RequestMapping("/closeApply/{companyId}")
    public String closeApply(@PathVariable("companyId") String companyId) {
        workflowService.closeApply(companyId);
        return "SUCCESS";
    }

    /**
     * 创建审批
     * @param companyId
     * @return
     */
    @RequestMapping("/create/{companyId}")
    public String createApplypply(@PathVariable("companyId") String companyId) {
        workflowService.createApply(companyId);
        return "SUCCESS";
    }

}
