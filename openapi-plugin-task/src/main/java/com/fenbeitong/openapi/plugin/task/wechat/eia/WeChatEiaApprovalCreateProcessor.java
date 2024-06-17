package com.fenbeitong.openapi.plugin.task.wechat.eia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.FbUserCheck;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;
import com.fenbeitong.openapi.plugin.wechat.eia.process.IWeChatEiaProcessApply;
import com.fenbeitong.openapi.plugin.wechat.eia.process.ProcessApplyFactory;
import com.fenbeitong.openapi.plugin.wechat.eia.service.apply.WeChatEiaApprovolService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.openapi.WeChatEiaPluginCallOpenApiService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.PluginCallWeChatEiaService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WechatTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;

/**
 * Created by dave.hansins on 19/12/13.
 */
@Controller
@Slf4j
@Component
public class WeChatEiaApprovalCreateProcessor extends AbstractTaskProcessor {

    @Autowired
    private PluginCallWeChatEiaService pluginCallWeChatEiaService;

    @Autowired
    private WeChatEiaApprovolService weChatEiaApprovolService;

    @Autowired
    private ProcessApplyFactory processApplyFactory;

    @Autowired
    private WeChatEiaPluginCallOpenApiService weChatEiaPluginCallOpenApiService;

    @Autowired
    private WechatTokenService wechatTokenService;

    @Override
    public Integer getTaskType() {
        return TaskType.WECHAT_EIA_APPROVAL_CREATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //企业
        String corpId = task.getCompanyId();
        //业务数据ID
        String dataId = task.getDataId();
        //0.根据企业ID查询企业secret
//        WeChatApply weChatApply = weChatEiaApprovolService.getWeChatApplyInfoByCorpId(corpId);
//        String agentSecret = weChatApply.getAgentSecret();
        PluginCorpDefinition pluginCorpDefinitionBy = weChatEiaApprovolService.getPluginCorpDefinitionByThirdCorpId(corpId);
        if (ObjectUtils.isEmpty(pluginCorpDefinitionBy)) {
            throw new OpenApiPluginException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ID_NOT_EXIST));
        }
//        //1.获取企业access_token
//        WeChatToken weChatCorpAccessToken = pluginCallWeChatEiaService.getWeChatCorpAccessToken(corpId, agentSecret);
//        if (ObjectUtils.isEmpty(weChatCorpAccessToken)) {//获取企业access_token异常
//            throw new OpenApiPluginException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_ACCESS_TOKEN_IS_NULL));
//        }
        String weChatApprovalToken = wechatTokenService.getWeChatApprovalTokenByCorpId(corpId);
        //2.根据审批单ID查询审批单详情
        WeChatApprovalDetail weChatApprovalDetailBySpNo = pluginCallWeChatEiaService.getWeChatApprovalDetailBySpNo(weChatApprovalToken, dataId, corpId);
        if (ObjectUtils.isEmpty(weChatApprovalDetailBySpNo)) {
            throw new OpenApiPluginException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_APPROVOL_IS_NULL));
        }
        String userId = weChatApprovalDetailBySpNo.getWeChatApprovalInfo().getApplyer().getUserId();

        FbUserCheck isFbtUser = weChatEiaPluginCallOpenApiService.CheckUserInfo(pluginCorpDefinitionBy.getAppId(), userId);
        if (isFbtUser.isFbUser()) {
            //根据审批单号查询模板信息数据
            String templateId = weChatApprovalDetailBySpNo.getWeChatApprovalInfo().getTemplateId();
            ThirdApplyDefinition thirdApplyConfigByProcessCode = weChatEiaApprovolService.getThirdApplyConfigByProcessCode(templateId);
            log.info("根据审批单code查找分贝通审批单 {}", templateId);
            if (thirdApplyConfigByProcessCode == null) {
                //TODO
                log.info("非分贝通审批单, 跳过, processCode: {}", templateId);
                return TaskProcessResult.success("非分贝通审批单, 跳过 success");
            }
            //审批类型，差旅或用车
            Integer processType = thirdApplyConfigByProcessCode.getProcessType();
            //3.根据审批单详情进行解析审批单数据
            //4.解析审批单完成后调用openapi进行新增操作
            //5.根据结果更新任务状态
            WeChatApprovalDetail.WeChatApprovalInfo weChatApprovalInfo = weChatApprovalDetailBySpNo.getWeChatApprovalInfo();
            IWeChatEiaProcessApply processApply = processApplyFactory.getProcessApply(processType);
            TaskResult taskResult = processApply.processApply(FinhubTaskUtils.convert2Task(task), pluginCorpDefinitionBy, thirdApplyConfigByProcessCode, weChatApprovalInfo);
            return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
        }
        log.info("申请人在分贝通未注册 {}", userId);
        return TaskProcessResult.success("申请人在分贝通未注册 abort success");

    }


}
