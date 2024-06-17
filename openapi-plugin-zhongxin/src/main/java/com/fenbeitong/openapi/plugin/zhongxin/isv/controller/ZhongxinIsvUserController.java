package com.fenbeitong.openapi.plugin.zhongxin.isv.controller;

import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.fenbeitong.openapi.plugin.zhongxin.common.exception.OpenApiZhongxinException;
import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinResultEntity;
import com.fenbeitong.openapi.plugin.zhongxin.isv.service.ZhongxinUserService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinResponseCode;
import com.fenbeitong.openapi.plugin.zhongxin.isv.util.ZhongxinResponseUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title:  ZhongxinIsvUserController</p>
 * <p>Description: 中信银行添加员工信息</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/19 下午6:24
 **/
@Controller
@Slf4j
@RequestMapping("/zhongxin/fala/isv/user")
public class ZhongxinIsvUserController {

    @Autowired
    private ZhongxinUserService zhongxinUserService;

    /**
     * 新增用户绑定银行
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/bind/company")
    @ResponseBody
    public ZhongxinResultEntity userAdd(HttpServletRequest request, HttpServletResponse response) {
        //用户主动点击添加链接，获取UC加密信息
        String encryptMsg = request.getParameter("encrypt");
        log.info("获取到的UC数据加密串为：{}", encryptMsg );
        try {
            zhongxinUserService.userAdd(encryptMsg);
            return ZhongxinResponseUtils.success("");
        } catch (OpenApiZhongxinException e) {
            log.warn("【中信银行】三方用户绑定公司出现业务异常", e);
            return ZhongxinResponseUtils.error(e.getCode(), e);
        } catch (Exception e) {
            log.error("【中信银行】三方用户绑定公司出现系统异常", e);
            return ZhongxinResponseUtils.error(ZhongxinResponseCode.SYSTEM_ERROR, "系统异常");
        }
    }

    /**
     * 获取企业名称
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getCompanyName")
    @ResponseBody
    public ZhongxinResultEntity getCompanyName(HttpServletRequest request, HttpServletResponse response) {
        //获取企业名称
        String encryptMsg = request.getParameter("encrypt");
        log.info("获取到的UC数据加密串为：{}", encryptMsg );
        try {
            String companyName = zhongxinUserService.getCompanyName(encryptMsg);
            return ZhongxinResponseUtils.success(companyName);
        } catch (OpenApiZhongxinException e) {
            log.warn("【中信银行】三方用户绑定公司出现业务异常", e);
            return ZhongxinResponseUtils.error(e.getCode(), e);
        } catch (Exception e) {
            log.error("【中信银行】三方用户绑定公司出现系统异常", e);
            return ZhongxinResponseUtils.error(ZhongxinResponseCode.SYSTEM_ERROR, "系统异常");
        }
    }

    /**
     * 中信用户登陆分贝通
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public Object userLogin(HttpServletRequest request, HttpServletResponse response) {
        //获取中信请求报文信息
        String hash = request.getParameter("hash");
        log.info("获取到的为:hash={}", hash);
        //根据请求信息判断后续页面跳转
        LoginResVO loginResVO;
        try {
            loginResVO = zhongxinUserService.authLogin(hash);
        } catch (Exception e) {
            log.error("【中信银行】用户登陆出现异常", e);
            throw new OpenApiZhongxinException(NumericUtils.obj2int(FuncResponseCode.FBT_WEB_APP_LOGIN_ERROR));
        }
        return ZhongxinResponseUtils.success(loginResVO);
    }
}
