package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.finhub.common.constant.saas.ApplyOrderCategory;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkProcessResult;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>Title: DingtalkEiaMultiTripReverseApplyServiceImpl<p>
 * <p>Description: 用餐反向审批<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/7/7 14:46
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkEiaMultiTripReverseApplyServiceImpl extends AbstractDingtalkApplyService {
    @Autowired
    CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task, PluginCorpDefinition dingtalkCorp, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        boolean fbtOrderApply;
        if (processInstanceTopVo.getResult().equals(DingtalkProcessResult.REFUSE.getValue())){
            fbtOrderApply = commonApplyService.notifyThirdApplyRepulse(corpId, dataId, OpenType.DINGTALK_EIA.getType() , ApplyOrderCategory.MULTI_TRIP.getCategory() , "");
        }else{
            //通过
            fbtOrderApply = commonApplyService.notifyAgree(corpId, dataId, OpenType.DINGTALK_EIA.getType());
        }
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }



}
