package com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.service;

import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.entity.DingtalkApproveTask;

import java.util.List;

/**
 * @author xiaohai
 * @date 2021/08/06
 */
public interface IDingtalkApproveTaskService {

    //通过审批单id和用户id查询审批任务
    DingtalkApproveTask getDingtalkApproveTaskByApproveIdAndUserId(String approveId, String userId);

    //新增任务信息
    void insertDingtalkApproveTask( DingtalkApproveTask task );

    //通过id修改任务状态
    void updateDingtalkApproveById( DingtalkApproveTask task );

    //通过状态查询所有任务
    List<DingtalkApproveTask> getDingtalkApproveTaskByStatus( String approveId,Integer approveStauts );

    //修改表中未处理的状态修改成已处理
     void updateMessageStatus( List<DingtalkApproveTask> dingtalkApproveTaskByApproveList );

}
