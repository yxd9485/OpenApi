package com.fenbeitong.openapi.plugin.customize.lishi.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.lishi.dto.LiShiOrderCallBackResDTO;
import com.fenbeitong.openapi.plugin.customize.lishi.dto.LiShiOrderTransferDTO;
import com.fenbeitong.openapi.plugin.customize.lishi.service.ILiShiOrderCallbackService;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

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
public class LiShiOrderCallbackServiceImpl implements ILiShiOrderCallbackService {

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
    public Object callback(Long configId, String data) {
        Map dataMap = JsonUtils.toObj(data, Map.class);
        //1:机票;3:火车;4:酒店
        int type = NumericUtils.obj2int(dataMap.get("type"));
        //只处理机酒火
        if (!Lists.newArrayList(1, 3, 4).contains(type)) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        }
        String token = getToken();
        if (ObjectUtils.isEmpty(token)) {
            throw new FinhubException(1, "理士订单推送异常");
        }
        Map transformData = etlService.transform(configId, dataMap);
        LiShiOrderTransferDTO transferDto = JsonUtils.toObj(JsonUtils.toJson(transformData), LiShiOrderTransferDTO.class);
        doCallBack(token, transferDto.getData());
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    private void doCallBack(String token, Map data) {
        String method = "酒店".equals(data.get("type_name")) ? "hotelOrder" : "trafficOrder";
        String address = String.format("%s/seeyon/rest/dee/task/%s?token=%s", url, method, token);
        Map<String, Object> jsonData = Maps.newHashMap();
        jsonData.put("datas", Lists.newArrayList(data));
        String result = httpUtils.postJson(address, JsonUtils.toJson(jsonData));
        if (ObjectUtils.isEmpty(result) || !result.startsWith("<root>")) {
            throw new FinhubException(1, "理士订单推送异常");
        }
        LiShiOrderCallBackResDTO resDto = (LiShiOrderCallBackResDTO) XmlUtil.xml2Object(result, LiShiOrderCallBackResDTO.class);
        if (resDto == null || !resDto.success()) {
            throw new FinhubException(1, "理士订单推送异常");
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
