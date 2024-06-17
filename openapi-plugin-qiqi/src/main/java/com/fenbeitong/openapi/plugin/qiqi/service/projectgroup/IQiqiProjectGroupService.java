package com.fenbeitong.openapi.plugin.qiqi.service.projectgroup;

/**
 * @ClassName IQiqiProjectGroupService
 * @Description 企企同步项目分组数据
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
public interface IQiqiProjectGroupService {

    /**
     * 全量拉取项目分组数据
     * @param companyId
     * @throws Exception
     */
    void syncQiqiProjectGroup(String companyId) throws Exception;
}
