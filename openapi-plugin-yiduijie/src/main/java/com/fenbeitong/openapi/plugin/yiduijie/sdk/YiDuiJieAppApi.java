package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yiduijie.dto.*;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

/**
 * <p>Title: YiDuiJieAppApi</p>
 * <p>Description: 易对接应用实例管理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:57 PM
 */
@Component
public class YiDuiJieAppApi extends YiDuiJieBaseApi {

    public YiDuiJieListMarketAppResp listMarketApp(String token) {
        String result = get(yiDuijieRouter.getListMarketAppUrl(), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieListMarketAppResp.class);
    }

    public YiDuiJieAddAppInstanceResp addAppInstance(String token, YiDuiJieAddAppInstanceReq req) {
        String result = postJson(yiDuijieRouter.getAddAppUrl(), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJieAddAppInstanceResp.class);
    }

    public YiDuiJieQueryAppInstanceResp queryAppInstance(String appId, String token) {
        String result = get(String.format(yiDuijieRouter.getQueryAppUrl(), appId), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieQueryAppInstanceResp.class);
    }

    public YiDuiJieListAppInstanceResp listAppInstance(String token) {
        String result = get(yiDuijieRouter.getListAppUrl(), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieListAppInstanceResp.class);
    }

}
