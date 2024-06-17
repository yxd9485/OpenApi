package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yiduijie.dto.*;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

/**
 * <p>Title: YiDuiJieUserClient</p>
 * <p>Description: 易对接客户端管理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:57 PM
 */
@Component
public class YiDuiJieClientApi extends YiDuiJieBaseApi {

    public YiDuiJieAddClientResp addClient(String token, YiDuiJieAddClientReq req) {
        String result = postJson(yiDuijieRouter.getAddClientUrl(), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJieAddClientResp.class);
    }

    public YiDuiJieBaseResp updateClient(String clientId, String token, YiDuiJieUpdateClientReq req) {
        String result = postJson(String.format(yiDuijieRouter.getUpdateClientUrl(), clientId), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJieBaseResp.class);
    }

    public YiDuiJieQueryClientResp queryClient(String clientId, String token) {
        String result = get(String.format(yiDuijieRouter.getQueryClientUrl(), clientId), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieQueryClientResp.class);
    }

    public YiDuiJieListClientResp listClient(String token) {
        String result = get(yiDuijieRouter.getListClientUrl(), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieListClientResp.class);
    }

}
