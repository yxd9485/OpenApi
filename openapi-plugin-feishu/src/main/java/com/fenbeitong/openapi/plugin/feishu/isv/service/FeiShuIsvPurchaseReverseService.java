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
 * 飞书用餐审批处理
 * @Auther zhang.peng
 * @Date 2021/7/9
 */
@Component
@Slf4j
public class FeiShuIsvPurchaseReverseService implements IFeishuIsvProcessApplyService {

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
            fbtOrderApply = commonApplyService.notifyThirdApplyRepulse(corpId, dataId, OpenType.FEISHU_ISV.getType() , "mall" , "");
        }
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }
}
