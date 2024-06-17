package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJiePreviewVoucherResp;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>Title: YiDuiJieTransformApi</p>
 * <p>Description: 易对接数据转换</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:57 PM
 */
@Component
public class YiDuiJieTransformApi extends YiDuiJieBaseApi {

    public YiDuiJiePreviewVoucherResp transform(String token, String appInstanceId, String body) {
        Map<String, Object> req = Maps.newHashMap();
        req.put("body", body);
        String result = postJson(String.format(yiDuijieRouter.getTransformUrl(), appInstanceId), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJiePreviewVoucherResp.class);
    }

}
