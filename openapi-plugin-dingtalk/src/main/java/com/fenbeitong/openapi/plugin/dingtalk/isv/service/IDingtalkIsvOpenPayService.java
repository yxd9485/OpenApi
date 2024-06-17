package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;

/**
 * 钉钉购买
 *
 * @author lizhen
 */
public interface IDingtalkIsvOpenPayService {
    /**
     * 充值
     *
     * @param user
     * @param callbackPage
     * @return
     */
    String recharge(UserComInfoVO user, String callbackPage);
}
