package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceListReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceListRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetApprovalInstanceRespDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkApprovalInstanceService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * <p>Title: FxkApprovalInstanceServiceImpl</p>
 * <p>Description: 审批实例服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/26 4:29 PM
 */
@ServiceAspect
@Service
public class FxkApprovalInstanceServiceImpl implements IFxkApprovalInstanceService {

    @Value("${fenxiaoke.host}")
    private String fxiaokeHost;

    @Autowired
    private RestHttpUtils httpUtils;

    @Override
    public FxkGetApprovalInstanceRespDTO getInstance(FxkGetApprovalInstanceReqDTO req) {
        String result = httpUtils.postJson(fxiaokeHost+"/cgi/crm/approvalInstance/get", JsonUtils.toJson(req));
        return JsonUtils.toObj(result, FxkGetApprovalInstanceRespDTO.class);
    }

    @Override
    public FxkGetApprovalInstanceListRespDTO getInstanceList(FxkGetApprovalInstanceListReqDTO req) {
        String result = httpUtils.postJson(fxiaokeHost+"/cgi/crm/approvalInstances/query", JsonUtils.toJson(req));
        return JsonUtils.toObj(result, FxkGetApprovalInstanceListRespDTO.class);
    }
}
