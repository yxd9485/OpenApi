package com.fenbeitong.openapi.plugin.kingdee.common.service;

/**
 * 金蝶项目同步通用逻辑
 * @Auther zhang.peng
 * @Date 2021/6/3
 */
public interface KingDeeProjectSyncService {

    /**
     * 从三方向分贝通同步项目信息
     * @param companyId 公司id
     * @return success 成功 ; failed 失败
     */
    String syncItem(String companyId);
}
