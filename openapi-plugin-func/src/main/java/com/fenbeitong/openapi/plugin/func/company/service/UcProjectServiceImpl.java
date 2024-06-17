package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.openapi.plugin.func.company.dto.FuncProjectDetailDTO;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * <p>Title: UcProjectServiceImpl</p>
 * <p>Description: 项目服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/17 4:37 PM
 */
@ServiceAspect
@Service
public class UcProjectServiceImpl {

    @Value("${host.usercenter}")
    private String userCenter;

    public FuncProjectDetailDTO queryFuncProjectDetail(String companyId, String thirdId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("companyId",companyId);
        param.put("thirdCostId",thirdId);
        String result = RestHttpUtils.get(userCenter + "/internal/third/project/center/detail", null, param);
        Map<String, Object> resultMap = JsonUtils.toObj(result, Map.class);
        return ObjectUtils.isEmpty(resultMap) ? null : JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(resultMap, "data")), FuncProjectDetailDTO.class);
    }

    public FuncProjectDetailDTO queryFuncProjectDetailById(String companyId, String id) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("companyId",companyId);
        param.put("id",id);
        String result = RestHttpUtils.get(userCenter + "/internal/third/project/center/detail", null, param);
        Map<String, Object> resultMap = JsonUtils.toObj(result, Map.class);
        return ObjectUtils.isEmpty(resultMap) ? null : JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(resultMap, "data")), FuncProjectDetailDTO.class);
    }
}
