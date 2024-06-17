package com.fenbeitong.openapi.plugin.customize.lishi.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.customize.lishi.service.ILiShiBillCallbackService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import com.lishi.financial.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Stub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: LiShiOrderCallbackServiceImpl</p>
 * <p>Description: 理士订单回传实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/11 11:53 AM
 */
@SuppressWarnings("unchecked")
@ServiceAspect
@Service
@Slf4j
public class LiShiBillCallbackServiceImpl implements ILiShiBillCallbackService {

    @Value("${lishi.host}")
    private String url;

    @Value("${lishi.username}")
    private String username;

    @Value("${lishi.password}")
    private String password;

    @Autowired
    private IEtlService etlService;

    @Autowired
    private RestHttpUtils httpUtils;

    @Override
    public Object callback(Long configId, String data, String userName, String password, String param) {
        List<Map<String, Object>> maps = JsonUtils.toObj(data, new TypeReference<List<Map<String, Object>>>() {
        });
        List<Map> transform = etlService.transform(configId, maps);
        try {
            if (transform != null && transform.size() > 0) {
                doCallBack(transform, userName, password, param);
            }
        } catch (ServiceException e) {
            log.error("push lishi bill occur error", e);
            throw new FinhubException(2, "理士账单推送异常");
        } catch (RemoteException e) {
            log.error("push lishi bill occur error", e);
            throw new FinhubException(3, "理士账单推送异常");
        } catch (OpenApiPluginException e) {
            log.error("push lishi bill occur yewu error", e);
            throw new FinhubException(3, "理士账单推送异常");
        }
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    private void doCallBack(List<Map> transform, String userName, String password, String param) throws ServiceException, WSInvokeException, RemoteException, OpenApiPluginException {
        EASLoginProxyServiceLocator loginloc = new EASLoginProxyServiceLocator();
        WSContext login = loginloc.getEASLogin().login(userName, password, "eas", param, "l2", 1);
        String jsonParams = JsonUtils.toJson(transform);
        WSReconciliationFacadeSrvProxyServiceLocator locator = new WSReconciliationFacadeSrvProxyServiceLocator();
        WSReconciliationFacadeSrvProxy proxyWs = locator.getWSReconciliationFacade();
        // 设置Header 关键步骤
        ((Stub) proxyWs).setHeader("http://login.webservice.bos.kingdee.com", "SessionId", login.getSessionId());
        log.info("push lishi param : " + jsonParams + " login sessionId: " + login.getSessionId());
        String result = proxyWs.createReconciliation(jsonParams);
        Map map = JsonUtils.toObj(result, Map.class);
        String bill_id = String.valueOf(transform.get(0).get("bill_id"));
        if (map != null && map.get("isSuccess") != null && "1".equals(String.valueOf(map.get("isSuccess")))) {
            log.info("push lishi success data id " + bill_id + " first data orderId " + transform.get(0).get("order_id") + " result : " + result);
            boolean success = loginloc.getEASLogin().logout(userName, "eas", param, "l2");
            if (success)
                log.info("logout from " + "" + " successed.");
            else
                log.info("logout from " + "" + " failed.");
        } else {
            log.info("push lishi fail data id " + bill_id + " first data orderId " + transform.get(0).get("order_id") + " result : " + result);
            throw new OpenApiPluginException(10000, bill_id, result);
        }

    }

    private String getToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        String address = String.format("%s/seeyon/rest/token/%s/%s", url, username, password);
        String result = httpUtils.get(address, headers, Maps.newHashMap());
        Map map = JsonUtils.toObj(result, Map.class);
        return ObjectUtils.isEmpty(map) ? null : (String) map.get("id");
    }
}
