package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.*;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkCustomDataService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: FxkCustomDataServiceImpl</p>
 * <p>Description: 纷享销客自定义表单服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/26 4:31 PM
 */
@ServiceAspect
@Service
public class FxkCustomDataServiceImpl implements IFxkCustomDataService {

    @Value("${fenxiaoke.host}")
    private String fxiaokeHost;

    @Override
    public FxkGetCustomDataRespDTO getCustomData(FxkGetCustomDataReqDTO req) {
        String result = RestHttpUtils.postJson(fxiaokeHost + "/cgi/crm/custom/data/get", JsonUtils.toJson(req));
        return JsonUtils.toObj(result, FxkGetCustomDataRespDTO.class);
    }

    @Override
    public FxkGetCustomCarApprovalRespDTO getCarCustomData(FxkGetCustomDataReqDTO req) {
        String result = RestHttpUtils.postJson(fxiaokeHost + "/cgi/crm/custom/data/get", JsonUtils.toJson(req));
        return JsonUtils.toObj(result, FxkGetCustomCarApprovalRespDTO.class);
    }

    @Override
    public FxkGetCustomTripApprovalRespDTO getTripCustomData(FxkGetCustomDataReqDTO req) {
        String result = RestHttpUtils.postJson(fxiaokeHost + "/cgi/crm/custom/data/get", JsonUtils.toJson(req));
        return JsonUtils.toObj(result, FxkGetCustomTripApprovalRespDTO.class);
    }

    @Override
    public FxkGetCustomDataListRespDTO getCustomDataList(FxkGetCustomDataListReqDTO req) {
        String result = RestHttpUtils.postJson(fxiaokeHost + "/cgi/crm/custom/data/query", JsonUtils.toJson(req));
        return JsonUtils.toObj(result, FxkGetCustomDataListRespDTO.class);
    }

    @Override
    public FxkGetCustomDataListRespDTO getCustomDataListV2(FxkGetCustomDataListReqDTO req) {
        String result = RestHttpUtils.postJson(fxiaokeHost + "/cgi/crm/custom/v2/data/query", JsonUtils.toJson(req));
        return JsonUtils.toObj(result, FxkGetCustomDataListRespDTO.class);
    }
}
