package com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.dao.DingtalkApproveTaskDao;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.entity.DingtalkApproveTask;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.service.IDingtalkApproveTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;
import java.util.List;

/**
 * @author xiaohai
 * @date 2021/08/06
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkApproveTaskServiceImpl implements IDingtalkApproveTaskService {

    @Autowired
    private DingtalkApproveTaskDao dingtalkApproveTaskDao;


    @Override
    public DingtalkApproveTask getDingtalkApproveTaskByApproveIdAndUserId(String approveId, String userId) {
        return dingtalkApproveTaskDao.getDingtalkApproveTaskByApproveIdAndUserId(approveId, userId);
    }

    @Override
    public void insertDingtalkApproveTask(DingtalkApproveTask task) {
        String approveId = task.getApproveId();
        String userId = task.getUserId();
        long taskId = task.getTaskId();
        DingtalkApproveTask dingtalkApprove= DingtalkApproveTask.builder().approveId(approveId).approveStauts(0).userId(userId).taskId(taskId).
                createTime(new Date()).updateTime(new Date()).build();
        dingtalkApproveTaskDao.save(dingtalkApprove);
    }

    @Override
    public void updateDingtalkApproveById(DingtalkApproveTask task) {
        dingtalkApproveTaskDao.updateById(task);
    }

    @Override
    public List<DingtalkApproveTask> getDingtalkApproveTaskByStatus(String approveId , Integer approveStauts) {
        return dingtalkApproveTaskDao.getDingtalkApproveTaskByApproveIdAndStatus( approveId , approveStauts);
    }

    //修改表中审批状态
    public void updateMessageStatus( List<DingtalkApproveTask> dingtalkApproveTaskByApproveList ){
        //通过审批单id查询出所有的未处理的任务(0、未处理 1、已处理)
        if(dingtalkApproveTaskByApproveList!=null && dingtalkApproveTaskByApproveList.size()>0){
            //修改所有历史节点为已处理
            dingtalkApproveTaskByApproveList.forEach( task -> {
                task.setApproveStauts(1);
                dingtalkApproveTaskDao.updateById(task);//修改表中数据
            });
        }
    }

}
