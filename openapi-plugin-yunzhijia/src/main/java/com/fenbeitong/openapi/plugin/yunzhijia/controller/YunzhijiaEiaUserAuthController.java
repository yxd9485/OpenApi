package com.fenbeitong.openapi.plugin.yunzhijia.controller;/**
 * <p>Title: YunzhijiaEiaUserAuthController</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/4/30 2:08 下午
 */

import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaTicketService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaUserAuthService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lizhen
 * @date 2021/4/30
 */
@RestController
@RequestMapping("/yunzhijia/auth")
public class YunzhijiaEiaUserAuthController {

    @Autowired
    private IYunzhijiaTicketService yunzhijiaTicketService;

    @Autowired
    private IYunzhijiaUserAuthService yunzhijiaUserAuthService;

    @RequestMapping()
    public Object auth(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam String corpId, @RequestParam String appid, @RequestParam String ticket) throws Exception {
        LoginResVO loginResVO = yunzhijiaUserAuthService.userAuth(corpId, appid, ticket);
        return YunzhijiaResponseUtils.success(loginResVO);
    }

}
