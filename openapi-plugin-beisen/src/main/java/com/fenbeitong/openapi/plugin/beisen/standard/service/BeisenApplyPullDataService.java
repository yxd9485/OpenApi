package com.fenbeitong.openapi.plugin.beisen.standard.service;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;

/**
 * wei.xiao
 *
 * @data 2020/06/16
 * 北森审批单拉取的服务
 */
public interface BeisenApplyPullDataService {
    /**
     * 拉取北森审批单数据
     * @return boolean 成功与失败
     */
    public boolean pullApplyData(BeisenParamConfig beisenParamConfig);


    /**
     * 拉取北森公单数据
     * @return boolean 成功与失败
     */
    public boolean pullOutWardApplyData(BeisenParamConfig beisenParamConfig);

}
