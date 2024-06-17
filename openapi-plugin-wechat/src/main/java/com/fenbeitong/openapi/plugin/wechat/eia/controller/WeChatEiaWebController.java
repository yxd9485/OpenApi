package com.fenbeitong.openapi.plugin.wechat.eia.controller;

import com.fenbeitong.openapi.plugin.wechat.common.WechatResponseUtils;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.WeChatEiaUpdatePhoneRequest;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatWebService;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * web前端业务
 * Created by xiaohai on 2021/11/30.
 */
@Controller
@Slf4j
@RequestMapping("/wechat/eia/web")
public class WeChatEiaWebController {

    @Autowired
    private WeChatWebService weChatWebService;

    /**
     * 获取企业Jsapi签名
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/getEnterpriseJsapiSign")
    @ResponseBody
    public Object getEnterpriseJsapiSign(HttpServletRequest request, HttpServletResponse response,
                                         @Valid JsapiSignRequest jsapiSignRequest) throws Exception {
        WeChatIsvJsapiSignResponse enterpriseJsapiSign = weChatWebService.getEnterpriseJsapiSign(jsapiSignRequest.getCompanyId(), jsapiSignRequest.getData());
        return WechatResponseUtils.success(enterpriseJsapiSign);
    }

    /**
     * 获取应用Jsapi签名
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/getAgentJsapiSign")
    @ResponseBody
    public Object getAgentJsapiSign(HttpServletRequest request, HttpServletResponse response,
                                         @Valid JsapiSignRequest jsapiSignRequest) throws Exception {
        if ("INVOICE".equals(jsapiSignRequest.getData())) {
             WeChatIsvJsapiSignResponse invoiceSign = weChatWebService.getAgentJsapiSignInvoice(jsapiSignRequest.getCompanyId(), jsapiSignRequest.getData());
            return WechatResponseUtils.success(invoiceSign);
        }
        WeChatIsvJsapiSignResponse enterpriseJsapiSign = weChatWebService.getAgentJsapiSign(jsapiSignRequest.getCompanyId(), jsapiSignRequest.getData());
        return WechatResponseUtils.success(enterpriseJsapiSign);
    }

    /**
     * 1 根据临时授权码获取用户敏感信息
     * 2 更新员工手机号
     *
     * @param updatePhoneRequest 更新手机号请求
     */
    @RequestMapping("/updateUserPhone")
    @ResponseBody
    public Object updateUserPhone(@RequestBody @Valid WeChatEiaUpdatePhoneRequest updatePhoneRequest) {
        weChatWebService.updateUserPhone(updatePhoneRequest.getCorpId(),updatePhoneRequest.getTempCode());
        return WechatResponseUtils.success("更新手机号成功");
    }



}
