package com.fenbeitong.openapi.plugin.feishu.isv.controller;

import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCallbackService;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 飞书回调controller
 *
 * @author lizhen
 * @date 2020/6/1
 */
@Controller
@Slf4j
@RequestMapping("/feishu/isv/callback")
public class FeiShuIsvCallbackController {

    @Autowired
    private FeiShuIsvCallbackService feiShuIsvCallbackService;

    @RequestMapping("/command")
    @ResponseBody
    public Object callBack(HttpServletRequest request) throws Exception {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        log.info("【feishu isv】callback 接收到消息回调:{}", requestBody);
        if (StringUtils.isBlank(requestBody)) {
            return null;
        }
        String result = feiShuIsvCallbackService.commandCallback(requestBody);
        return result;
    }

}
