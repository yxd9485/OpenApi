package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdatamedium;

import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkProcessResult;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeEnum;
import org.apache.dubbo.config.annotation.DubboReference;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingTalkNoticeServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkApplyServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataMediumType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IApiIsvProcessInstanceService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvProcessApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.DingtalkIsvProcessApplyFactory;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl.ApiIsvUserServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.dao.CompanyInitUserDao;
import com.fenbeitong.openapi.plugin.support.init.entity.CompanyInitUser;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.google.common.collect.Lists;
import com.luastar.swift.base.json.JsonUtils;
import com.luastar.swift.base.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Description 审批单
 * @Author duhui
 * @Date 2021-04-01
 **/
@ServiceAspect
@Service
@Slf4j
public class DingtalkProcessBizrHandler implements IOpenSyncBizDataMediumTaskHandler {

    @Autowired
    private DingtalkApplyServiceImpl dingtalkApplyService;

    @Autowired
    private DingtalkIsvProcessApplyFactory applyFactory;

    @Autowired
    private IApiIsvProcessInstanceService apiIsvProcessInstanceService;

    @Autowired
    private DingTalkNoticeServiceImpl dingTalkNoticeService;

    @Autowired
    private CompanyInitUserDao companyInitUserDao;

    @Autowired
    private ApiIsvUserServiceImpl apiIsvUserService;

    @DubboReference(check = false)
    private IThirdEmployeeService employeeService;


    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;


    @Override
    public OpenSyncBizDataMediumType getTaskType() {
        return OpenSyncBizDataMediumType.DINGTALK_ISV_PROCESS_BIZ;
    }

    @Override
    public TaskResult execute(OpenSyncBizDataMedium task) {
        String processInstanceId = task.getBizId();
        String bizData = task.getBizData();
        String corpId = task.getCorpId();
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCorpId(corpId);
        Map<String, Object> dataMap = JsonUtils.toObj(task.getBizData(), Map.class);
        OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo = JsonUtils.toObj( bizData , OapiProcessinstanceGetResponse.ProcessInstanceTopVo.class);
        String processCode = (String) dataMap.get("processCode");
        // 只处理分贝通的审批单
        log.info("根据审批单code查找分贝通审批单 {}", processCode);
        DingtalkApply apply = dingtalkApplyService.getAppyByProcessCode(processCode);
        if (apply == null) {
            log.info("非分贝通审批单, 跳过, processCode: {}", processCode);
            return TaskResult.SUCCESS;
        }
        Integer processType = apply.getProcessType();
        String processDirType = ProcessTypeEnum.valueOf(processType);
        if (processInstanceTopVo.getResult().equals(DingtalkProcessResult.REFUSE.getValue()) && "0".equals(processDirType)){
            log.info("三方审批单, 跳过, processCode: {}", processCode);
            return TaskResult.ABORT;
        }
      //  OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo = apiIsvProcessInstanceService.getProcessInstance(processInstanceId, corpId);
        log.info("获取钉钉审批单详情完成 {}", processInstanceTopVo.getBusinessId());
        String thirdUserId = processInstanceTopVo.getOriginatorUserid();
        // 判断钉钉账号是否与分贝通进行了绑定
        log.info("获取的审批单用户ID {}", thirdUserId);
        if (!hasBindFbtAccount(thirdUserId, dingtalkIsvCompany.getCompanyId())) {
            String unBindMsg = StrUtils.formatString("您提交的分贝通差旅审批单没有创建成功，原因：\n您的钉钉账号[{0}]尚未与分贝通进行绑定，请联系分贝通工作人员绑定后再次进行提交。", thirdUserId);
            dingTalkNoticeService.sendMsg(corpId, thirdUserId, unBindMsg);
            return TaskResult.ABORT;
        }
        IDingtalkIsvProcessApplyService processApply = applyFactory.getProcessApply(processType);
        return processApply.processApply(task, dingtalkIsvCompany, apply, processInstanceTopVo);

    }

    protected TaskResult onlyDealInitUser(String corpId, String dingtalkUserId) {
        OapiUserGetResponse userWithOriginal = apiIsvUserService.getUserWithOriginal(corpId, dingtalkUserId);
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


}
