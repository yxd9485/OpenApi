package com.fenbeitong.openapi.plugin.wechat.eia.controller;

import com.fenbeitong.openapi.plugin.wechat.common.WechatResponseUtils;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatPullThirdOrgService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 拉取企业微信人员组织架构的定时任务
 * Created by Z.H.W on 20/02/18.
 */
@Controller
@Slf4j
@RequestMapping("/wechat/pullThirdOrg")
public class WeChatEiaPullThirdOrgController {

    @Autowired
    private WeChatPullThirdOrgService weChatPullThirdOrgService;

    @RequestMapping("/syncThird")
    @ResponseBody
    public Object syncThird(@RequestParam("corpId") String corpId, @RequestParam("deptId") String deptId) {
        weChatPullThirdOrgService.pullThirdOrg(corpId, deptId);
        return WechatResponseUtils.success(Maps.newHashMap());
    }

    @RequestMapping("/checkDepartment")
    @ResponseBody
    public Object checkDepartment(@RequestParam("corpId") String corpId) {
        Object result = weChatPullThirdOrgService.checkDepartment(corpId);
        return WechatResponseUtils.success(result);
    }

    @RequestMapping("/checkEmployee")
    @ResponseBody
    public Object checkEmployee(@RequestParam("corpId") String corpId) {
        Object result = weChatPullThirdOrgService.checkEmployee(corpId);
        return WechatResponseUtils.success(result);
    }

    /**
     * @Description 互联企业同步
     * @Author duhui
     * @Date 2022/1/14
     **/
    @RequestMapping("/unionOrgSync")
    @ResponseBody
    public Object unionOrgSync(@RequestParam("corpId") String corpId) {
        weChatPullThirdOrgService.pullUnionThirdOrg(corpId);
        return WechatResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 1 用于存量企业app切换企业微信嵌入版
     * 2 初始化企业微信三方id到UC
     *
     * @param corpId 三方企业id
     * @return result
     */
    @RequestMapping("/transferInitOrg")
    @ResponseBody
    public Object transferInitOrg(@RequestParam("corpId") String corpId) {
        weChatPullThirdOrgService.transferInitOrg(corpId);
        return WechatResponseUtils.success(Maps.newHashMap());
    }
}
