package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dao.OpenSyncBizDataDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dao.OpenSyncBizDataHistoryDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataHistory;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IOpenSyncBizDataService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskState;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 * @date 2020/7/15
 */
@ServiceAspect
@Service
@Slf4j
public class OpenSyncBizDataServiceImpl implements IOpenSyncBizDataService {

    @Autowired
    private OpenSyncBizDataDao openSyncBizDataDao;

    @Autowired
    private OpenSyncBizDataHistoryDao openSyncBizDataHistoryDao;

    @Override
    public List<OpenSyncBizData> listOpenSyncBizData(Map<String, Object> condition) {
        List<Integer> bizType = new ArrayList<>();
        //bizType.add(OpenSyncBizDataType.DINGTALK_ISV_SUITE_TICKET.getKey());
        bizType.add(OpenSyncBizDataType.DINGTALK_ISV_CHANGE_AUTH.getKey());
        bizType.add(OpenSyncBizDataType.DINGTALK_ISV_CHANGE_STATUS.getKey());
        bizType.add(OpenSyncBizDataType.DINGTALK_ISV_ORDER.getKey());
        bizType.add(OpenSyncBizDataType.DINGTALK_ISV_TRY_OUT.getKey());
        return openSyncBizDataDao.listOpenSyncBizData(condition, bizType);
    }

    @Override
    public void saveTask(Long taskId, String result) {
        OpenSyncBizData task = openSyncBizDataDao.getById(taskId);
        if (task == null) {
            log.info("任务{}不存在！", taskId);
            return;
        }
        task.setGmtModified(new Date());
        task.setStatus(TaskState.SUCCESS.getKey());
        task.setRemark(result);
        OpenSyncBizDataHistory openSyncBizDataHistory = new OpenSyncBizDataHistory();
        BeanUtils.copyProperties(task, openSyncBizDataHistory);
        //更新任务状态
        openSyncBizDataDao.updateById(task);
        //添加历史数据
        openSyncBizDataHistoryDao.save(openSyncBizDataHistory);
        //删除源数据
        openSyncBizDataDao.deleteById(task.getId());
    }


    @Override
    public void updateTask(Long taskId, String result) {
        OpenSyncBizData task = openSyncBizDataDao.getById(taskId);
        if (task == null) {
            log.info("任务{}不存在！", taskId);
            return;
        }
        task.setGmtModified(new Date());
        task.setStatus(TaskState.FAIL.getKey());
        task.setRemark(result);
        long createTime = System.currentTimeMillis() - task.getGmtCreate().getTime();
        if (createTime <= 86400000 * 3) {
            task.setNextExecute(DateUtils.addMinute(new Date(), 30));
        } else if (createTime > 86400000 * 3 && createTime <= 86400000 * 7) {
            task.setNextExecute(DateUtils.addDay(new Date(), 1));
        }
        openSyncBizDataDao.updateById(task);
        // 7天的删除
        if (createTime > 86400000 * 7) {
            log.info("任务[{}]失败已达超限，移除任务，任务类型: {}", task.getId(), task.getBizType());
            OpenSyncBizDataHistory openSyncBizDataHistory = new OpenSyncBizDataHistory();
            BeanUtils.copyProperties(task, openSyncBizDataHistory);
            //添加历史数据
            openSyncBizDataHistoryDao.save(openSyncBizDataHistory);
            //删除源数据
            openSyncBizDataDao.deleteById(task.getId());
        }
    }

}
