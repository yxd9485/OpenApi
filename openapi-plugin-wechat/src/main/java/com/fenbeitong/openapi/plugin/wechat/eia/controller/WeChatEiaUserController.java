package com.fenbeitong.openapi.plugin.wechat.eia.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResultEntity;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WechatResponseUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WechatResultEntity;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatAuthRespDTO;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatUserAuthService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信授权登录
 * Created by log.chang on 2020/3/2.
 */
@Controller
@Slf4j
@RequestMapping("/wechat/eia/auth")
public class WeChatEiaUserController {

    @Autowired
    private WeChatUserAuthService weChatUserAuthService;

    @RequestMapping()
    @ResponseBody
    public Object auth(@RequestParam String code, @RequestParam String corpId,@RequestParam Boolean sensitiveFlag) throws Exception {
        LoginResVO authInfo = weChatUserAuthService.auth(code, corpId, sensitiveFlag);
        return WechatResponseUtils.success(authInfo);
    }
}
