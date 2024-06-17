package com.fenbeitong.openapi.plugin.customize.shankun.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.shankun.service.IShangKunOrderCallbackService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: ShangKunOrderCallbackServiceImpl</p>
 * <p>Description: 上坤订单推送</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/8/26 6:58 PM
 */
@ServiceAspect
@Service
public class ShangKunOrderCallbackServiceImpl implements IShangKunOrderCallbackService {

    @Autowired
    private RestHttpUtils httpUtils;

    @Override
    public Object callback(String url, String data) {
        Map dataMap = JsonUtils.toObj(data, Map.class);
        String typeName = (String) dataMap.get("type_name");
        List<String> typeList = Lists.newArrayList("机票", "火车", "酒店");
        if (!typeList.contains(typeName)) {
            return OpenapiResponseUtils.success(Maps.newHashMap());
        }
        MultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add("jsonData", data);
        String result = httpUtils.postFormUrlEncode(url, null, multiValueMap);
        if (!ObjectUtils.isEmpty(result)) {
            result = result.substring(result.indexOf("{"), result.lastIndexOf("<"));
        }
        Map resultMap = JsonUtils.toObj(result, Map.class);
        Object code = resultMap == null ? null : MapUtils.getValueByExpress(resultMap, "data:code");
        if (code == null || NumericUtils.obj2int(code, 0) != 1) {
            throw new FinhubException(1, "上坤订单推送异常");
        }
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}
