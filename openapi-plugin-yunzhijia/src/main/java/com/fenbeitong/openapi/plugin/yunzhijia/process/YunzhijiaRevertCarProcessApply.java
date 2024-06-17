package com.fenbeitong.openapi.plugin.yunzhijia.process;

import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 云之家反向用车审批
 * @Auther zhang.peng
 * @Date 2021/4/28
 */
@ServiceAspect
@Service
public class YunzhijiaRevertCarProcessApply extends AbstractApplyService implements IYunzhijiaProcessApply {

    @Autowired
    CommonApplyServiceImpl commonApplyService;

    @Override
    public TaskResult processApply(Task task, ThirdApplyDefinition thirdApplyDefinition, PluginCorpDefinition apply, YunzhijiaApplyEventDTO.YunzhijiaApplyData yunzhijiaApplyData) {
        //1.接收到云之家消息回调通知，解析处理
        String corpId = task.getCorpId();
        String dataId = task.getDataId();
        boolean fbtOrderApply = commonApplyService.notifyApplyAgree(corpId, dataId, OpenType.YUNZHIJIA.getType());
        if (fbtOrderApply) {
            return TaskResult.SUCCESS;
        } else {
            return TaskResult.FAIL;
        }
    }

}
