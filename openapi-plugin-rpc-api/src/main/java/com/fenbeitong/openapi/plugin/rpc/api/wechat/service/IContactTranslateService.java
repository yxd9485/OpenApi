package com.fenbeitong.openapi.plugin.rpc.api.wechat.service;

/**
 * 转译
 *
 * @author lizhen
 * @date 2020/9/23
 */
public interface IContactTranslateService {
    /**
     * 通讯录转译
     *
     * @param taskId    任务ID
     * @param ossKey    OSS文件KEY
     * @param companyId 企业ID
     */
    void translate(String taskId, String ossKey, String companyId);

}
