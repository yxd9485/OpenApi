package com.fenbeitong.openapi.plugin.ecology.v8.standard.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.EcologyToDoFactoryService;
import com.fenbeitong.openapi.plugin.support.webhook.dto.WebHookData;
import com.fenbeitong.openapi.plugin.support.webhook.dto.WebHookOrderDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 泛微待办相关接口
 * @Auther zhang.peng
 * @Date 2021/12/13
 */
@RestController
@RequestMapping("/ecology/standard/fanwei/todo")
@Slf4j
public class EcologyPushToDoController {

    @Autowired
    private EcologyToDoFactoryService ecologyToDoFactoryService;

    @RequestMapping("/push")
    @ResponseBody
    public Object pushToDoApplyInfo(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        log.info("接到待办数据 : {}",requestBody);
        WebHookData webHookData = JsonUtils.toObj(requestBody,WebHookData.class);
        if ( null == webHookData ){
            log.info("数据为空");
            return OpenapiResponseUtils.error(-1, "创建泛微待办失败,待办数据为空" );
        }
        WebHookOrderDTO webHookOrderDTO = webHookData.getWebhookOrder();
        boolean result = ecologyToDoFactoryService.doEcologyToDo(webHookOrderDTO);
        if (result) {
            return OpenapiResponseUtils.success("调用泛微待办接口成功");
        } else {
            return OpenapiResponseUtils.error(-1, "调用泛微待办接口失败" );
        }
    }

}
