package com.fenbeitong.openapi.plugin.customize.wanyang.service;

/**
 * @ClassName OrgUnitSyncService
 * @Description 万洋组织架构同步
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/8/2 下午7:56
 **/
public interface OrgUnitSyncService {
    /**
     * 同步万洋组织架构
     * @author helu
     * @date 2022/8/2 下午7:58
     * @param companyId
     */
    void syncOrgUnit(String companyId);
}
