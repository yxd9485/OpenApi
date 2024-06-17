package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

import com.fenbeitong.openapi.plugin.support.webhook.dto.WebHookOrderDTO;

/**
 * 泛微待办接口
 * @Auther zhang.peng
 * @Date 2021/12/7
 */
public interface IEcologyToDoService {

    /**
     * 创建泛微待办
     * @param requestBody
     * @return true 成功 ; false 失败
     */
    boolean createEcologyToDo( WebHookOrderDTO webHookOrderDTO );

    /**
     * 完成泛微待办
     * @param requestBody
     * @return true 成功 ; false 失败
     */
    boolean finishEcologyToDo( WebHookOrderDTO webHookOrderDTO );

    /**
     * 删除泛微待办
     * @param webHookOrderDTO
     * @return true 成功 ; false 失败
     */
    boolean deleteEcologyToDo( WebHookOrderDTO webHookOrderDTO );

}
