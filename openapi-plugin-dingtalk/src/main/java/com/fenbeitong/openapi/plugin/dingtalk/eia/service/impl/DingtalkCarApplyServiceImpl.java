package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.constant.DingtalkProcessBizActionType;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkTripApplyProcessInfo;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;


/**
 * <p>Title: DingtalkCarApplyServiceImpl</p>
 * <p>Description: 用车订单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/24 8:11 PM
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkCarApplyServiceImpl extends AbstractDingtalkApplyService {


    @Autowired
    private DingtalkCarApplyFormParserServiceImpl formParser;
    @Autowired
    DingtalkCorpServiceImpl dingtalkCorpService;

    @Autowired
    OpenApiAuthServiceImpl openApiAuthService;

    @Autowired
    UserCenterService userCenterService;


    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition dingtalkCorp, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        String bizAction = processInstanceTopVo.getBizAction();
        if (DingtalkProcessBizActionType.NONE.getValue().equalsIgnoreCase(bizAction)) {
            // 用车审批单
            DingtalkTripApplyProcessInfo processInfo = formParser.parse(dingtalkCorp.getThirdCorpId(), dingtalkCorp.getAppId(), apply.getProcessType(), task.getDataId(), processInstanceTopVo);

            if (processInfo == null) {
                log.info("不符合分贝通审批用车申请单创建规则， 标记为废弃任务, taskId: {}, processInstanceId: {}", task.getId(), task.getDataId());
                return TaskResult.ABORT;
            }

            String token = openApiAuthService.getEmployeeFbToken(dingtalkCorp.getAppId(), processInstanceTopVo.getOriginatorUserid(), "1");
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            String data = gson.toJson(processInfo);
            CarApproveCreateReqDTO carApproveCreateReqDTO = JsonUtils.toObj(data, CarApproveCreateReqDTO.class);
            carApproveCreateReqDTO.getApply().setCompanyId(dingtalkCorp.getAppId());
            createCarApprove(token, carApproveCreateReqDTO);

        }
        //记录审批实例信息
        saveDingtalkProcessInstance(task, apply, processInstanceTopVo);
        return TaskResult.SUCCESS;
    }
}

