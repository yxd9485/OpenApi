package com.fenbeitong.openapi.plugin.welink.isv.service.job;

import com.fenbeitong.openapi.plugin.support.task.dao.TaskDao;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskDataSrc;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskState;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.AbstractTaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvOrganizationService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 微信isv任务服务
 * Created by lizhen on 2020/3/26.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvTaskService extends AbstractTaskService {

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private WeLinkIsvOrganizationService weLinkIsvOrganizationService;

    @Override
    public String getProcessorKey() {
        return this.getClass().getName();
    }

    /**
     * 查询需要处理的任务数据
     *
     * @return
     */
    public int countWeLinkIsvNeedProcessedTask() {
        return taskDao.countWeLinkIsvNeedProcessedTask();
    }

    public List<Task> getWeLinkIsvNeedProcessedTaskList(int taskLimit) {
        return taskDao.getWeLinkIsvNeedProcessedTaskList(taskLimit);
    }


    /**
     * 保存人员task
     *
     * @param eventMsg
     */
    public void genWeLinkIsvUserTask(Map<String, Object> eventMsg) {
        String eventType = (String) eventMsg.get("EventType");
        String corpId = (String) eventMsg.get("CorpId");
        String eventTime = (String) eventMsg.get("TimeStamp");
        List<String> userIds = (List<String>) eventMsg.get("UserId");
        for (String userId : userIds) {
            List<Task> updateOrAddList = null;
            if (TaskType.WELINK_ISV_CORP_EDIT_USER.getKey().equals(eventType)) {
                updateOrAddList = taskDao.listUpdateOrAddTaskWithCondition(corpId, userId, Lists.newArrayList(TaskType.WELINK_ISV_CORP_EDIT_USER.getKey()));
            }
            if (updateOrAddList != null && updateOrAddList.size() > 0) {
                Task updateOrAddTask = updateOrAddList.get(0);
                updateOrAddTask.setDataSrc(TaskDataSrc.CALLBACK.getKey());
                updateOrAddTask.setDataContent(JsonUtils.toJson(eventMsg));
                updateOrAddTask.setEventTime(Long.valueOf(eventTime));
                updateOrAddTask.setExecuteNum(0);
                updateOrAddTask.setState(TaskState.PENDING.getKey());
                updateOrAddTask.setUpdateTime(new Date());
                taskDao.updateById(updateOrAddTask);
            } else {
                Task task = new Task();
                task.setCorpId(corpId);
                task.setDataId(userId);
                task.setTaskType(eventType);
                task.setDataSrc(TaskDataSrc.CALLBACK.getKey());
                task.setDataContent(JsonUtils.toJson(eventMsg));
                task.setEventTime(Long.valueOf(eventTime));
                task.setExecuteMax(3);
                createTask(task);
            }
        }
    }

    /**
     * 保存部门task
     *
     * @param eventMsg
     */
    public void genWeLinkIsvDepartmentTask(Map<String, Object> eventMsg) {
        String eventType = (String) eventMsg.get("EventType");
        String corpId = (String) eventMsg.get("CorpId");
        List<?> ids = (List<?>) eventMsg.get("DeptId");
        String eventTime = (String) eventMsg.get("TimeStamp");

        List<String> deptIds = ids.stream().map(String::valueOf).collect(Collectors.toList());
        int size = deptIds.size();
        //一次添加多个部门，需要先对部门进行排序
        if (size > 1 && eventType.equals(TaskType.WELINK_ISV_CORP_EDIT_DEPT.getKey())) {
            // 根据部门级别进行排序,添加时先添加上级部门
            deptIds = weLinkIsvOrganizationService.getSortedDepartments(deptIds, corpId);
        }
        int max = 3;
        if (eventType.equals(TaskType.WELINK_ISV_CORP_EDIT_DEPT.getKey())) {
            // 添加和删除操作有可能存在上级/下级部门未添加/删除的情况，最坏情况下需要执行size次操作。
            max = size;
        }
        if (eventType.equals(TaskType.WELINK_ISV_CORP_DEL_DEPT.getKey())) {
            max = size;
            // 从叶子节点开始删除，可以减少失败次数
            Collections.reverse(deptIds);
        }
        for (String deptId : deptIds) {
            List<Task> updateOrAddList = null;
            if (TaskType.WELINK_ISV_CORP_EDIT_DEPT.getKey().equals(eventType)) {
                updateOrAddList = taskDao.listUpdateOrAddTaskWithCondition(corpId, deptId, Lists.newArrayList(TaskType.WELINK_ISV_CORP_EDIT_DEPT.getKey()));
            }
            if (updateOrAddList != null && updateOrAddList.size() > 0) {
                Task updateOrAddTask = updateOrAddList.get(0);
                updateOrAddTask.setDataSrc(TaskDataSrc.CALLBACK.getKey());
                updateOrAddTask.setDataContent(JsonUtils.toJson(eventMsg));
                updateOrAddTask.setEventTime(Long.valueOf(eventTime));
                updateOrAddTask.setExecuteNum(0);
                updateOrAddTask.setState(TaskState.PENDING.getKey());
                updateOrAddTask.setUpdateTime(new Date());
                taskDao.updateById(updateOrAddTask);
            } else {
                Task task = new Task();
                task.setCorpId(corpId);
                task.setDataId(deptId);
                task.setTaskType(eventType);
                task.setDataSrc(TaskDataSrc.CALLBACK.getKey());
                task.setDataContent(JsonUtils.toJson(eventMsg));
                task.setEventTime(Long.valueOf(eventTime));
                task.setExecuteMax(max);
                task.setExecuteMax(3);
                createTask(task);
            }
        }
    }

    /**
     * 变更授权task
     *
     * @param eventMsg
     */
    public void genWeLinkIsvChangeAuthTask(Map<String, Object> eventMsg) {
        String eventType = (String) eventMsg.get("EventType");
        String corpId = (String) eventMsg.get("CorpId");
        String eventTime = (String) eventMsg.get("TimeStamp");
        Task task = new Task();
        task.setCorpId(corpId);
        task.setDataId(corpId);
        task.setTaskType(eventType);
        task.setDataSrc(TaskDataSrc.CALLBACK.getKey());
        task.setDataContent(JsonUtils.toJson(eventMsg));
        task.setEventTime(Long.valueOf(eventTime));
        task.setExecuteMax(3);
        createTask(task);
    }

    /**
     * 新购和续费
     * @param eventMsg
     */
    public void genWeLinkIsvCorpNewInstanceAndRefreshTask(Map<String, Object> eventMsg) {
        String eventType = (String) eventMsg.get("EventType");
        String corpId = (String) eventMsg.get("CorpId");
        String eventTime = (String) eventMsg.get("TimeStamp");
        String dataId = (String) eventMsg.get("DataId");
        String dataContent = (String) eventMsg.get("DataContent");
        Task task = new Task();
        task.setCorpId(corpId);
        task.setDataId(dataId);
        task.setTaskType(eventType);
        task.setDataSrc(TaskDataSrc.CALLBACK.getKey());
        task.setDataContent(dataContent);
        task.setEventTime(Long.valueOf(eventTime));
        task.setExecuteMax(3);
        createTask(task);
    }
}
