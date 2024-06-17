package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieBaseResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListConfigResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListExtConfigResp;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>Title: YiDuiJieConfigApi</p>
 * <p>Description: 易对接配置管理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 4:57 PM
 */
@Component
public class YiDuiJieConfigApi extends YiDuiJieBaseApi {

    public YiDuiJieBaseResp setConfig(String token, String appInstanceId, Map req) {
        String result = postJson(String.format(yiDuijieRouter.getSetConfigUrl(), appInstanceId), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJieBaseResp.class);
    }

    public YiDuiJieListConfigResp listConfig(String token, String appInstanceId) {
        String result = get(String.format(yiDuijieRouter.getListConfigUrl(), appInstanceId), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieListConfigResp.class);
    }

    public YiDuiJieBaseResp setExtConfig(String token, String appInstanceId, String body) {
        String result = postText(String.format(yiDuijieRouter.getSetExtConfigUrl(), appInstanceId), token, body);
        return JsonUtils.toObj(result, YiDuiJieBaseResp.class);
    }

    public YiDuiJieListExtConfigResp listExtConfig(String token, String appInstanceId) {
        String result = get(String.format(yiDuijieRouter.getListExtConfigUrl(), appInstanceId), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieListExtConfigResp.class);
    }

}
