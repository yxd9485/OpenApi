package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yiduijie.dto.*;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

/**
 * <p>Title: YiDuiJieUserClient</p>
 * <p>Description: 易对接子账号管理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 4:57 PM
 */
@Component
public class YiDuiJieUserApi extends YiDuiJieBaseApi {

    public YiDuiJieAddUserResp addUser(String token, YiDuiJieAddUserReq req) {
        String result = postJson(yiDuijieRouter.getAddUserUrl(), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJieAddUserResp.class);
    }

    public YiDuiJieBaseResp updateUser(String userId, String token, YiDuiJieUpdateUserReq req) {
        String result = postJson(String.format(yiDuijieRouter.getUpdateUserUrl(), userId), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJieBaseResp.class);
    }

    public YiDuiJieQueryUserResp queryUser(String userId, String token) {
        String result = get(String.format(yiDuijieRouter.getQueryUserUrl(), userId), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieQueryUserResp.class);
    }

    public YiDuiJieQueryUserResp listUser(String token) {
        String result = get(yiDuijieRouter.getListUserUrl(), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieQueryUserResp.class);
    }

}
