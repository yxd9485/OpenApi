package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataMediumType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dao.OpenSyncBizDataMediumDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dao.OpenSyncBizDataMediumHistoryDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMediumHistory;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IOpenSyncBizDataMediumService;
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
public class OpenSyncBizDataMediumServiceImpl implements IOpenSyncBizDataMediumService {

    @Autowired
    private OpenSyncBizDataMediumDao openSyncBizDataMediumDao;

    @Autowired
    private OpenSyncBizDataMediumHistoryDao openSyncBizDataMediumHistoryDao;

    @Override
    public List<OpenSyncBizDataMedium> listOpenSyncBizMediumData(Map<String, Object> condition) {
        List<Integer> bizType = new ArrayList<>();
        bizType.add(OpenSyncBizDataMediumType.DINGTALK_ISV_USER.getKey());
        bizType.add(OpenSyncBizDataMediumType.DINGTALK_ISV_DEPARTMENT.getKey());
        bizType.add(OpenSyncBizDataMediumType.DINGTALK_ISV_COMPANY_STATUS_CHANGE.getKey());
        bizType.add(OpenSyncBizDataMediumType.DINGTALK_ISV_PROCESS_BIZ.getKey());

        return openSyncBizDataMediumDao.listOpenSyncBizDataMedium(condition, bizType);
    }

    @Override
    public void saveTask(Long taskId, String result) {
        OpenSyncBizDataMedium task = openSyncBizDataMediumDao.getById(taskId);
        if (task == null) {
            log.info("任务{}不存在！", taskId);
            return;
        }
        task.setGmtModified(new Date());
        task.setStatus(TaskState.SUCCESS.getKey());
        task.setRemark(result);
        OpenSyncBizDataMediumHistory openSyncBizDataMediumHistory = new OpenSyncBizDataMediumHistory();
        BeanUtils.copyProperties(task, openSyncBizDataMediumHistory);
        //更新任务状态
        openSyncBizDataMediumDao.updateById(task);
        //添加历史数据
        openSyncBizDataMediumHistoryDao.save(openSyncBizDataMediumHistory);
        //删除源数据
        openSyncBizDataMediumDao.deleteById(task.getId());
    }


    @Override
    public void updateTask(Long taskId, String result) {
        OpenSyncBizDataMedium task = openSyncBizDataMediumDao.getById(taskId);
        if (task == null) {
            log.info("任务{}不存在！", taskId);
            return;
        }
        task.setGmtModified(new Date());
        task.setStatus(TaskState.FAIL.getKey());
        task.setRemark(result);
        // 当前时间与上次执行时间间隔
        long createTime = System.currentTimeMillis() - task.getGmtCreate().getTime();
        if (createTime <= 86400000 * 3) {
            task.setNextExecute(DateUtils.addMinute(new Date(), 30));
        } else if (createTime > 86400000 * 3 && createTime <= 86400000 * 7) {
            task.setNextExecute(DateUtils.addDay(new Date(), 1));
        }
        openSyncBizDataMediumDao.updateById(task);
        // 7天的删除
        if (createTime > 86400000 * 7) {
            log.info("任务[{}]失败已达超限，移除任务，任务类型: {}", task.getId(), task.getBizType());
            OpenSyncBizDataMediumHistory openSyncBizDataMediumHistory = new OpenSyncBizDataMediumHistory();
            BeanUtils.copyProperties(task, openSyncBizDataMediumHistory);
            //添加历史数据
            openSyncBizDataMediumHistoryDao.save(openSyncBizDataMediumHistory);
            //删除源数据
            openSyncBizDataMediumDao.deleteById(task.getId());
        }
    }

}
