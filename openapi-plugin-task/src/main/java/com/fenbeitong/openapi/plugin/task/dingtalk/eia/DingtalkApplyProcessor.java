package com.fenbeitong.openapi.plugin.task.dingtalk.eia;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiProcessInstanceService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkProcessApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.handler.DingtalkProcessApplyFactory;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.ApiUserServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingTalkNoticeServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkApplyServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkCorpServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.dao.CompanyInitUserDao;
import com.fenbeitong.openapi.plugin.support.init.entity.CompanyInitUser;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.luastar.swift.base.json.JsonUtils;
import com.luastar.swift.base.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 钉钉审批流程结束处理类
 *
 * @author zhaokechun
 * @date 2018/11/27 14:34
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkApplyProcessor extends AbstractTaskProcessor {

    @Autowired
    private DingtalkCorpServiceImpl dingtalkCorpService;

    @Autowired
    private DingtalkApplyServiceImpl dingtalkApplyService;

    @Autowired
    private DingtalkProcessApplyFactory applyFactory;

    @Autowired
    private IApiProcessInstanceService apiProcessInstanceService;

    @Autowired
    private DingTalkNoticeServiceImpl dingTalkNoticeService;

    @DubboReference(check = false)
    private IThirdEmployeeService employeeService;

    @Autowired
    private CompanyInitUserDao companyInitUserDao;

    @Autowired
    private ApiUserServiceImpl apiUserService;

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        String processInstanceId = task.getDataId();
        String corpId = task.getCompanyId();
        PluginCorpDefinition dingtalkCorp = dingtalkCorpService.getByCorpId(corpId);
        Map<String, Object> dataMap = JsonUtils.toObj(task.getDataContent(), Map.class);
        String processCode = (String) dataMap.get("processCode");
        // 只处理分贝通的审批单
        log.info("根据审批单code查找分贝通审批单 {}", processCode);
        DingtalkApply apply = dingtalkApplyService.getAppyByProcessCode(processCode);
        if (apply == null || !dingtalkCorp.getAppId().equals(apply.getCompanyId())) {
            log.info("非分贝通审批单, 跳过, processCode: {}", processCode);
            return TaskProcessResult.success("success");
        }
        OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo = apiProcessInstanceService.getProcessInstance(processInstanceId, corpId);
        log.info("获取钉钉审批单详情完成 {}", processInstanceTopVo.getBusinessId());
        String thirdUserId = processInstanceTopVo.getOriginatorUserid();
        TaskResult taskResult = onlyDealInitUser(corpId, thirdUserId);
        if (taskResult != null) {
            return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
        }
        // 判断钉钉账号是否与分贝通进行了绑定
        log.info("获取的审批单用户ID {}", thirdUserId);
        if (!hasBindFbtAccount(thirdUserId, dingtalkCorp.getAppId())) {
            String unBindMsg = StrUtils.formatString("您提交的分贝通差旅审批单没有创建成功，原因：\n您的钉钉账号[{0}]尚未与分贝通进行绑定，请联系分贝通工作人员绑定后再次进行提交。", thirdUserId);
            dingTalkNoticeService.sendMsg(corpId, thirdUserId, unBindMsg);
            return TaskProcessResult.success("success");
        }
        IDingtalkProcessApplyService processApply = applyFactory.getProcessApply(apply.getProcessType());
        taskResult = processApply.processApply(FinhubTaskUtils.convert2Task(task), dingtalkCorp, apply, processInstanceTopVo);
        return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
    }

    protected TaskResult onlyDealInitUser(String corpId, String dingtalkUserId) {
        OapiUserGetResponse userWithOriginal = apiUserService.getUserWithOriginal(corpId, dingtalkUserId);
        DingtalkUser dingtalkUser = JsonUtils.toObj(userWithOriginal.getBody(), DingtalkUser.class);
        CompanyInitUser companyInitUser = companyInitUserDao.getBYCorpId(corpId);
        List<String> initUserPhonesList = companyInitUser == null ? Lists.newArrayList() : companyInitUser.getDingtalkUserPhoneList();
        String fbtMobile = Optional.ofNullable(dingtalkUser.getFbtMobile()).orElse("");
        return ObjectUtils.isEmpty(initUserPhonesList) || initUserPhonesList.contains(fbtMobile) ? null : TaskResult.ABORT;
    }

    /**
     * 校验是否绑定了分贝通ID
     *
     * @param thirdUserId thirdUserId
     * @param companyId   companyId
     * @return
     */
    private boolean hasBindFbtAccount(String thirdUserId, String companyId) {
        log.info("校验人员是否绑定传入人员参数 :{},公司 :{}", thirdUserId, companyId);
        ThirdEmployeeContract thirdEmployeeContract = new ThirdEmployeeContract();
        thirdEmployeeContract.setEmployeeId(thirdUserId);
        thirdEmployeeContract.setUserType(2);
        thirdEmployeeContract.setCompanyId(companyId);
        thirdEmployeeContract.setType(1);
        ThirdEmployeeRes employeeContract = employeeService.queryEmployeeInfo(thirdEmployeeContract);
        if (employeeContract == null) {
            log.info("公司{},钉钉人员id{},未绑定分贝通账号", companyId, thirdUserId);
        }
        return employeeContract != null;
    }

    @Override
    public Integer getTaskType() {
        return TaskType.DINGTALK_EIA_BPMS_INSTANCE_CHANGE.getCode();
    }

}
