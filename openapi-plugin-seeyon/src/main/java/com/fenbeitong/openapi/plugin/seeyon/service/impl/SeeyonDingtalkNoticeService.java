package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fenbeitong.openapi.plugin.seeyon.utils.TextMsg;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

@ServiceAspect
@Service
public class SeeyonDingtalkNoticeService {
    @Autowired
    RestHttpUtils restHttpUtils;

    public void sendDingtalkNotice(StringBuilder contents) {
        /* dingtalk 消息*/
        TextMsg textMsg =
                TextMsg.builder()
                        .text(TextMsg.TextBean.builder().content(contents.toString()).build())
                        .at(TextMsg.AtBean.builder().isAtAll(true).build())
                        .build();
        String url = "https://oapi.dingtalk.com/robot/send?access_token=c63605bb48cbd3e9e22f0dc267e7db1f7fd650c68d4ce3f58d027d2c53033b9b";
        String s = restHttpUtils.postJson(url, JsonUtils.toJsonSnake(textMsg));
    }
}
