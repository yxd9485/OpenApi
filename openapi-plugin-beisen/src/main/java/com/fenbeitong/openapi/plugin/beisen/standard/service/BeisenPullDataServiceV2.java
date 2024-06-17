package com.fenbeitong.openapi.plugin.beisen.standard.service;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;

/**
 * @Description 组织架构同步
 * @Author duhui
 * @Date 2022/3/2
 **/
public interface BeisenPullDataServiceV2 {

    /**
     * 拉取北森全量的组织员工数据
     *
     * @return boolean 成功与失败
     */
    void pullAllDataV2(BeisenParamConfig beisenParamConfig);


}
