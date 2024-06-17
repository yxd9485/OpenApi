package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeAuthRespDTO;

/**
 * @author lizhen
 */
public interface IFxkUserAuthService {
    /**
     * 应用免登
     *
     * @param code
     * @param appId
     * @param state
     * @return
     */
    FxiaokeAuthRespDTO auth(String code, String appId, String state);

    /**
     * 别名做工号
     *
     * @param companyId
     * @return
     */
    boolean useNickNameToEmployeeNumber(String companyId);
}
