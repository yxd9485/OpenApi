package com.fenbeitong.openapi.plugin.feishu.eia.controller;

import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaCallbackService;
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
 * @date 2020/7/7
 */
@Controller
@Slf4j
@RequestMapping("/feishu/eia/callback")
public class FeiShuEiaCallbackController {

    @Autowired
    private FeiShuEiaCallbackService feiShuEiaCallbackService;

    @RequestMapping("/command")
    @ResponseBody
    public Object callBack(HttpServletRequest request) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        log.info("【feishu eia】callback 接收到消息回调:{}", requestBody);
        if (StringUtils.isBlank(requestBody)) {
            return null;
        }
        String result = feiShuEiaCallbackService.commandCallback(requestBody);
        return result;
    }

}
