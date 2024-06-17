package com.fenbeitong.openapi.plugin.qiqi.service.addjob;

/**
 * @ClassName IQiqiDataAddJobService
 * @Description 企企增量数据同步
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/06/07
 **/
public interface IQiqiDataAddJobService {
    /**
     * 企企增量数据同步
     *
     * @param companyId
     * @throws Exception
     */
    void syncAddData(String companyId) throws Exception;
}
