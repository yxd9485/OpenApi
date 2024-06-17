package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceGetRequest;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiProcessInstanceService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IApiIsvProcessInstanceService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.luastar.swift.base.json.JsonUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: ApiProcessInstanceServiceImpl</p>
 * <p>Description: 钉钉审批实例服务接口</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/4/07 7:39 PM
 */
@Slf4j
@ServiceAspect
@Service
public class ApiIsvProcessInstanceServiceImpl implements IApiIsvProcessInstanceService {

    @Autowired
    DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Override
    public OapiProcessinstanceGetResponse.ProcessInstanceTopVo getProcessInstance(String instanceId, String corpId) {
        log.info("调用钉钉审批实例详情接口，参数: instanceId: {}, corpId: {}", instanceId, corpId);
        String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId(corpId);
        DingTalkClient client = new DefaultDingTalkClient(dingtalkHost + "/topapi/processinstance/get");
        OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
        request.setProcessInstanceId(instanceId);
        try {
            OapiProcessinstanceGetResponse response = client.execute(request, accessToken);
            log.info("调用钉钉审批实例详情接口完成，返回结果: {}", getFormatJson(response));
            if (response.isSuccess()) {
                return response.getProcessInstance();
            }
            throw new OpenApiArgumentException(response.getErrmsg());
        } catch (ApiException e) {
            log.info("获取钉钉审批实例详情异常", e);
            throw new OpenApiArgumentException(e.getErrMsg());
        }
    }

    /**
     * 格式化返回结果
     *
     * @param response
     * @return
     */
    private String getFormatJson(OapiProcessinstanceGetResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("errcode", response.getErrcode());
        map.put("msg", response.getMsg());
        map.put("success", response.isSuccess());
        if (response.isSuccess()) {
            BeanMap instance = BeanMap.create(response.getProcessInstance());
            Map<String, Object> instanceMap = new HashMap<>(10);
            for (Object key : instance.keySet()) {
                if (!"formComponentValues".equals(key)) {
                    instanceMap.put(String.valueOf(key), instance.get(key));
                }
            }
            map.put("processInstance", instanceMap);
        }
        return JsonUtils.toJson(map);
    }
}
