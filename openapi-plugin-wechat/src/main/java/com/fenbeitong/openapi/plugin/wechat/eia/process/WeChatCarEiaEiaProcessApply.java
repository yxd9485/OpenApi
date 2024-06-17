package com.fenbeitong.openapi.plugin.wechat.eia.process;

import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.ApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;
import com.fenbeitong.openapi.plugin.wechat.eia.service.openapi.WeChatEiaPluginCallOpenApiService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by dave.hansins on 19/12/16.
 */
@Component
@Slf4j
public class WeChatCarEiaEiaProcessApply extends AbstractWeChatEiaEiaProcessApply implements IWeChatEiaProcessApply {

    @Resource(name = "weChatEiaCarListBeanParser")
    IWeChatEiaProcessFormParser iWeChatEiaProcessFormParser;

    @Autowired
    WeChatEiaPluginCallOpenApiService weChatEiaPluginCallOpenApiService;


    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition corp, ThirdApplyDefinition apply, WeChatApprovalDetail.WeChatApprovalInfo weChatApprovalInfo) {
        ApprovalInfo approvalInfo = iWeChatEiaProcessFormParser.parse(corp.getAppId(), apply.getProcessType(), task.getDataId(), weChatApprovalInfo);
        if (approvalInfo == null) {
            log.info("不符合分贝通审批单创建规则， 标记为废弃任务, taskId: {}, processInstanceId: {}", task.getId(), task.getDataId());
            return TaskResult.ABORT;
        }
        // 调用OPENAPI创建审批单
        try {
            String token = weChatEiaPluginCallOpenApiService.getEmployeeFbToken(corp.getAppId(), weChatApprovalInfo.getApplyer().getUserId(), "1");
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            String data = gson.toJson(approvalInfo);
            CarApproveCreateReqDTO carApproveCreateReqDTO = JsonUtils.toObj(data, CarApproveCreateReqDTO.class);
            carApproveCreateReqDTO.getApply().setCompanyId(corp.getAppId());
            createCarApprove(token, carApproveCreateReqDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //保存审批实例信息
        saveDingtalkProcessInstance(task, apply, weChatApprovalInfo);
        return TaskResult.SUCCESS;
    }
}
