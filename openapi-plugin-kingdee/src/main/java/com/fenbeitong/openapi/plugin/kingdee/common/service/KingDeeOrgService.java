package com.fenbeitong.openapi.plugin.kingdee.common.service;


/**
 * 金蝶相关接口
 *
 * @Auther zhang.peng
 * @Date 2021/6/7
 */
public interface KingDeeOrgService {

    /**
     * 组织架构同步
     */

    String syncOrganization(String companyId);

}
