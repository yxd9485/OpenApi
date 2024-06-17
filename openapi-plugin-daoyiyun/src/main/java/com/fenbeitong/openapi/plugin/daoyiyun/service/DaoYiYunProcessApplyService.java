package com.fenbeitong.openapi.plugin.daoyiyun.service;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunCallbackBodyDTO;

/**
 * @author lizhen
 */
public interface DaoYiYunProcessApplyService {
    TaskProcessResult processApply(FinhubTask task, DaoYiYunCallbackBodyDTO daoYiYunCallbackBodyDTO, String companyId) throws Exception;

}
