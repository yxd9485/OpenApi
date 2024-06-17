package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvWebLoginInfoRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvAuthRespDTO;

/**
 *
 * @author lizhen
 * @date 2020/7/20
 */
public interface IDingtalkIsvUserAuthService {

    DingtalkIsvAuthRespDTO appAuth(String code, String corpId);

    DingtalkIsvWebLoginInfoRespDTO webLogin(String code);
}
