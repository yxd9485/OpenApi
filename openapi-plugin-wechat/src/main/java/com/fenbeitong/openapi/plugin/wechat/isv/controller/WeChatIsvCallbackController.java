package com.fenbeitong.openapi.plugin.wechat.isv.controller;

import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
import com.fenbeitong.openapi.plugin.wechat.common.exception.AesException;
import com.fenbeitong.openapi.plugin.wechat.isv.aes.WXIsvBizMsgCrypt;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvCallbackBody;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvCompanyAuthDecryptBody;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvDataCallbackDecryptBody;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvSuiteTicketCallbackDecryptBody;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCallbackService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvUserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

import static com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant.WEB_AUTH_SKIP_URL;

/**
 * 企业微信ISV回调
 * Created by log.chang on 2020/2/27.
 */
@Controller
@Slf4j
@RequestMapping("/wechat/isv/callback")
public class WeChatIsvCallbackController {

    @Value("${host.fbtweb}")
    private String WEB_HOST;
    @Value("${wechat.isv.token}")
    private String token;
    @Value("${wechat.isv.encoding-aes-key}")
    private String encodingAesKey;
    @Value("${wechat.isv.corp-id}")
    private String corpId;

    @Autowired
    private WeChatIsvCallbackService weChatIsvCallbackService;
    @Autowired
    private WeChatIsvUserAuthService weChatIsvUserAuthService;

    @RequestMapping("/business")
    @ResponseBody
    private void businessCallback(HttpServletRequest request, HttpServletResponse response, @RequestParam("auth_code") String authCode) throws Exception {
        // 企业微信后台操作登录服务商后台先生成登录信息并缓存5Min，跳转到前端登录页并返回key，前端用key换取登录信息
        String token = weChatIsvUserAuthService.webLogin(authCode);
        response.sendRedirect(WEB_HOST + WEB_AUTH_SKIP_URL + token);
        //return WEB_HOST + WEB_AUTH_SKIP_URL + token;
    }

    @RequestMapping("/data")
    @ResponseBody
    private String dataCallback(HttpServletRequest request, @RequestParam("msg_signature") String signature,
                                String timestamp, String nonce, String echostr) throws Exception {
        try{
            //验证回调,获取token
            if (StringUtils.isNotBlank(echostr)) {
                WXIsvBizMsgCrypt wxcpt = WXIsvBizMsgCrypt.getInstance(corpId, encodingAesKey);
                return wxcpt.verifyURL(token, signature, timestamp, nonce, echostr);
            } else {
                String xmlBody = HttpServletRequestUtils.ReadAsChars(request);
                log.info("RequestBody is {}", xmlBody);
                WeChatIsvCallbackBody weChatIsvCallbackBody = (WeChatIsvCallbackBody) XmlUtil.xml2Object(xmlBody, WeChatIsvCallbackBody.class);
                WXIsvBizMsgCrypt wxcpt = WXIsvBizMsgCrypt.getInstance(weChatIsvCallbackBody.getToUserName(), encodingAesKey);
                log.info("decrypt msg is {}", wxcpt.decryptMsg(token, signature, timestamp, nonce, xmlBody));
                String decryptMsg = wxcpt.decryptMsg(token, signature, timestamp, nonce, xmlBody);
                WeChatIsvDataCallbackDecryptBody weChatIsvDataCallbackDecryptBody = (WeChatIsvDataCallbackDecryptBody) XmlUtil.xml2Object(decryptMsg, WeChatIsvDataCallbackDecryptBody.class);
                log.info("weChatIsvDataCallbackDecryptBody is {}", weChatIsvDataCallbackDecryptBody);
                return "success";
            }
        }catch (AesException e){
          log.warn("企业微信解密失败！");
        }
        return "success";
    }

    @RequestMapping("/command")
    @ResponseBody
    private String commandCallback(HttpServletRequest request, @RequestParam("msg_signature") String signature,
                                   String timestamp, String nonce, String echostr) throws Exception {
        try{
            //验证回调,获取token
            if (StringUtils.isNotBlank(echostr)) {
                WXIsvBizMsgCrypt wxcpt = WXIsvBizMsgCrypt.getInstance(corpId, encodingAesKey);
                return wxcpt.verifyURL(token, signature, timestamp, nonce, echostr);
            } else {
                String xmlBody = HttpServletRequestUtils.ReadAsChars(request);
                log.info("RequestBody is {}", xmlBody);
                CompletableFuture.runAsync(() -> weChatIsvCallbackService.commandCallback(signature, timestamp, nonce, xmlBody));
                return "success";
            }
        }catch (AesException e){
            log.warn("企业微信解密失败！");
        }
        return "success";
    }

    public static void main(String[] args) throws Exception {
        //String receiveId = "ww9c195a1a54433029";
        //WXIsvBizMsgCrypt wxcpt = WXIsvBizMsgCrypt.getInstance(receiveId);
        //String msgSignature = "c4c243fb861880086b2838ef5e1f62a4a5c4bbc9";
        //String timestamp = "1582797641";
        //String nonce = "1583250493";
        //String echostr = "/mqzJyeBTWAKWbH5531J0v5qMWxeSV3lJv+0ycNR/hvel5jcHW586jhNh1zxZMvJiS6KHW7KhBIUXxPYXe+d7w==";
        //String text = "<xml><ToUserName><![CDATA[ww9c195a1a54433029]]></ToUserName><Encrypt><![CDATA[lZEch/6aWnwMYEWMtQQT2nt6wCiXgK6wxTYontBycnjJYPtiTuXb7F+cbzf096YU19xYOJgcfpFMpjVNL5XugESb5736vEtYNbg/xA/O3LWDsPstlrYdPtkiPnM92Sd1ssKymPsbW4TqbpESqeGmDC0Hmaw/2HSvgD9RGjNickvZ3N6AlLtANNf8GVNI67lTYDFIgGCRlaRK8z9s5MEFX2/zKykmaGvK8D+usbozTorHbbiy6XMlpxAOi/YlljC0155+aCXfFxzqPUTsjf3MhK4XCiZk1IZxghcFlXIvNBvFU2Mo5lvhg7mqrSIpqV8CcRv6E7WJ+ZZrciEnZsi8ErZr28lF6DIPv9xKBibbXnUXH59i22ii/jZ3/vrhjyRy]]></Encrypt><AgentID><![CDATA[]]></AgentID></xml>";
        //String msgSignature = "115c802efcfa207e7321946a1aa3b9ed8200e9c8";
        //String timestamp = "1583995363";
        //String nonce = "1584393397";
        //System.err.println(wxcpt.decryptMsg(msgSignature, timestamp, nonce, text));
//        String text = "<xml><SuiteId><![CDATA[ww4asffe99e54c0fxxxx]]></SuiteId><InfoType><![CDATA[suite_ticket]]></InfoType><TimeStamp>1403610513</TimeStamp><SuiteTicket><![CDATA[asdfasfdasdfasdf]]></SuiteTicket></xml>";
        String text = "<xml><SuiteId><![CDATA[ww4asffe9xxx4c0f4c]]></SuiteId><AuthCode><![CDATA[AUTHCODE]]></AuthCode><InfoType><![CDATA[create_auth]]></InfoType><TimeStamp>1403610513</TimeStamp></xml>";
        if (text.contains("<InfoType><![CDATA[suite_ticket]]></InfoType>")) {
            WeChatIsvSuiteTicketCallbackDecryptBody weChatIsvCommandCallbackBody = (WeChatIsvSuiteTicketCallbackDecryptBody) XmlUtil.xml2Object(text, WeChatIsvSuiteTicketCallbackDecryptBody.class);
            System.err.println(weChatIsvCommandCallbackBody.getSuiteTicket());
        } else if (text.contains("<InfoType><![CDATA[create_auth]]></InfoType>")) {
            WeChatIsvCompanyAuthDecryptBody companyAuthRequest = (WeChatIsvCompanyAuthDecryptBody) XmlUtil.xml2Object(text, WeChatIsvCompanyAuthDecryptBody.class);
            System.out.println(companyAuthRequest.getAuthCode());
        }

    }

}
