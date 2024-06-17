package com.fenbeitong.openapi.plugin.dingtalk.yida.service;

import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaApplyDetailRespDTO;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;

/**
 * <p>Title: IDingtalkProcessApplyService</p>
 * <p>Description: 易搭审批</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/21 3:44 PM
 */
public interface IYiDaProcessApplyService {

    /**
     *
     * @param companyId
     * @param yiDaApplyDetailRespDTO
     * @return
     */
    TaskResult processApply(String companyId, YiDaApplyDetailRespDTO yiDaApplyDetailRespDTO) throws Exception;


}
