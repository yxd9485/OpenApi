package com.fenbeitong.openapi.plugin.yunzhijia.process;

import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;

public interface IYunzhijiaProcessApply {
    TaskResult processApply(Task task, ThirdApplyDefinition thirdApplyDefinition,PluginCorpDefinition apply, YunzhijiaApplyEventDTO.YunzhijiaApplyData yunzhijiaApplyData);
}
