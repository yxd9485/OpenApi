package com.fenbeitong.openapi.plugin.dingtalk.yida.service;

import java.util.Map;

/**
 * 宜搭表单同步业务
 *
 * @author ctl
 * @date 2022/3/4
 */
public interface IYiDaFormSyncService {

    /**
     * 执行业务
     *
     * @param params
     * @param companyId
     */
    void execute(Map<String, Object> params, String companyId);

}
