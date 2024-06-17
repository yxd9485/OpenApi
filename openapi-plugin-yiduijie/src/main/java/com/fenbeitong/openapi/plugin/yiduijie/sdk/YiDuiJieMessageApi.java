package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieSendMessageReq;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieSendMessageResp;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>Title: YiDuiJieMessageApi</p>
 * <p>Description: 易对接消息管理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 4:57 PM
 */
@Component
public class YiDuiJieMessageApi extends YiDuiJieBaseApi {

    public YiDuiJieSendMessageResp sendMessage(String token, YiDuiJieSendMessageReq req) {
        ThreadUtils.sleep(1, TimeUnit.SECONDS);
        String result = postJson(yiDuijieRouter.getSendMessageUrl(), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJieSendMessageResp.class);
    }

}
