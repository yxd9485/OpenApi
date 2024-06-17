package com.fenbeitong.openapi.plugin.task.yunzhijia;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.dao.MsgRecipientDefinitionDao;
import com.fenbeitong.openapi.plugin.support.common.notice.sender.entity.MsgRecipientDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.impl.CommonPluginCorpAppDefinitionService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaApply;
import com.fenbeitong.openapi.plugin.yunzhijia.notice.sender.YunzhijiaNoticeSender;
import com.fenbeitong.openapi.plugin.yunzhijia.process.IYunzhijiaProcessApply;
import com.fenbeitong.openapi.plugin.yunzhijia.process.YunzhijiaProcessApplyFactory;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply.YunzhijiaApplyServiceImpl;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.uc.YunzhijiaFbEmployeeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
@Slf4j
public class YunzhijiaApplyCreateProcessor extends AbstractTaskProcessor {
    @Autowired
    YunzhijiaApplyServiceImpl yunzhijiaApplyService;
    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;
    @Autowired
    YunzhijiaProcessApplyFactory yunzhijiaProcessApplyFactory;
    @Autowired
    CommonPluginCorpAppDefinitionService commonPluginCorpAppDefinitionService;
    @Autowired
    YunzhijiaFbEmployeeService yunzhijiaFbEmployeeService;
    @Autowired
    YunzhijiaNoticeSender yunzhijiaNoticeSender;
    @Autowired
    MsgRecipientDefinitionDao msgRecipientDefinitionDao;


    @Override
    public Integer getTaskType() {
        return TaskType.YUNZHIJIA_APPROVE_CREATE.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //1.根据公司ID获取调用云之家审批单详情access_token
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        //审批里data_content字段存储的是模板ID，用于区分是差旅审批还是用车审批
        String formCodeId = task.getDataContent();
        YunzhijiaApply yunzhijiaApplyByCorpId = yunzhijiaApplyService.getYunzhijiaApplyByCorpId(corpId);
        String agentId = yunzhijiaApplyByCorpId.getAgentId();
        String agentSecret = yunzhijiaApplyByCorpId.getAgentSecret();
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
            .eid(corpId)
            .appId(agentId)
            .secret(agentSecret)
            .timestamp(System.currentTimeMillis())
            .scope(YunzhijiaResourceLevelConstant.TEAM)
            .build();
        //2.根据审批模板和审批单实例ID查询审批单详情，审批单模板可根据data_content字段获取
        YunzhijiaApplyEventDTO yunzhijiaApplyDetail = yunzhijiaApplyService.getYunzhijiaApplyDetail(build, formCodeId, dataId);
        if (ObjectUtils.isEmpty(yunzhijiaApplyDetail)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_APPLY_DETAIL_IS_NULL)));
        }
        //3.解析审批单详情，映射为实体对象
        Integer errorCode = yunzhijiaApplyDetail.getErrorCode();
        boolean success = yunzhijiaApplyDetail.isSuccess();
        if (!success) {//code为正常情况时进行解析处理
            throw new OpenApiPluginException(Integer.valueOf(errorCode), "调用云之家获取审批单详情失败");
        }
        //4.根据实体对象转换成分贝通审批实体，需要工厂处理差旅和用车两种情况，根据模板ID查询审批单类型
        YunzhijiaApplyEventDTO.YunzhijiaApplyData applyData = yunzhijiaApplyDetail.getData();
        //查询申请单创建人是否为分贝通用户
        String yunzhijiaUserOId = applyData.getBasicInfo().getYunzhijiaEmployeeDTO().getOid();
        String yunzhijiaUserName = applyData.getBasicInfo().getYunzhijiaEmployeeDTO().getName();
        PluginCorpDefinition pluginCorpByCorpId = commonPluginCorpAppDefinitionService.getPluginCorpByCorpId(corpId);
        String token = yunzhijiaFbEmployeeService.getCreateTripApproveToken(pluginCorpByCorpId.getAppId(), yunzhijiaUserOId);
        if (StringUtils.isBlank(token)) {
            String msg = "通知：" + yunzhijiaUserName + "未开通分贝通账号导致分贝通差旅审批创建失败，请您尽快添加该员工至分贝通";
            //通知消息接收人
            List<MsgRecipientDefinition> msgRecipientList = msgRecipientDefinitionDao.getMsgRecipientList(corpId, null);
            List<String> msgRecipientIdList = Lists.newArrayList();
            msgRecipientIdList.add(yunzhijiaUserOId);
            msgRecipientList.stream().forEach(msgRecipientDefinition -> {
                msgRecipientIdList.add(msgRecipientDefinition.getThirdUserId());
            });
            yunzhijiaNoticeSender.sender(corpId, msgRecipientIdList, msg);
        }

        ThirdApplyDefinition thirdApplyDefinitionById = yunzhijiaApplyService.getThirdApplyDefinitionById(formCodeId);
        IYunzhijiaProcessApply processApply = yunzhijiaProcessApplyFactory.getProcessApply(thirdApplyDefinitionById.getProcessType());
        //5.解析人员是否已经存在分贝通，差旅信息是否有误，进行相应的消息通知
        //6.解析完成单独调用不同的差旅和用车审批，同步到分贝通
        TaskResult taskResult = processApply.processApply(FinhubTaskUtils.convert2Task(task), thirdApplyDefinitionById, pluginCorpByCorpId, applyData);
        return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
    }
}
