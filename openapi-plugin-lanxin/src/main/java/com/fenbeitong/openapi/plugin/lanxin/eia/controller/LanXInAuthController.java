package com.fenbeitong.openapi.plugin.lanxin.eia.controller;

import com.fenbeitong.openapi.plugin.lanxin.eia.service.LanXinAuthService;
import com.fenbeitong.openapi.plugin.lanxin.util.LanXinResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * <p>Title: LanXInAuthController</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 4:26 下午
 */
@Slf4j
@RestController
@RequestMapping("/lanxin/auth")
public class LanXInAuthController {

    @Autowired
    LanXinAuthService lanXinAuthService;

    /**
     * @Description 蓝信用户免登入口
     * @Author duhui
     *
     * @Date 2021/12/8
     **/
    @RequestMapping("/getLoginUser")
    public Object getLoginUser(@RequestParam("corpId") String corpId, @RequestParam("authCode") String authCode) {
        log.info("蓝信用户登录请求：corpId: {}, authCode: {}", corpId, authCode);
        return LanXinResponseUtils.success(lanXinAuthService.getLoginUser(corpId, authCode));
    }
}
