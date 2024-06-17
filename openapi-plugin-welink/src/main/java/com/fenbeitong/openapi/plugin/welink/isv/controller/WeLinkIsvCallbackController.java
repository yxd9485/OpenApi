package com.fenbeitong.openapi.plugin.welink.isv.controller;

import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.welink.common.util.WeLinkIsvMarketEncryptUtils;
import com.fenbeitong.openapi.plugin.welink.isv.dto.WeLinkIsvCallbackRespDTO;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvCallbackService;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.welink.isv.service.WeLinkIsvUserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * welink回调
 * Created by lizhen on 2020/4/13.
 */
@Controller
@Slf4j
@RequestMapping("/welink/isv/callback")
public class WeLinkIsvCallbackController {

    @Value("${welink.isv.appSecret}")
    private String appSecret;
    @Value("${welink.isv.key}")
    private String key;
    @Autowired
    private WeLinkIsvUserAuthService weLinkIsvUserAuthService;
    @Autowired
    private WeLinkIsvCompanyAuthService weLinkIsvCompanyAuthService;
    @Autowired
    private WeLinkIsvCallbackService weLinkIsvCallbackService;

    @RequestMapping("/trial")
    @ResponseBody
    public Object callBack(HttpServletRequest request) throws Exception {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        log.info("【welink callback】接收到消息回调:{}", requestBody);
        weLinkIsvCallbackService.commandCallback(requestBody);

        WeLinkIsvCallbackRespDTO weLinkCallbackRespDto = new WeLinkIsvCallbackRespDTO();
        weLinkCallbackRespDto.setMsg("success");
        weLinkCallbackRespDto.setTimestamp(StringUtils.obj2str(System.currentTimeMillis() / 1000));
        return weLinkCallbackRespDto;
    }

    /**
     * 华为云市场回调
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/huaweicloudmarket")
    @ResponseBody
    public Object huaweicloudmarket(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> result = weLinkIsvCallbackService.marketCallback(request);
        String resultBody = JsonUtils.toJson(result);
        String signature = WeLinkIsvMarketEncryptUtils.generateResponseBodySignature(key, resultBody);
        String bodySign = "sign_type=\"HMAC-SHA256\", signature= \"" + signature + "\"";
        response.setHeader("Body-Sign", bodySign);
        return JsonUtils.toJson(result);
    }

}
