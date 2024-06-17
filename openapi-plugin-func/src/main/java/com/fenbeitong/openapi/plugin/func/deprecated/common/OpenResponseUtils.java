package com.fenbeitong.openapi.plugin.func.deprecated.common;

import com.fenbeitong.finhub.common.entity.ResponseResultEntity;
import com.fenbeitong.finhub.common.mask.utils.DataMaskUtils;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiRespDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import org.slf4j.MDC;

import java.util.Map;

/**
 * module: openapi-java项目<br/>
 * <p>
 * description: 封装公共响应示例<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/5 14:56
 * @since 2.0
 */
@SuppressWarnings("all")
@Deprecated
public class OpenResponseUtils {


    public static OpenResponseResultEntity success(Object data) {
        OpenResponseResultEntity result = new OpenResponseResultEntity<>();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setType(0);
        result.setMsg("success");
        DataMaskUtils.getAndCheckMaskObj(data);
        result.setData(data);
        return result;
    }

    public static OpenApiRespDTO successV2(Object data) {
        OpenApiRespDTO result = new OpenApiRespDTO<>();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(0);
        result.setMsg("success");
        DataMaskUtils.getAndCheckMaskObj(data);
        result.setData(data);
        return result;
    }

    public static OpenResponseResultEntity fail(String json) {
        Map<String,Object> map = JsonUtils.toObj(json,Map.class);
        OpenResponseResultEntity result = new OpenResponseResultEntity<>();
        result.setRequestId(MDC.get("requestId"));
        result.setCode(map.get("code") != null ? (Integer) map.get("code") : null);
        result.setMsg((map.get("msg") != null ? (String) map.get("msg") : null));
        result.setData(map.get("data"));
        return result;
    }
}
