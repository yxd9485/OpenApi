package com.fenbeitong.openapi.plugin.feishu.isv.service;

import com.fenbeitong.finhub.common.constant.saas.ApplyOrderCategory;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Title: FeiShuIsvCarReverseService<p>
 * <p>Description: 飞书市场版反向审批后回调分贝通<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date  2022/6/9 16:50
 */
@Component
@Slf4j
public class FeiShuIsvCarReverseServiceImpl implements IFeishuIsvProcessApplyService{
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
            fbtOrderApply = commonApplyService.notifyThirdApplyRepulse(corpId, dataId, OpenType.FEISHU_ISV.getType() , ApplyOrderCategory.TAXI.getCategory(), "");
        }
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }
}
