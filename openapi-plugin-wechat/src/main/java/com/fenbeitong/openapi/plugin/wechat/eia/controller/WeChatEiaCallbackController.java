package com.fenbeitong.openapi.plugin.wechat.eia.controller;

import com.fenbeitong.openapi.plugin.wechat.eia.entity.WeChatTokenKey;
import com.fenbeitong.openapi.plugin.wechat.eia.service.callback.WeChatEiaCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;


/**
 * Created by dave.hansins on 19/12/4.
 * 接收来自企业微信的消息回调数据
 */
@Controller
@Slf4j
@RequestMapping("/wechat/callback")
public class WeChatEiaCallbackController {

    @Autowired
    private WeChatEiaCallbackService weChatEiaCallbackService;

    //审批具体接收企业微信回调消息地址
    @RequestMapping("/receive")
    @ResponseBody
    private String createWeChatApply(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestParam("msg_signature") String signature,
                                   String timestamp,
                                   String nonce,
                                   String echostr) throws Exception {

        WeChatTokenKey weChatCorpInfo;
        // 正常的消息回调，验证地址有效性
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(echostr)) {//验证回调,获取token
            weChatCorpInfo = weChatEiaCallbackService.getWeChatCorpInfo(signature, timestamp, nonce, echostr);
            //获取公司相关鉴权信息
            if (ObjectUtils.isEmpty(weChatCorpInfo)) {
                log.info("企业微信信息鉴权失败，公司未注册");
                return null;
            }
            String sEchoStr = weChatEiaCallbackService.verify(weChatCorpInfo.getCorpId(), weChatCorpInfo.getCorpEncodingAesKey(),
                    weChatCorpInfo.getCorpToken(), signature, nonce, echostr, timestamp);
            log.info("返回echostr {}",sEchoStr);
            //返回企业微信验证地址有效性后的字符串
//            response.getWriter().write(sEchoStr);
            return sEchoStr;
        } else {//不为空则代表正常的消息回调
            //1.解析具体数据
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String toUserName = sb.toString().substring(26, sb.toString().lastIndexOf("ToUserName") - 5);
            log.info("获取的企业ID {}", toUserName);
            //2.解析企业ID
            weChatCorpInfo = weChatEiaCallbackService.getWeChatTokenInfoByExample(toUserName);

            //获取公司相关鉴权信息
            if (ObjectUtils.isEmpty(weChatCorpInfo)) {
                log.info("企业微信信息鉴权失败，公司未注册");
                return null;
            }
            weChatEiaCallbackService.callback(weChatCorpInfo.getCorpId(), weChatCorpInfo.getCorpEncodingAesKey(),
                    weChatCorpInfo.getCorpToken(), sb.toString(), signature, nonce, timestamp);
        }
        return "success";
    }
}
