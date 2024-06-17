package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieTokenReq;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieTokenResp;
import org.springframework.stereotype.Component;

/**
 * <p>Title: YiDuiJieTokenApi</p>
 * <p>Description: 易对接tokenl工具</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/11 2:21 PM
 */
@Component
public class YiDuiJieTokenApi extends YiDuiJieBaseApi {

    public YiDuiJieTokenResp getToken(YiDuiJieTokenReq tokenReq) {
        String result = httpUtils.postJson(yiDuijieRouter.getTokenUrl(), JsonUtils.toJson(tokenReq));
        return JsonUtils.toObj(result, YiDuiJieTokenResp.class);
    }

}
