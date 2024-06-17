package com.fenbeitong.openapi.plugin.dingtalk.yida.service;

import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaCallbackDTO;

/**
 * <p>Title: IYiDaCallbackService</p>
 * <p>Description:回调处理 </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/13 7:23 下午
 */
public interface IYiDaCallbackService {

    /**
     * 回调处理
     *
     * @param callbackParam
     */
    void callbackCommand(YiDaCallbackDTO callbackParam);
}
