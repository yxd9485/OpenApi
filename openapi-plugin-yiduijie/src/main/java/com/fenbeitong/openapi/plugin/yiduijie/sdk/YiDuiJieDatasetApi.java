package com.fenbeitong.openapi.plugin.yiduijie.sdk;

import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.yiduijie.constant.MappingType;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieBaseResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListAccountResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListDepartmentResp;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

/**
 * <p>Title: YiDuiJieDatasetApi</p>
 * <p>Description: 易对接数据集管理</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/13 4:57 PM
 */
@Component
public class YiDuiJieDatasetApi extends YiDuiJieBaseApi {

    public YiDuiJieBaseResp upsertDataset(String token, String appInstanceId, String datasetName, String json) {
        String result = postJson(String.format(yiDuijieRouter.getAddDatasetUrl(), appInstanceId, datasetName), token, json);
        return JsonUtils.toObj(result, YiDuiJieBaseResp.class);
    }

    public YiDuiJieListAccountResp listAccountDataset(String token, String appInstanceId) {
        String result = get(String.format(yiDuijieRouter.getListDatasetUrl(), appInstanceId, MappingType.account.getValue()), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieListAccountResp.class);
    }

    public YiDuiJieListDepartmentResp listDeptDataset(String token, String appInstanceId) {
        String result = get(String.format(yiDuijieRouter.getListDatasetUrl(), appInstanceId, MappingType.department.getValue()), token, Maps.newHashMap());
        return JsonUtils.toObj(result, YiDuiJieListDepartmentResp.class);
    }

}
