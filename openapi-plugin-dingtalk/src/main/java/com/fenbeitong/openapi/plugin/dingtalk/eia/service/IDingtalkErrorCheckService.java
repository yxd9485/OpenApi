package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import java.io.IOException;

/**
 * dingtalk_task失败检查
 * @author lizhen
 * @date 2020/11/4
 */
public interface IDingtalkErrorCheckService {
    void checkFailedTask();

    void checkFailedTaskAndPhone() throws IOException;

    void failOrgList2CsmOrClient();

}
