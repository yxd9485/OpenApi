package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.constant.DingtalkProcessBizActionType;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dao.DingtalkProcessInstanceDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkProcessInstance;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.AbstractDingtalkApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkTripApplyFormParserServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.OpenApiProcessServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>Title: DingtalkTripApplyServiceImpl</p>
 * <p>Description: 钉钉行程审批服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/04/07 10:31 AM
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkIsvTripApplyServiceImpl extends AbstractDingtalkIsvApplyService {

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private OpenApiProcessServiceImpl openApiProcessService;

    @Autowired
    private DingtalkTripApplyFormParserServiceImpl tripApplyFormParserService;

    @Autowired
    private DingtalkProcessInstanceDao dingtalkProcessInstanceDao;


    @Override
    public TaskResult processApply(OpenSyncBizDataMedium task, DingtalkIsvCompany dingtalkIsvCompany, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        String bizAction = processInstanceTopVo.getBizAction();
        if (DingtalkProcessBizActionType.NONE.getValue().equalsIgnoreCase(bizAction) || DingtalkProcessBizActionType.MODIFY.getValue().equalsIgnoreCase(bizAction)) {
            // 如果是修改审批单，则额外执行撤销之前的审批单操作
            if (DingtalkProcessBizActionType.MODIFY.getValue().equalsIgnoreCase(bizAction)) {
                revokePreProcess(processInstanceTopVo.getBusinessId());
            }
            // 正常审批单
            DingtalkTripApplyProcessInfo processInfo = tripApplyFormParserService.parse(dingtalkIsvCompany.getCorpId(), dingtalkIsvCompany.getCompanyId(), apply.getProcessType(), task.getBizId(), processInstanceTopVo);
            if (processInfo == null) {
                log.info("不符合分贝通审批单创建规则， 标记为废弃任务, taskId: {}, processInstanceId: {}", task.getId(), task.getBizId());
                return TaskResult.ABORT;
            }
            openApiProcessService.createTripApprove(processInfo, dingtalkIsvCompany.getCorpId(), processInstanceTopVo.getOriginatorUserid(),processInstanceTopVo);
        } else if (DingtalkProcessBizActionType.REVOKE.getValue().equalsIgnoreCase(bizAction)) {
            revokePreProcess(processInstanceTopVo.getBusinessId());
        }
        //记录审批实例信息
        saveDingtalkProcessInstance(task, apply, processInstanceTopVo);
        return TaskResult.SUCCESS;
    }

    /**
     * 撤销先前提交的审批单
     *
     * @param businessId
     */
    private void revokePreProcess(String businessId) {
        List<DingtalkProcessInstance> instanceList = dingtalkProcessInstanceDao.listInstanceByBusinessId(businessId);
        if (ObjectUtils.isEmpty(instanceList)) {
            log.info("没有查询到需要撤销的分贝通审批单, businessId: {}", businessId);
            return;
        }
        instanceList.stream().filter(ins -> {
            String action = ins.getBizAction();
            return action.equalsIgnoreCase(DingtalkProcessBizActionType.NONE.getValue())
                    || action.equalsIgnoreCase(DingtalkProcessBizActionType.MODIFY.getValue());
        }).forEach(item -> {
            PluginCorpDefinition dingtalkCorp = dingtalkCorpService.getByCorpId(item.getCorpId());
            // 撤销审批单
            openApiProcessService.cancel(item.getInstanceId(), 2, dingtalkCorp.getAppId(), item.getUserId());
        });
    }
}
