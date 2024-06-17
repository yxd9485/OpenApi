package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieUpsertMappingResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieBaseResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListMappingResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieMappingReq;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>Title: YiDuiJieMappingApi</p>
 * <p>Description: 易对接映射</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 4:57 PM
 */
@Component
public class YiDuiJieMappingApi extends YiDuiJieBaseApi {

    public YiDuiJieBaseResp mappings(String token, String appInstanceId, String mappingType, YiDuiJieMappingReq req) {
        String result = postJson(String.format(yiDuijieRouter.getMappingUrl(), appInstanceId, mappingType), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJieBaseResp.class);
    }

    public YiDuiJieUpsertMappingResp upsertMappings(String token, String appInstanceId, String mappingType, YiDuiJieMappingReq req) {
        String result = postJson(String.format(yiDuijieRouter.getMappingUrl(), appInstanceId, mappingType), token, JsonUtils.toJson(req));
        return JsonUtils.toObj(result, YiDuiJieUpsertMappingResp.class);
    }

    public YiDuiJieBaseResp deleteMappings(String token, String appInstanceId, String mappingType, List<String> thirdMappingIdList) {
        String result = postJson(String.format(yiDuijieRouter.getDeleteMappingUrl(), appInstanceId, mappingType), token, JsonUtils.toJson(thirdMappingIdList));
        return JsonUtils.toObj(result, YiDuiJieBaseResp.class);
    }

    public YiDuiJieListMappingResp listMappings(String token, String appInstanceId, String mappingType, int pageIndex, int pageSize) {
        String result = get(String.format(yiDuijieRouter.getListMappingUrl(), appInstanceId, mappingType, pageIndex, pageSize), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieListMappingResp.class);
    }

}
