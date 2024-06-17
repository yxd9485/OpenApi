package com.fenbeitong.openapi.plugin.wechat.isv.service.job;

import com.fenbeitong.openapi.plugin.support.task.dao.TaskDao;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskDataSrc;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskState;
import com.fenbeitong.openapi.plugin.support.task.service.AbstractTaskService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
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
public class WeChatIsvTaskService extends AbstractTaskService {

    @Autowired
    TaskDao taskDao;

    @Override
    public String getProcessorKey() {
        return this.getClass().getName();
    }

    /**
     * 查询需要处理的任务数据
     *
     * @return
     */
    public int countWeChatIsvNeedProcessedTask() {
        return taskDao.countWeChatIsvNeedProcessedTask();
    }

    public List<Task> getWeChatIsvNeedProcessedTaskList(int taskLimit) {
        return taskDao.getWeChatIsvNeedProcessedTaskList(taskLimit);
    }


    /**
     * 保存人员task
     *
     * @param eventMsg
     */
    public void genWeChatIsvUserTask(Map<String, Object> eventMsg) {
        String eventType = (String) eventMsg.get("EventType");
        String corpId = (String) eventMsg.get("CorpId");
        String eventTime = (String) eventMsg.get("TimeStamp");
        List<String> userIds = (List<String>) eventMsg.get("UserId");
        for (String userId : userIds) {
            List<Task> updateOrAddList = null;
            if (WeChatIsvConstant.WECHAT_ISV_CREATE_USER.equals(eventType) || WeChatIsvConstant.WECHAT_ISV_UPDATE_USER.equals(eventType)) {
                updateOrAddList = taskDao.listUpdateOrAddTaskWithCondition(corpId, userId, Lists.newArrayList(WeChatIsvConstant.WECHAT_ISV_CREATE_USER, WeChatIsvConstant.WECHAT_ISV_UPDATE_USER));
            }
            if (updateOrAddList != null && updateOrAddList.size() > 0) {
                Task updateOrAddTask = updateOrAddList.get(0);
                if (WeChatIsvConstant.WECHAT_ISV_CREATE_USER.equals(eventType)) {
                    updateOrAddTask.setTaskType(WeChatIsvConstant.WECHAT_ISV_CREATE_USER);
                    updateOrAddTask.setExecuteMax(1);
                }
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
                // 修改操作有可能会先于添加操作达到，需要设置重试次数3次
                if (WeChatIsvConstant.WECHAT_ISV_UPDATE_USER.equals(eventType)) {
                    task.setExecuteMax(3);
                } else {
                    task.setExecuteMax(1);
                }
                createTask(task);
            }
        }

    }

    /**
     * 保存部门task
     *
     * @param eventMsg
     */
    public void genQywxDepartmentTask(Map<String, Object> eventMsg) {
        String eventType = (String) eventMsg.get("EventType");
        String corpId = (String) eventMsg.get("CorpId");
        List<?> ids = (List<?>) eventMsg.get("DeptId");
        String eventTime = (String) eventMsg.get("TimeStamp");

        List<Long> deptIds = ids.stream().map(String::valueOf).map(Long::valueOf).collect(Collectors.toList());
        int size = deptIds.size();
        //一次添加多个部门，需要先对部门进行排序
//        if (size > 1 && eventType.equals(QywxCallbackTagConstant.ORG_DEPT_CREATE)) {
//            // 根据部门级别进行排序,添加时先添加上级部门
//            deptIds = dingtalkApiFacade.sortDepartments(deptIds, corpId);
//        }
        int max = 3;
        if (eventType.equals(WeChatIsvConstant.WECHAT_ISV_ORG_DEPT_CREATE)) {
            // 添加和删除操作有可能存在上级/下级部门未添加/删除的情况，最坏情况下需要执行size次操作。
            max = size;
        }
        if (eventType.equals(WeChatIsvConstant.WECHAT_ISV_ORG_DEPT_REMOVE)) {
            max = size;
            // 从叶子节点开始删除，可以减少失败次数
            Collections.reverse(deptIds);
        }
        for (Long deptId : deptIds) {
            Task task = new Task();
            task.setCorpId(corpId);
            task.setDataId(String.valueOf(deptId));
            task.setTaskType(eventType);
            task.setDataSrc(TaskDataSrc.CALLBACK.getKey());
            task.setDataContent(JsonUtils.toJson(eventMsg));
            task.setEventTime(Long.valueOf(eventTime));
            task.setExecuteMax(max);
            createTask(task);
        }
    }

    /**
     * 变更授权task
     *
     * @param eventMsg
     */
    public void genWeChatIsvChangeAuthTask(Map<String, Object> eventMsg) {
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
        task.setExecuteMax(1);
        createTask(task);

    }

}
