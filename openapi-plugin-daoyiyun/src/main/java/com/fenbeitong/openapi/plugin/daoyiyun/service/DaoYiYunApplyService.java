package com.fenbeitong.openapi.plugin.daoyiyun.service;

/**
 * 审批
 * @author lizhen
 */
public interface DaoYiYunApplyService {
    /**
     * 创建表单
     * @param body 单据体
     * @param applicationId 应用id
     * @param formModelId 表单id
     */
    String createApplyInstance(String body, String applicationId, String formModelId);
}
