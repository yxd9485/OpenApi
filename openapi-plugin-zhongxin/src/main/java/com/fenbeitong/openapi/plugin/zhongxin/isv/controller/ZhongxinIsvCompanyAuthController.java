package com.fenbeitong.openapi.plugin.zhongxin.isv.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fenbeitong.openapi.plugin.zhongxin.common.exception.OpenApiZhongxinException;
import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinResultEntity;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.ZhongxinCompanyAuthService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinResponseCode;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 中信企业授权相关接口
 */
@Controller
@Slf4j
@RequestMapping("/zhongxin/fala/isv/company")
public class ZhongxinIsvCompanyAuthController {

    @Autowired
    private ZhongxinCompanyAuthService zhongxinCompanyAuthService;

    /**
     * 企业授权进件
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/auth")
    @ResponseBody
    public ZhongxinResultEntity companyAuth(HttpServletRequest request, HttpServletResponse response) {
        //获取中信请求报文信息
        String encrypt = request.getParameter("encrypt");
        log.info("【中信银行】企业授权接口请求信息为：{}", encrypt);
        JSONObject data ;
        //根据请求信息判断后续页面跳转
        try {
            data = zhongxinCompanyAuthService.companyAuth(encrypt);
            return ZhongxinResponseUtils.success(data);
        } catch (OpenApiZhongxinException e) {
            log.warn("【中信银行】企业授权登陆出现业务异常", e);
            return ZhongxinResponseUtils.error(e.getCode(), e);
        } catch (Exception e) {
            log.warn("【中信银行】企业授权登陆出现系统异常", e);
            return ZhongxinResponseUtils.error(ZhongxinResponseCode.SYSTEM_ERROR, "系统异常");
        }
    }

    /**
     * 发送短信验证码
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/sendMsg")
    @ResponseBody
    public ZhongxinResultEntity sendMsg(HttpServletRequest request, HttpServletResponse response) {
        //获取页面请求报文信息
        String corpId = request.getParameter("corpId");
        String userId = request.getParameter("userId");
        log.info("页面请求发送短信验证，corpId={},userId={}", corpId, userId);
        //根据请求信息判断后续页面跳转
        try {
            zhongxinCompanyAuthService.getMsg(corpId, userId);
            return ZhongxinResponseUtils.success("");
        } catch (OpenApiZhongxinException e) {
            log.warn("【中信银行】发送验证码验证出现业务异常", e);
            return ZhongxinResponseUtils.error(e.getCode(), e);
        } catch (Exception e) {
            log.warn("【中信银行】发送验证码验证出现系统异常", e);
            return ZhongxinResponseUtils.error(ZhongxinResponseCode.SYSTEM_ERROR, "系统异常");
        }
    }

    /**
     * 短信验证码验证
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/verify")
    @ResponseBody
    public ZhongxinResultEntity verify(HttpServletRequest request, HttpServletResponse response) {
        //获取中信请求报文信息
        String corpId = request.getParameter("corpId");
        String userName = request.getParameter("userName");
        String verifyCode = request.getParameter("verifyCode");
        log.info("页面请求短信验证数据，corpId={},userName={},verifyCode={}", corpId, userName, verifyCode);
        //根据请求信息判断后续页面跳转
        try {
            String redirectUrl = zhongxinCompanyAuthService.verify(corpId, userName, verifyCode);
            return ZhongxinResponseUtils.success(redirectUrl);
        } catch (OpenApiZhongxinException e) {
            log.warn("【中信银行】短信验证码验证出现业务异常", e);
            return ZhongxinResponseUtils.error(e.getCode(), e);
        } catch (Exception e) {
            log.warn("【中信银行】短信验证码验证出现系统异常", e);
            return ZhongxinResponseUtils.error(ZhongxinResponseCode.SYSTEM_ERROR, "系统异常");
        }
    }
}
