package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 对公付款反向审批
 * @Auther xiaohai
 * @Date 2022/03/27
 */
@Component
@Slf4j
public class FeiShuIsvPaymentReverseService implements IFeishuIsvProcessApplyService {

    @Autowired
    CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task , String status)  {
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        boolean fbtOrderApply = false;
        if(FeiShuConstant.APPROVAL_INSTANCE_STATUS_APPROVED.equals(status)){
            //同意
            fbtOrderApply = commonApplyService.notifyAgree(corpId, dataId, OpenType.FEISHU_ISV.getType());
        }else if(FeiShuConstant.APPROVAL_INSTANCE_STATUS_REJECTED.equals(status)){
            fbtOrderApply = commonApplyService.notifyThirdApplyRepulse(corpId, dataId, OpenType.FEISHU_ISV.getType() , "payment" , "");
        }
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }
}
