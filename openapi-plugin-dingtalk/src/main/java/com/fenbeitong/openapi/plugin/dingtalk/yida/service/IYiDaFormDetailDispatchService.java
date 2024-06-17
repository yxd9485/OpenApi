package com.fenbeitong.openapi.plugin.dingtalk.yida.service;

import java.util.Map;

/**
 * 宜搭表单详情业务调度
 *
 * @author ctl
 * @date 2022/3/4
 */
public interface IYiDaFormDetailDispatchService {

    /**
     * 调度业务
     *
     * @param parameterMap
     */
    void dispatch(Map<String, Object> parameterMap);
}
