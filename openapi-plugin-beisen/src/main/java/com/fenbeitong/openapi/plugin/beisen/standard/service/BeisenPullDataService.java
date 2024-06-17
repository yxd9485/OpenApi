package com.fenbeitong.openapi.plugin.beisen.standard.service;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenRankParamConfig;

/**
 * wei.xiao
 *
 * @data 2020/06/16
 * 北森同步数据的服务接口
 */
public interface BeisenPullDataService {
    /**
     * 拉取北森全量的组织员工数据
     *
     * @return boolean 成功与失败
     */
    void pullAllData(BeisenParamConfig beisenParamConfig);

    /**
     * 拉取北森增量（时间维度的）的组织员工数据
     *
     * @return boolean 成功与失败  hours  指的是拉取多少小时前的数据
     */
    boolean pullIncrementalData(BeisenParamConfig beisenParamConfig);

    /**
     * 全量拉取北森职级数据同步
     * @param beisenRankParamConfig
     * @throws Exception
     */
    void syncAllRank(BeisenRankParamConfig beisenRankParamConfig);


}
