package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceGetRequest;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiProcessInstanceService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.luastar.swift.base.json.JsonUtils;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author hwangsy
 * @date 2020/8/20 7:39 PM
 */
@Slf4j
@ServiceAspect
@Service
public class ApiProcessInstanceServiceImpl implements IApiProcessInstanceService {

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Override
    public OapiProcessinstanceGetResponse.ProcessInstanceTopVo getProcessInstance(String instanceId, String corpId) {
        log.info("调用钉钉审批实例详情接口，参数: instanceId: {}, corpId: {}", instanceId, corpId);
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/topapi/processinstance/get");
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
