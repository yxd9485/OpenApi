package com.fenbeitong.openapi.plugin.qiqi.service.rank;

/**
 * @ClassName IQiqiRankService
 * @Description 企企职级数据同步
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/28
 **/
public interface IQiqiRankService {

    /**
     * 全量拉取职级数据同步
     * @param companyId
     * @throws Exception
     */
    void syncQiqiRank(String companyId) throws Exception;
}
