package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import com.fenbeitong.openapi.plugin.support.job.service.AbstractJobService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xiaowei
 */
@ServiceAspect
@Service
@Slf4j
public class BeisenOutwardApplyJobService extends AbstractJobService {
    /**
     * 获取各自业务分支的taskType
     * @return
     */
    @Override
    protected List<String> getTaskType() {
        List<String> taskType = new ArrayList<>();
        taskType.add(TaskType.BEISEN_OUTWARD_EVENT_CREATE.getKey());
        return taskType;
    }


}