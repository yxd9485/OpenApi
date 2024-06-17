package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkProcessResult;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>Title: DingtalkIsvReverseApplyServiceImpl</p>
 * <p>Description: 分贝通发起审批，三方审批，审批完成后同步审批结果到分贝通</p>
 *
 * @author xiaohai
 * @date 2022/01/28
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkIsvReverseApplyServiceImpl extends AbstractDingtalkIsvApplyService {

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(OpenSyncBizDataMedium task, DingtalkIsvCompany dingtalkIsvCompany, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        String corpId = task.getCorpId();
        String dataId = task.getBizId();
        boolean fbtOrderApply = false ;
        if (processInstanceTopVo.getResult().equals(DingtalkProcessResult.REFUSE.getValue())){
            fbtOrderApply = commonApplyService.notifyThirdApplyRepulse(corpId, dataId, OpenType.DINGTALK_ISV.getType() , "multi_trip" , "");
        }else{
            //通过
            fbtOrderApply = commonApplyService.notifyMultiApplyAgree(corpId, dataId, OpenType.DINGTALK_ISV.getType());
        }
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }

}

