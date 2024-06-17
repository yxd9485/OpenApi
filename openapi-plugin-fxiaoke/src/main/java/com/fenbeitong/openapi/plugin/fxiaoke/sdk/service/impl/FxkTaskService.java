package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.finhub.common.utils.CheckUtils;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dao.FxiaokeTaskDao;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity.FxiaokeTask;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskState;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.service.AbstractTaskService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class FxkTaskService extends AbstractTaskService {


    @Autowired
    FxiaokeTaskDao fxiaokeTaskDao;

    @Override
    public String getProcessorKey() {
        return this.getClass().getName();
    }


    public void updateProcessingTask2Pending() {
        log.debug("设置正在执行的任务为待执行状态");
        //只更新企业微信审批，钉钉原有项目已更新所有审批类型，因此无需多处理
        Example example = new Example(FxiaokeTask.class);
        example.createCriteria()
                .andEqualTo("state", TaskState.PROCESSING.getKey())
                .andEqualTo("taskType", TaskType.WECHAT_EIA_APPROVAL_CREATE.getKey());
        FxiaokeTask upTask = new FxiaokeTask();
        upTask.setState(TaskState.PENDING.getKey());
        upTask.setUpdateTime(new Date());
        fxiaokeTaskDao.updateByExample(upTask, example);
    }


    public Task beginTask(Long taskId) {
        CheckUtils.checkNull(taskId, "taskId 不能为空！");
        ObjectUtils.isEmpty(taskId);
        FxiaokeTask task = fxiaokeTaskDao.getById(taskId);
        if (task == null) {
            log.info("任务{}不存在！", taskId);
            return null;
        }
        FxiaokeTask upTask = new FxiaokeTask();
        upTask.setId(task.getId());
        upTask.setExecuteBegin(new Date());
        upTask.setState(TaskState.PROCESSING.getKey());
        upTask.setUpdateTime(new Date());

        Example example = new Example(Task.class);
        example.createCriteria().andEqualTo("id", task.getId());
        fxiaokeTaskDao.updateByExample(upTask, example);
        return upTask;
    }


    public void saveTask(Long taskId, String result) {
        CheckUtils.create()
                .addCheckNull(taskId, "taskId 不能为空！")
                .addCheckEmpty(result, "result 不能为空！");
        FxiaokeTask task = fxiaokeTaskDao.getById(taskId);
        if (task == null) {
            log.info("任务{}不存在！", taskId);
            return;
        }
        FxiaokeTask upTask = new FxiaokeTask();
        upTask.setId(task.getId());
        upTask.setExecuteNum(ObjUtils.ifNull(task.getExecuteNum(), 0) + 1);
        upTask.setExecuteEnd(new Date());
        upTask.setExecuteResult(result);
        if (result.equals(TaskResult.FAIL.getMsg())) {//判断执行状态
            upTask.setState(TaskState.FAIL.getKey());
        } else if (result.equals(TaskResult.SUCCESS.getMsg())) {
            upTask.setState(TaskState.SUCCESS.getKey());
        }
        upTask.setUpdateTime(new Date());
        log.info("执行任务成功更新任务对象 {}", JsonUtils.toJson(upTask));

        Example example = new Example(FxiaokeTask.class);
        example.createCriteria().andEqualTo("id", task.getId());
        //更新任务状态
        fxiaokeTaskDao.updateByExample(upTask, example);
        //添加历史数据
//        fxiaokeTaskDao.insert2HistoryById(task.getId());
        //删除源数据
//        fxiaokeTaskDao.deleteById(task.getId());
    }


    public void updateTask(Long taskId, String result) {
        CheckUtils.create()
                .addCheckNull(taskId, "taskId 不能为空！")
                .addCheckEmpty(result, "result 不能为空！");
        FxiaokeTask task = fxiaokeTaskDao.getById(taskId);
        if (task == null) {
            log.info("任务{}不存在！", taskId);
            return;
        }
        FxiaokeTask upTask = new FxiaokeTask();
        upTask.setId(task.getId());
        upTask.setExecuteNum(ObjUtils.ifNull(task.getExecuteNum(), 0) + 1);
        upTask.setExecuteEnd(new Date());
        upTask.setExecuteResult(result);
        upTask.setState(TaskState.FAIL.getKey());
        upTask.setUpdateTime(new Date());
        // 达max的失败任务，设置下次执行时间，创建时间0-3天为30分钟，3-7天为1天，超过7天的放弃
        long createTime = System.currentTimeMillis() - task.getCreateTime().getTime();
        if (upTask.getExecuteNum() >= task.getExecuteMax()) {
            // 当前时间与上次执行时间间隔
            if (createTime <= 86400000 * 3) {
                upTask.setNextExecute(DateUtils.addMinute(upTask.getExecuteEnd(), 30));
            } else if (createTime > 86400000 * 3 && createTime <= 86400000 * 7) {
                upTask.setNextExecute(DateUtils.addDay(upTask.getExecuteEnd(), 1));
            }
        }
        fxiaokeTaskDao.updateById(upTask);
        if (createTime > 86400000 * 7) {
            log.info("任务[{}]失败已达超限，移除任务，任务类型: {}", task.getId(), task.getTaskType());
            //添加历史数据
            fxiaokeTaskDao.insert2HistoryById(task.getId());
            //删除源数据
            fxiaokeTaskDao.deleteById(task.getId());
        }
    }


    public int countFxiaokeNeedProcessedTask() {
        return fxiaokeTaskDao.countFxiaokeNeedProcessedTask();
    }

    public List<FxiaokeTask> getFxiaokeNeedProcessedTaskList(int taskLimit) {
        return fxiaokeTaskDao.getFxiaokeNeedProcessedTaskList(taskLimit);
    }


}
