package com.fenbeitong.openapi.plugin.wechat.eia.service.job;

import com.fenbeitong.openapi.plugin.support.task.dao.TaskDao;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskDataSrc;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskState;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.AbstractTaskService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * 微信任务服务
 * Created by dave.hansins on 19/12/10.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatEiaTaskService extends AbstractTaskService {

    @Autowired
    TaskDao taskDao;

    @Override
    public String getProcessorKey() {
        return this.getClass().getName();
    }

    /**
     * 创建微信任务
     */
    public void createWeChatTask(String corpId, String instanceId, String eventType, String msgJson, Long eventTime) {
        Task task = new Task();
        task.setCorpId(corpId);
        task.setDataId(instanceId);
        task.setTaskType(eventType);
        task.setDataSrc(TaskDataSrc.CALLBACK.getKey());
        task.setDataContent(msgJson);
        task.setEventTime(eventTime);
        super.createTask(task);
    }

}
