package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.task;

import com.fenbeitong.openapi.plugin.support.task.dao.TaskDao;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskDataSrc;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskState;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.AbstractTaskService;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaCallbackTagConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class YunzhijiaTaskService extends AbstractTaskService {

    @Autowired
    TaskDao taskDao;

    /**
     * 创建云之家任务,包含企业ID，事件ID，事件类型，事件内容，创建时间
     *
     * @param corpId
     * @param instanceId
     * @param eventType
     * @param msgJson
     * @param eventTime
     */
    public void createYunzhijiaTask(String corpId, String instanceId, String eventType, String msgJson, String eventTime) {
        Task task = new Task();
        task.setCorpId(corpId);
        task.setDataId(instanceId);
        //人员新增
        if (YunzhijiaCallbackTagConstant.YUNZHIJIA_USER_ADD_ORG.equals(eventType)) {
            eventType = TaskType.YUNZHIJIA_USER_ADD_ORG.getKey();
        }
        //人员修改包含三种情况,部门修改，手机号修改，角色修改
        if (YunzhijiaCallbackTagConstant.YUNZHIJIA_USER_MODIFY_ORG.equals(eventType) || YunzhijiaCallbackTagConstant.YUNZHIJIA_USER_MODIFY_PHONE.equals(eventType) || YunzhijiaCallbackTagConstant.YUNZHIJIA_USER_MODIFY_ROLE.equals(eventType)) {//修改手机事件,或者修改角色
            eventType = TaskType.YUNZHIJIA_USER_MODIFY_ORG.getKey();
        }
        //人员删除
        if (YunzhijiaCallbackTagConstant.YUNZHIJIA_USER_LEAVE_ORG.equals(eventType)) {
            eventType = TaskType.YUNZHIJIA_USER_LEAVE_ORG.getKey();
        }
        //部门新增
        if (YunzhijiaCallbackTagConstant.YUNZHIJIA_ORG_DEPT_CREATE.equals(eventType)) {
            eventType = TaskType.YUNZHIJIA_ORG_DEPT_CREATE.getKey();
        }
        //部门修改
        if (YunzhijiaCallbackTagConstant.YUNZHIJIA_ORG_DEPT_MODIFY.equals(eventType)) {
            eventType = TaskType.YUNZHIJIA_ORG_DEPT_MODIFY.getKey();
        }
        //部门删除
        if (YunzhijiaCallbackTagConstant.YUNZHIJIA_ORG_DEPT_REMOVE.equals(eventType)) {
            eventType = TaskType.YUNZHIJIA_ORG_DEPT_REMOVE.getKey();
        }
        //修改部门负责人
        if (YunzhijiaCallbackTagConstant.YUNZHIJIA_ORG_DEPT_LEADER_MODIFY.equals(eventType)) {
            eventType = TaskType.YUNZHIJIA_ORG_DEPT_LEADER_MODIFY.getKey();
        }
        // TODO 修改角色情况，后期需要根据客户需求与云之家和分贝通角色相匹配
        task.setTaskType(eventType);
        task.setDataSrc(TaskDataSrc.CALLBACK.getKey());
        task.setDataContent(msgJson);
        task.setEventTime(Long.valueOf(eventTime));
        super.createTask(task);
    }
    @Override
    public String getProcessorKey() {
        return this.getClass().getName();
    }
}
