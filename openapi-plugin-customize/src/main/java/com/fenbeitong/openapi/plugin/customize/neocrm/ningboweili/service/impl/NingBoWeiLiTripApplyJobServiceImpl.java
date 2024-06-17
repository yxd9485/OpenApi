package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service.impl;

import com.fenbeitong.openapi.plugin.support.job.service.AbstractJobService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title: HyprocaTripApplyServiceImpl</p>
 * <p>Description: 宁波伟立差旅审批拉取</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-03 15:12
 */
@ServiceAspect
@Service
public class NingBoWeiLiTripApplyJobServiceImpl extends AbstractJobService {

    @Override
    protected List<String> getTaskType() {
        List<String> taskType = new ArrayList<>();
        taskType.add(TaskType.NINGBOWEILI_TRIP_APPLY_CREATE.getKey());
        return taskType;
    }


}